package org.diarymoodanalyzer.controller;

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

@RequiredArgsConstructor
@RestController
public class InsertTestDataApiController {

    private final DiaryService diaryService;

    private final DiaryRepository diaryRepository;

    private final UserRepository userRepository;

    /**
     * 테스트 데이터를 넣기 위한 컨트롤러 메소드
     * 인증된 사용자의 소유로 된 다이어리를 30개 만들어서 DB에 저장한다.
     * @return 확인 응답
     */
    @PostMapping("/api/test/diary")
    public ResponseEntity<Void> insertTestDiaryData() {
        String titleFormat = "오늘의 Diary %d";
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
    }
}
