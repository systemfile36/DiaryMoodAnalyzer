package org.diarymoodanalyzer.dto.request;

import lombok.Getter;

/**
 * Expert가 자신이 관리하는 사용자의 다이어리 목록을 페이지로 요청할 때 사용하는 DTO
 *
 *
 */
@Getter
public class GetDiaryOfManagedUserByPageRequest extends BasePagingRequest {
    /**
     * 조회할 다이어리들의 주인
     */
    private String ownerEmail;
}
