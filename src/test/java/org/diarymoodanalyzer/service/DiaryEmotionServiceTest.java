package org.diarymoodanalyzer.service;

import org.diarymoodanalyzer.domain.DepressionLevel;
import org.diarymoodanalyzer.domain.Diary;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Deprecated
@SpringBootTest
public class DiaryEmotionServiceTest {

    @Autowired
    private DiaryEmotionService diaryEmotionService;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("submitTask: AI 분석 요청이 제대로 실행된다.")
    @Test
    public void submitTask() throws Exception {

        //테스트 데이터 세팅

        User user = User.builder()
                .email("test@email.com")
                .password("testPassword")
                .build();

        userRepository.save(user);


        Diary diary = Diary.builder()
                .title("TestTitle")
                .content("TestContent")
                .build();

        user.addDiary(diary);

        diaryRepository.save(diary);

        DiaryEmotionTask task = new DiaryEmotionTask(diary);

        //테스크 실행
        diaryEmotionService.submitTask(task);

        //처리 될 때까지 대기
        Thread.sleep(10000);

        Diary resultDiary = diaryRepository.findById(diary.getId())
                .orElseThrow(()->new IllegalArgumentException("not found diary : " + diary.getId()));

        //오류 여부와 반영되었는지 여부를 체크한다.
        assertThat(resultDiary.getDepressionLevel()).isNotIn(DepressionLevel.ERROR.getValue(), DepressionLevel.NOT_REFLECTED.getValue());
    }
}
