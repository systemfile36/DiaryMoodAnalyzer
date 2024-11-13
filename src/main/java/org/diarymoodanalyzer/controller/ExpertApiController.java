package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.dto.response.GetManagedUserResponse;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('EXPERT')")
public class ExpertApiController {

    private final ExpertRepository expertRepository;

    /**
     * 현재 인증된 전문가가 관리하는 사용자 목록 반환
     * @return 관리하는 사용자 목록
     */
    @GetMapping("/expert/managedUsers")
    public ResponseEntity<List<GetManagedUserResponse>> getManagedUsers() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail();
        Expert expert = expertRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("not found Expert " + currentUserEmail));

        //응답 DTO의 배열 형태로 반환
        return ResponseEntity.ok(expert.getManagedUsers().stream().
                map((value) -> new GetManagedUserResponse(value.getEmail())).toList());
    }
}
