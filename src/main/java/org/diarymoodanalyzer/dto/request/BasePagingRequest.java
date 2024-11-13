package org.diarymoodanalyzer.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * 페이징 관련 필드와 메소드들을 정의한 추상 클래스
 */
@NoArgsConstructor
@Getter
@Setter
public abstract class BasePagingRequest {
    //페이지의 인덱스와 사이즈
    private int page;
    private int size;

    //정렬 기준이 될 필드와 오름차순 여부. 기본값 : 생성 시간 기준 역순
    private String sortBy = "createdAt";
    private boolean isAscending = false;

    /**
     * 필드의 값들로 Sort를 구성해서 반환
     * @return 정렬 기준
     */
    public Sort getSort() {
        //정렬 기준을 Sort로 구성
        return isAscending ? Sort.by(sortBy) : Sort.by(sortBy).descending();
    }

    /**
     * 필드의 값들을 통해 Pageable 구성해서 반환
     * @return 필드의 값으로 구성된 Pageable 객체
     */
    public Pageable getPageable() {
        //페이지 번호와 사이즈, 정렬 기준을 설정
        return PageRequest.of(page, size, getSort());
    }
}
