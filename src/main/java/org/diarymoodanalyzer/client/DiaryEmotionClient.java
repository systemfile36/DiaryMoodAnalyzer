package org.diarymoodanalyzer.client;

import org.diarymoodanalyzer.config.DiaryAnalyzeProperties;
import org.diarymoodanalyzer.dto.ai.request.DiaryEmotionRequest;
import org.diarymoodanalyzer.dto.ai.response.DiaryEmotionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Diary 감정 분석 AI 서버와의 통신을 담당하는 클래스
 */
@Deprecated
@Component
public class DiaryEmotionClient {
    private final WebClient webClient;

    private final DiaryAnalyzeProperties diaryAnalyzeProperties;

    //자동으로 등록된 WebClient.Builder 를 주입받아서 구성한다.
    public DiaryEmotionClient(WebClient.Builder webClientBuilder, DiaryAnalyzeProperties diaryAnalyzeProperties) {

        //프로퍼티 클래스 초기화
        this.diaryAnalyzeProperties = diaryAnalyzeProperties;

        //프로퍼티에 설정된 url 사용
        this.webClient = webClientBuilder
                .baseUrl(diaryAnalyzeProperties.getUrl())
                //기본적으로 application/json 추가
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();

    }

    /**
     * AI 서버에 요청을 보내서 Diary의 분석결과를 받아온다.
     * @param req 요청할 Diary 데이터
     * @return 분석 결과
     * @throws RuntimeException 요청 중에러가 발생했을 경우. (예외 클래스 따로 만들 예정)
     */
    public DiaryEmotionResponse sendRequest(DiaryEmotionRequest req) throws RuntimeException {
        //응답을 받아온다.
        ResponseEntity<DiaryEmotionResponse> res = webClient.post()
                .uri("/analyze")
                .bodyValue(req) //자동으로 매핑될 것
                .retrieve()
                .toEntity(DiaryEmotionResponse.class)
                .block(); //동기적으로 불러온다.

        if(res == null || (res.getStatusCode() != HttpStatus.OK)) {
            //예외 클래스 만들 예정
            throw new RuntimeException("Failed to analyze Diary content : " + req.getDiary());
        } else {
            return res.getBody();
        }
    }
}
