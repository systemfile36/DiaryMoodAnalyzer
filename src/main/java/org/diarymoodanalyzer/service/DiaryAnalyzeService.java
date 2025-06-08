package org.diarymoodanalyzer.service;

import org.diarymoodanalyzer.client.DiaryAnalyzeClient;
import org.diarymoodanalyzer.client.DiaryEmotionClient;
import org.diarymoodanalyzer.dto.ai.request.DiaryAnalyzeRequest;
import org.diarymoodanalyzer.dto.ai.request.DiaryEmotionRequest;
import org.diarymoodanalyzer.dto.ai.response.DiaryAnalyzeResponse;
import org.diarymoodanalyzer.dto.ai.response.DiaryEmotionResponse;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Service class for analyze {@link org.diarymoodanalyzer.domain.Diary Diary} asynchronously.
 * <br/>
 * Limit request rate by scheduled polling to reduce load on the AI server
 */
@Service
public class DiaryAnalyzeService {
    private final BlockingQueue<DiaryAnalyzeTask> taskQueue = new LinkedBlockingQueue<>();

    private final DiaryAnalyzeClient client;

    private final DiaryRepository diaryRepository;

    // Will be injected by Spring container
    public DiaryAnalyzeService(DiaryAnalyzeClient client, DiaryRepository diaryRepository) {
        this.client = client;
        this.diaryRepository = diaryRepository;
    }

    /**
     * Offer task to queue.
     * @param task 요청할 작업
     */
    public void submitTask(DiaryAnalyzeTask task) {
        if(!taskQueue.offer(task)) {
            // Will be replaced to Logger
            System.err.println("taskQueue is full. drop task : " + task);
        }
    }

    /**
     * Process task from queue asynchronously. <br/>
     * Using {@link Scheduled Scheduled} with <code>fixedRate</code> to limit request rate.
     */
    @Scheduled(fixedRate = 1000)
    public void processQueue() {
        // pop from queue
        DiaryAnalyzeTask task = taskQueue.poll();

        if(task != null) {
            // process asynchronously
            processTask(task);
        }
    }

    /**
     * 작업을 비동기로 처리한다. 예외 발생 시 handleTaskFailure를 호출하여 복구를 시도한다.
     * @param task 요청 작업
     */
    @Async
    public void processTask(DiaryAnalyzeTask task) {
        try {
            // Will be replaced to Logger
            System.out.println("Processing : " + task);

            //요청 DTO를 구성한다.
            DiaryAnalyzeRequest req = new DiaryAnalyzeRequest();
            req.setDiaryContent(task.getContent());

            //client를 통해 AI 서버에 요청을 보낸다.
            DiaryAnalyzeResponse res = client.sendRequest(req);

            //결과를 저장한다.
            saveResult(task, res);

        } catch(Exception e) {
            //오류 발생 시 복구를 시도한다.
            handleTaskFailure(task, e);
        }
    }


}
