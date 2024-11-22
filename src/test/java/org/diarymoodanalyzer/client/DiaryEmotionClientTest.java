package org.diarymoodanalyzer.client;

import org.diarymoodanalyzer.domain.DepressionLevel;
import org.diarymoodanalyzer.dto.ai.request.DiaryEmotionRequest;
import org.diarymoodanalyzer.dto.ai.response.DiaryEmotionResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class DiaryEmotionClientTest {

    @Autowired
    private DiaryEmotionClient client;

    @DisplayName("sendRequest: AI 서버로 요청을 보내고, 응답을 받는다. ")
    @Test
    public void sendRequest() {
        DiaryEmotionRequest req = new DiaryEmotionRequest();
        req.setDiary("""
                시간이 만병통치약이라는 말은 거짓이다. 단지 모든 것을 망각의 저편으로 몰아내고 그 가치를 떨어뜨림으로써 문제 자체를 풍화시킬 뿐이다.
                내가 변하면 세상도 변한다는 말 또한 거짓이다. 기만이다. 세상은 언제나 개인을 침식하고 틀에 끼워 맞춘 후 삐져나온 부분을 조금씩 갈아낸다. 그러는 사이에 생각하기를 포기해버리는 것에 불과하다. 세상이, 그리고 주위가 '내가 달라지니 세상도 달라졌다.'고 생각하게끔 강요하고 세뇌해나가는 것뿐이다.
                그런 감정론과 근성론과 정신론으로는 세상도 주위도 집단도 바꿔놓을 수 없다.
                진정으로 세상을 바꿔놓는다는 게 무엇인지 알려주마""");

        DiaryEmotionResponse res = client.sendRequest(req);

        assertThat(res).isNotNull();

        byte weight = (byte)res.getOverall_average_weight();

        DepressionLevel.fromValue(weight);

        System.out.println(weight);
    }
}
