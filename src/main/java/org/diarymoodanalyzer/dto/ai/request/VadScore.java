package org.diarymoodanalyzer.dto.ai.request;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Embeddable class to indicate VAD score of analyze result.
 * <br/>
 * This will be embedded to {@link org.diarymoodanalyzer.domain.Diary Diary} entity.
 */
@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VadScore {
    @Column(nullable = true)
    private float v;
    @Column(nullable = true)
    private float a;
    @Column(nullable = true)
    private float d;
}
