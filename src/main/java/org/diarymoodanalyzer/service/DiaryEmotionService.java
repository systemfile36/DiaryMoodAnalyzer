package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
import org.diarymoodanalyzer.client.DiaryEmotionClient;
import org.diarymoodanalyzer.domain.DepressionLevel;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.dto.ai.request.DiaryEmotionRequest;
import org.diarymoodanalyzer.dto.ai.response.DiaryEmotionResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Diary의 감정 분석을 관리하는 서비스 클래스
 * 내부의 BlockingQueue를 사용해 AI 서버에 보내는 요청을 관리한다.
 */
@Service
public class DiaryEmotionService {

    private final BlockingQueue<DiaryEmotionTask> taskQueue = new LinkedBlockingQueue<>();

    private final DiaryEmotionClient client;

    private final DiaryRepository diaryRepository;

    /**
     * 최대 재시도 횟수
     */
    private static final int maxRetryCount = 3;


    //생성자로 빈 주입
    public DiaryEmotionService(DiaryEmotionClient client, DiaryRepository diaryRepository) {
        this.client = client;
        this.diaryRepository = diaryRepository;
    }

    /**
     * 요청한 작업을 queue에 삽입한다.
     * @param task 요청할 작업
     */
    public void submitTask(DiaryEmotionTask task) {
        if(!taskQueue.offer(task)) {
            //캐퍼시티 초과로 삽입에 실패하면 에러 로그 찍음'
            //임시로 err 입출력 사용
            System.err.println("taskQueue is full. drop task : " + task);
        }
    }


    /**
     * 일정 주기로 작업을 Queue에서 꺼내어서 처리한다.
     * "@Scheduled" 사용
     */
    @Scheduled(fixedRate = 1000) //1초마다 실행
    public void processQueue() {
        //Queue에서 작업 꺼냄
        DiaryEmotionTask task = taskQueue.poll();

        if(task != null) {
            //꺼낸 작업을 실행 (비동기)
            processTask(task);
        }
    }

    /**
     * 작업을 비동기로 처리한다. 예외 발생 시 handleTaskFailure를 호출하여 복구를 시도한다.
     * @param task 요청 작업
     */
    @Async
    public void processTask(DiaryEmotionTask task) {
        try {
            System.out.println("Processing : " + task);

            //요청 DTO를 구성한다.
            DiaryEmotionRequest req = new DiaryEmotionRequest();
            req.setDiary(task.getContent());

            //client를 통해 AI 서버에 요청을 보낸다.
            DiaryEmotionResponse res = client.sendRequest(req);

            //결과를 저장한다.
            saveResult(task, res);

        } catch(Exception e) {
            //오류 발생 시 복구를 시도한다.
            handleTaskFailure(task, e);
        }
    }

    /**
     * 작업 실행 중 예외 발생 시, 재시도 횟수를 늘리고 다시 Queue에 넣는다.
     * 재시도 횟수가 초과하면 해당 작업을 drop 하고 해당 Diary의 측정 결과를 DepressionLevel.ERROR로 설정한다.
     * @param task 요청한 작업
     * @param e 발생한 예외
     */
    private void handleTaskFailure(DiaryEmotionTask task, Exception e) {
        //재시도 횟수를 늘린다.
        task.incrementRetryCount();

        //재시도 횟수가 초과되지 않았다면 다시 Queue에 넣는다. 초과되면 drop 한다.
        if(!task.isRetryCountExceeded(maxRetryCount)) {
            taskQueue.offer(task);
        } else {
            //임시로 err 사용
            System.err.println(
                    "Task exceeded max retry count : " + task + "\n" + e);

            DiaryEmotionResponse res = new DiaryEmotionResponse();

            //재시도 횟수 초과 시, 분석 결과 수치를 오류 값으로 설정한다. (-2)
            res.setOverall_average_weight(DepressionLevel.ERROR.getValue());
            saveResult(task, res);
        }
    }

    /**
     * 감정 분석 결과를 DB에 반영한다.
     * @param task 저장할 작업
     * @param res AI 서버로 부터 받은 결과
     */
    @Transactional
    private void saveResult(DiaryEmotionTask task, DiaryEmotionResponse res) {
        //설정할 다이어리를 불러옴
        Diary diary = diaryRepository.findById(task.getDiaryId())
                .orElseThrow(()->new IllegalArgumentException("There is no diary : " + task.getDiaryId()));

        //엔티티 수정함 (변경 감지)
        diary.setDepressionLevel((byte)res.getOverall_average_weight());

        diaryRepository.save(diary);
    }
}
