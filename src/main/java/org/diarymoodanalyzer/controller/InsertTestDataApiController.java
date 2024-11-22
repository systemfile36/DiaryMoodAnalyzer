package org.diarymoodanalyzer.controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.AddDiaryRequest;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.service.DiaryService;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RestController
public class InsertTestDataApiController {

    private final DiaryService diaryService;

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    //네이티브 쿼리를 사용하기 위한 엔티티 매니저
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * 테스트용 Diary 엔티티를 추가한다.
     * 최근 한달 간의 날짜들로 TIMESTAMP가 설정된다.
     * 네이티브 SQL 쿼리를 통해 수동으로 진행한다. 
     * @return HTTP 응답
     */
    @Transactional
    @PostMapping("/api/test/diary")
    public ResponseEntity<Void> insertTestDiaryRecentMonth() {
        final String titleFormat = "오늘의 Diary %d (UI 테스트 용)";
        final String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas maximus nunc eget nibh dignissim varius. Cras gravida fermentum sem ut tincidunt. Curabitur efficitur efficitur tempor. Fusce ullamcorper sem pulvinar felis congue, sit amet pulvinar erat porta. Quisque quis massa vel leo placerat elementum. Duis urna nibh, maximus vitae justo vel, pulvinar aliquam dolor. Praesent hendrerit faucibus dictum. Phasellus sollicitudin ut ligula ut scelerisque. Vivamus ac nibh congue, auctor lectus sit amet, venenatis lacus. Nunc rutrum ultricies orci vel mollis. Integer ipsum elit, mollis vel lorem nec, aliquam tempus tellus. Donec consequat faucibus sem. Maecenas rutrum est ut diam consequat, vitae scelerisque nulla tempor. In hac habitasse platea dictumst. Curabitur bibendum lectus quis neque ornare ornare. Mauris nec odio in arcu dictum commodo in eget velit.";

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no permission"));

        //현재 유저 불러옴
        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user : " + currentUserEmail));

        //네이티브 SQL 쿼리문 작성
        String nativeSql = "INSERT INTO diaries(title, content, depression_level, created_at, updated_at, user_id) VALUES " +
                "(:title, :content, :depression_level, :created_at, :updated_at, :user_id)";


        for(int i = 30; i >= 0; i--) {
            String title = String.format(titleFormat, i);

            //만든 시간을 (현재 시간 - 30일) 에서 (현재 시간 - 0일)까지로 설정함 (최근 한달)
            LocalDateTime createdAt = LocalDateTime.now().minusDays(i);

            //감정 분석 결과를 랜덤한 값으로 설정
            byte depressionLevel = (byte)Math.floor(Math.random() * 10);

            //쿼리문 실행
            entityManager.createNativeQuery(nativeSql)
                    .setParameter("title", title)
                    .setParameter("content", content)
                    .setParameter("depression_level", depressionLevel)
                    .setParameter("created_at", createdAt)
                    .setParameter("updated_at", createdAt)
                    .setParameter("user_id", user.getId())
                    .executeUpdate();
        }

        return ResponseEntity.ok(null);
    }

/*
    @PostMapping("/api/test/diary")
    public ResponseEntity<Void> insertTestDiaryData() {
        String titleFormat = "오늘의 Diary %d (Test)";
        String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas maximus nunc eget nibh dignissim varius. Cras gravida fermentum sem ut tincidunt. Curabitur efficitur efficitur tempor. Fusce ullamcorper sem pulvinar felis congue, sit amet pulvinar erat porta. Quisque quis massa vel leo placerat elementum. Duis urna nibh, maximus vitae justo vel, pulvinar aliquam dolor. Praesent hendrerit faucibus dictum. Phasellus sollicitudin ut ligula ut scelerisque. Vivamus ac nibh congue, auctor lectus sit amet, venenatis lacus. Nunc rutrum ultricies orci vel mollis. Integer ipsum elit, mollis vel lorem nec, aliquam tempus tellus. Donec consequat faucibus sem. Maecenas rutrum est ut diam consequat, vitae scelerisque nulla tempor. In hac habitasse platea dictumst. Curabitur bibendum lectus quis neque ornare ornare. Mauris nec odio in arcu dictum commodo in eget velit.";

        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new ResponseStatusException(HttpStatus.FORBIDDEN, "You have no permission"));

        User user = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "not found user : " + currentUserEmail));

        //변경 감지 사용
        for(int i = 1; i <= 30; i++) {
            //diaryService.addDiary(new AddDiaryRequest(String.format(titleFormat, i), content));
            Diary diary = Diary.builder()
                    .title(String.format(titleFormat, i))
                    .content(content)
                    .build();

            user.addDiary(diary);

            diaryRepository.save(diary);
        }

        return ResponseEntity.ok().body(null);
    }*/


}
