package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
import org.diarymoodanalyzer.annotation.SkipLogging;
import org.diarymoodanalyzer.client.DiaryAnalyzeClient;
import org.diarymoodanalyzer.client.DiaryEmotionClient;
import org.diarymoodanalyzer.domain.DepressionLevel;
import org.diarymoodanalyzer.domain.Diary;
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
 * Limit request rate by scheduled polling to reduce load on the AI server. <br/>
 * This class created for replace <code>DiaryEmotionService</code> (already deprecated)
 */
@SkipLogging
@Service
public class DiaryAnalyzeService {
    private final BlockingQueue<DiaryAnalyzeTask> taskQueue = new LinkedBlockingQueue<>();

    private final DiaryAnalyzeClient client;

    private final DiaryRepository diaryRepository;

    /**
     * Max retry count of task
     */
    private static final int maxRetryCount = 3;

    // Will be injected by Spring container
    public DiaryAnalyzeService(DiaryAnalyzeClient client, DiaryRepository diaryRepository) {
        this.client = client;
        this.diaryRepository = diaryRepository;
    }

    /**
     * Offer task to queue.
     * @param task task to offer
     */
    public void submitTask(DiaryAnalyzeTask task) {
        if(!taskQueue.offer(task)) {
            // Will be replaced to Logger
            System.err.println("taskQueue is full. drop task : " + task);
        }
    }

    /**
     * Get task from queue and process it asynchronously.
     * <br/>
     * Using {@link Scheduled Scheduled} with <code>fixedDelay</code> to limit request rate.
     */
    @Scheduled(fixedDelay = 200) // Use fixed delay to limit request rate
    public void processQueue() {
        // pop from queue
        DiaryAnalyzeTask task = taskQueue.poll();

        if(task != null) {
            // process asynchronously
            processTask(task);
        }
    }

    /**
     * Process task asynchronously.
     * <br/>
     * If the task throw exception, try recovery by calling <code>handleTaskFailure</code>
     * @param task task to process
     */
    @Async
    public void processTask(DiaryAnalyzeTask task) {
        try {
            // Will be replaced to Logger
            System.out.println("Processing : " + task);

            // Create request DTO instance
            DiaryAnalyzeRequest req = new DiaryAnalyzeRequest();
            req.setDiaryContent(task.getContent());

            // Send request using WebClient
            DiaryAnalyzeResponse res = client.sendRequest(req);

            // Apply result to DB by calling `saveResult`
            saveResult(task, res);

        } catch(Exception e) {
            // Try to recovery
            handleTaskFailure(task, e);
        }
    }

    /**
     * Increase retry count and submit task again.<br/>
     * If retry count of the task exceed max value, drop it and save as failure.
     * @param task task to be saved
     * @param e Exception thrown during the task
     */
    private void handleTaskFailure(DiaryAnalyzeTask task, Exception e) {
        // Increase retry count
        task.incrementRetryCount();

        // If retry count of the task does not exceed max value,
        // insert it to queue again
        if(!task.isRetryCountExceeded(maxRetryCount)) {
            submitTask(task);
        } else {
            // Will be replaced to Logger
            System.err.println(
                    "Task exceeded max retry count : " + task + "\n" + e);

            // Save as failure
            saveResultAsFailure(task);
        }
    }

    /**
     * Save result of analyze to DB
     * @param task task to save
     * @param res Response DTO from AI server
     */
    @Transactional
    private void saveResult(DiaryAnalyzeTask task, DiaryAnalyzeResponse res) {
        Diary diary = diaryRepository.findById(task.getDiaryId())
                .orElseThrow(()->new IllegalArgumentException("There is no diary : " + task.getDiaryId()));

        // Update entity and save (dirty checking)
        diary.setAnalyzeResult(res);
        diaryRepository.save(diary);
    }

    /**
     * Save result of analyze to DB as failure.
     * @param task task to save
     */
    private void saveResultAsFailure(DiaryAnalyzeTask task) {
        Diary diary = diaryRepository.findById(task.getDiaryId())
                .orElseThrow(()->new IllegalArgumentException("There is no diary : " + task.getDiaryId()));

        // Update entity and save (dirty checking)
        diary.setAnalyzeAsFail();
        diaryRepository.save(diary);
    }
}
