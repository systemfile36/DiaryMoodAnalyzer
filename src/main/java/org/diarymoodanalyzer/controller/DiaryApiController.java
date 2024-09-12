package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.AddDiaryRequest;
import org.diarymoodanalyzer.dto.AddDiaryResponse;
import org.diarymoodanalyzer.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiaryApiController {

    private final DiaryService diaryService;

    @PostMapping("/api/diaries")
    public ResponseEntity<AddDiaryResponse> addDiary(
            @RequestBody AddDiaryRequest req
            ) {

        return ResponseEntity.status(HttpStatus.CREATED).body(diaryService.addDiary(req));
    }
}
