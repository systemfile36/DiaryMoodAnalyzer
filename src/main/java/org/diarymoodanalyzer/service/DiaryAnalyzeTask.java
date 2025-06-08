package org.diarymoodanalyzer.service;

import lombok.Getter;
import org.diarymoodanalyzer.domain.Diary;

/**
 * Task object for {@link DiaryAnalyzeService DiaryAnalyzeSevice}.
 * Used for rate-limited asynchronous request to AI server.
 */
@Getter
public class DiaryAnalyzeTask {

    /**
     * <code>id</code> of {@link org.diarymoodanalyzer.domain.Diary Diary} entity.
     * Used for specify Diary to analyze
     */
    private final Long diaryId;

    /**
     * <code>content</code> of {@link org.diarymoodanalyzer.domain.Diary Diary} entity.
     */
    private final String content;

    /**
     * Retry count. It will be increased when task failed
     */
    private int retryCount = 0;

    public DiaryAnalyzeTask(Diary diary) { this.diaryId = diary.getId(); this.content = diary.getContent(); }

    public DiaryAnalyzeTask(Long id, String content) { this.diaryId = id; this.content = content; }

    /**
     * Increase <code>retryCount</code> by 1.
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * Check retry count of this task is exceed max value
     * @param maxRetryCount Max retry count
     * @return true if <code>retryCount</code> greater than <code>maxRetryCount</code>, otherwise false
     */
    public boolean isRetryCountExceeded(int maxRetryCount) {
        return this.retryCount >= maxRetryCount;
    }

    @Override
    public String toString() {
        return getClass().getName() + " Diary id : "
                + diaryId.toString() + " content " + content;
    }
}
