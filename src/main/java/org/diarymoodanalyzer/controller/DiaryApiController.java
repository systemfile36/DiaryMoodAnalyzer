package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.*;
import org.diarymoodanalyzer.dto.response.*;
import org.diarymoodanalyzer.service.CommentService;
import org.diarymoodanalyzer.service.DiaryAnalyzeStatService;
import org.diarymoodanalyzer.service.DiaryService;
import org.diarymoodanalyzer.service.DiaryStatisticsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class DiaryApiController {

    private final DiaryService diaryService;

//    private final DiaryStatisticsService diaryStatisticsService;

    private final DiaryAnalyzeStatService diaryAnalyzeStatService;

    private final CommentService commentService;

    @PostMapping("/api/diaries")
    public ResponseEntity<AddDiaryResponse> addDiary(
            @RequestBody AddDiaryRequest req
            ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(diaryService.addDiary(req));
    }

    @GetMapping("/api/diary/{id}")
    public ResponseEntity<GetDiaryByIdResponse> getDiaryById(@PathVariable long id) {
        return ResponseEntity.ok().body(diaryService.getDiaryById(id));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
     */
    @GetMapping("/api/diaries")
    public ResponseEntity<Page<GetDiaryByPageResponse>> getDiariesByEmail(
            @ModelAttribute GetDiaryByPageRequest req //GET으로 받은 파라미터가 DTO로 자동 매핑됨
            ) {
        return ResponseEntity.ok(diaryService.getDiariesByEmail(req));
    }

    /*
    사용자 이메일과 페이지 번호, 페이지 사이즈, 정렬 기준 필드와 오름차순 여부를 받아서 페이지로 리턴한다.
    정렬 기준 필드와 오름차순은 기본값으로 created_at, false로 설정되어 있다. 파라미터가 없으면 해당 기본값을 사용한다.
    DTO를 통해 제목과 생성/수정 일자, 유저 email만 반환한다.
     */
    @GetMapping("/api/diaries/title")
    public ResponseEntity<Page<GetDiaryTitleByPageResponse>> getDiariesTitleByEmail(
            @ModelAttribute GetDiaryByPageRequest req
            ) {
        return ResponseEntity.ok(diaryService.getDiariesTitleByEmail(req));
    }
    /*
    Diary의 id로 수정
     */
    @PutMapping("/api/diary/{id}")
    public ResponseEntity<GetDiaryByIdResponse> updateDiary(
            @PathVariable long id,
            @RequestBody AddDiaryRequest req // 수정에 사용할 요청 DTO
    ) {
        GetDiaryByIdResponse updatedDiary = diaryService.updateDiaryById(id, req);
        return ResponseEntity.ok(updatedDiary);
    }
    /*
    Diary의 id로 삭제
     */
    @DeleteMapping("/api/diary/{id}")
    public ResponseEntity<Void> deleteDiaryById(@PathVariable long id) {

        diaryService.deleteDiaryById(id);

        return ResponseEntity.ok().build();
    }


    /* Comment 관련 엔드 포인트  */

    /**
     * Diary의 id를 받아서 Comment 목록을 반환
     * @param id 코멘트를 추가할 Diary의 id
     * @return HTTP 응답
     */
    @GetMapping("/api/diary/{id}/comments")
    public ResponseEntity<List<GetCommentByDiaryIdResponse>> getCommentsByDiaryId(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(commentService.getCommentsByDiaryId(id));
    }

    @PostMapping("/api/diary/{id}/comments")
    public ResponseEntity<Void> addCommentToDiary(
            @PathVariable Long id,
            @RequestBody AddCommentRequest req
    ) {

        commentService.addCommentToDiary(id, req.getContent());

        //예외가 발생하지 않으면 201 CREATED 반환
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @DeleteMapping("/api/diary/comments/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long id
    ) {
        commentService.deleteComment(id);

        return ResponseEntity.ok().build();
    }

    /* 통계 관련 엔드 포인트 */

    @Deprecated
    @GetMapping("/api/diaries/statistics/average/depressionLevel/daily")
    public ResponseEntity<?> getDailyAverageDepressionLevel(
            @ModelAttribute GetDailyAvgDepressionLevelRequest req //DTO 사용
    ) {
        //start와 end 둘 중 하나라도 null이면 전체 Diary에 대해, 둘 다 있으면 해당 범위 내의 Diary에 대해 일별 평균 계산
//        return ResponseEntity.ok((req.getStart() == null || req.getEnd() == null) ?
//                diaryStatisticsService.getDailyAvgDepressionLevel() : diaryStatisticsService.getDailyAvgDepressionLevel(req.getStart(), req.getEnd()));

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Deprecated endpoint");
    }


    @GetMapping("/api/diaries/statistics/average/depressionScore/daily")
    public ResponseEntity<DailyAvgDepressionScoreResponse> getDailyAverageDepressionScore(
            @ModelAttribute DailyAvgDepressionScoreRequest req // Mapping to DTO by using `@ModelAttribute`
    ) {
        return ResponseEntity.ok(diaryAnalyzeStatService.getDailyAvgDepressionScore(req));
    }

}
