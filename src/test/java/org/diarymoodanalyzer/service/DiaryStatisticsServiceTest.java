package org.diarymoodanalyzer.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.response.GetAvgDepressionLevel;
import org.diarymoodanalyzer.repository.DiaryRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DiaryStatisticsServiceTest {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DiaryStatisticsService diaryStatisticsService;

    @PersistenceContext
    private EntityManager entityManager;

    @DisplayName("getDailyAvgDepressionLevel: 일별 평균 depression level을 받아온다. ")
    @Test
    @Transactional
    public void getDailyAvgDepressionLevel() {

        //테스트 데이터 세팅

        final User user = User.builder()
                .email("test@email.com")
                .password("testPassword")
                .build();

        userRepository.save(user);

        insertTestDiary(user);

        GetAvgDepressionLevel result = diaryStatisticsService.getDailyAvgDepressionLevel(user.getEmail());

        System.out.println(result.getDailyAvg());

        assertThat(result.getDailyAvg().size()).isEqualTo(31);


    }

    /**
     * 인자로 받은 User 명의로 테스트용 다이어리를 다량 넣는다.
     * @param user
     */
    @Transactional
    private void insertTestDiary(User user) {

        final String titleFormat = "오늘의 Diary %d (UI 테스트 용)";
        final String content = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas maximus nunc eget nibh dignissim varius. Cras gravida fermentum sem ut tincidunt. Curabitur efficitur efficitur tempor. Fusce ullamcorper sem pulvinar felis congue, sit amet pulvinar erat porta. Quisque quis massa vel leo placerat elementum. Duis urna nibh, maximus vitae justo vel, pulvinar aliquam dolor. Praesent hendrerit faucibus dictum. Phasellus sollicitudin ut ligula ut scelerisque. Vivamus ac nibh congue, auctor lectus sit amet, venenatis lacus. Nunc rutrum ultricies orci vel mollis. Integer ipsum elit, mollis vel lorem nec, aliquam tempus tellus. Donec consequat faucibus sem. Maecenas rutrum est ut diam consequat, vitae scelerisque nulla tempor. In hac habitasse platea dictumst. Curabitur bibendum lectus quis neque ornare ornare. Mauris nec odio in arcu dictum commodo in eget velit.";


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
    }
}
