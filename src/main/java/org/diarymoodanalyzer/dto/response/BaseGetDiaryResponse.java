package org.diarymoodanalyzer.dto.response;

import lombok.*;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.dto.ai.request.VadScore;

import java.time.LocalDateTime;

/**
 * Abstract class for response DTO contain info of {@link org.diarymoodanalyzer.domain.Diary} entity.
 * <br/>
 * This class is base class of response DTO that return Diary entity
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseGetDiaryResponse {
    private Long id;
    private String title;
    private byte depressionLevel;

    // About analyze results
    private VadScore vadScore;
    private int depressionScore;
    private String classification;

    /**
     * Flag to indicate analyze was successfully completed
     */
    private boolean isAnalyzeSuccess = false;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String userEmail;

    /**
     * Set fields from {@link Diary} entity.
     * @param diary {@link Diary} entity.
     */
    public BaseGetDiaryResponse(Diary diary) {
        this.id = diary.getId(); this.title = diary.getTitle();
        this.depressionLevel = diary.getDepressionLevel();
        this.vadScore = diary.getVadScore();
        this.depressionScore = diary.getDepressionScore();
        this.classification = diary.getClassification();
        this.createdAt = diary.getCreatedAt();
        this.updatedAt = diary.getUpdatedAt();
        this.userEmail = diary.getUser().getEmail();

        //Check analyze was success
        this.isAnalyzeSuccess = diary.isAnalyzeSuccess();
    }

}
