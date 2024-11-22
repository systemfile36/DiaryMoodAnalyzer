package org.diarymoodanalyzer.service;

import lombok.Getter;
import org.diarymoodanalyzer.domain.Diary;

/**
 * 외부에서 Diary 감정 분석을 요청할 때 사용할 태스크 객체
 */
@Getter
public class DiaryEmotionTask {
    /**
     * 분석할 Diary id
     */
    private final Long diaryId;

    /**
     * 분석할 Diary 본문
     */
    private final String content;

    /**
     * 재시도 횟수
     */
    private int retryCount = 0;

    public DiaryEmotionTask(Diary diary) {
        this(diary.getId(), diary.getContent());
    }

    public DiaryEmotionTask(Long diaryId, String content) {
        this.diaryId = diaryId; this.content = content;
    }

    /**
     * 재시도 횟수를 1 늘린다.
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }

    /**
     * 재시도 횟수가 최대 횟수를 초과하였는지 여부를 반환한다.
     * @param maxRetryCount 최대 재시도 횟수
     * @return 재시도 횟수가 maxRetryCount 보다 크거나 같으면 true, otherwise, false
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
