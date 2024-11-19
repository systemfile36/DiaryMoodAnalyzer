package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageForExpertRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryByPageRequest;
import org.diarymoodanalyzer.dto.request.GetDiaryForExpertRequest;
import org.diarymoodanalyzer.dto.response.GetCommentByDiaryIdResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByIdResponse;
import org.diarymoodanalyzer.dto.response.GetDiaryByPageResponse;
import org.diarymoodanalyzer.dto.response.GetManagedUserResponse;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.service.CommentService;
import org.diarymoodanalyzer.service.DiaryService;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController
@PreAuthorize("hasRole('EXPERT')") //EXPERT만 접근 가능
public class ExpertApiController {

    private final ExpertRepository expertRepository;

    private final UserRepository userRepository;

    private final DiaryService diaryService;

    private final CommentService commentService;

    /**
     * 현재 인증된 전문가가 관리하는 사용자 목록 반환
     * @return 관리하는 사용자 목록
     */
    @GetMapping("/api/expert/managedUsers")
    public ResponseEntity<List<GetManagedUserResponse>> getManagedUsers() {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN ,"Invalid Authentication"));

        Expert expert = expertRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new IllegalArgumentException("not found Expert " + currentUserEmail));

        //응답 DTO의 배열 형태로 반환
        return ResponseEntity.ok(expert.getManagedUsers().stream().
                map((value) -> new GetManagedUserResponse(value.getEmail())).toList());
    }

    /**
     * 현재 인증된 전문가가 관리하는 사용자의 다이어리 목록을 반환
     * @param req - 요청 DTO. 페이징 관련 변수와 소유자의 이메일을 가진다.
     * @return 요청의 ownerEmail 에 해당하는 사용자의 Diary 목록을 Page로 반환
     */
    @GetMapping("/api/expert/diaries")
    public ResponseEntity<Page<GetDiaryByPageResponse>> getDiariesByEmailForExpert(
            @ModelAttribute GetDiaryByPageForExpertRequest req) {

        return ResponseEntity.ok(diaryService.getDiariesByEmailForExpert(req));
    }

    /**
     * 현재 인증된 전문가가 관리하는 사용자의 특정 다이어리를 반환
     * @param req - 요청 DTO. 조회할 다이어리의 id와 소유자의 이메일을 가진다.
     * @return 요청의 ownerEmail과 id에 해당하는 사용자의 단일 Diary를 반환
     */
    @GetMapping("/api/expert/diary")
    public ResponseEntity<GetDiaryByIdResponse> getDiaryByIdForExpert(
            @ModelAttribute GetDiaryForExpertRequest req
    ) {
        return ResponseEntity.ok(diaryService.getDiaryByIdForExpert(req));
    }

    /**
     * 전문가가 작성한(소유한) 코멘트 목록을 반환
     * @return 코멘트 목록
     */
    @GetMapping("/api/expert/comments")
    public ResponseEntity<List<GetCommentByDiaryIdResponse>> getCommentsByExpert() {
        return ResponseEntity.ok().body(commentService.getCommentsByExpert());
    }

    //테스트용 임시 엔드 포인트
    @PostMapping("/api/expert/managedUsers/{userEmail}")
    public ResponseEntity<String> addManagedUser(@PathVariable String userEmail) {
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElse("");

        Expert expert = expertRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found expert : " + currentUserEmail));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user : " + userEmail));

        expert.addManagedUser(user);

        userRepository.save(user);

        return ResponseEntity.ok(userEmail);
    }
}
