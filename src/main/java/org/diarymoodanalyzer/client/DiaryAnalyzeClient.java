package org.diarymoodanalyzer.client;

import org.diarymoodanalyzer.config.DiaryEmotionProperties;
import org.diarymoodanalyzer.dto.ai.request.DiaryAnalyzeRequest;
import org.diarymoodanalyzer.dto.ai.response.DiaryAnalyzeResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class DiaryAnalyzeClient {
    /**
     * Client for AI server. Using WebFlux
     */
    private final WebClient webClient;

    /**
     * Inject {@link WebClient WebClient} and {@link DiaryEmotionProperties DiaryEmotionProperties} from Spring container <br/>
     * Initialize {@link WebClient WebClient} by set base url and headers.
     * @param webClientBuilder Builder of {@link WebClient WebClient}. Will be injected by Spring container
     * @param diaryEmotionProperties Properties contain URL of AI server. Will be injected by Spring container
     */
    public DiaryAnalyzeClient(WebClient.Builder webClientBuilder, DiaryEmotionProperties diaryEmotionProperties) {

        // Use property for set base url of AI server
        this.webClient = webClientBuilder
                .baseUrl(diaryEmotionProperties.getUrl())
                // Default header
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
                })
                .build();
    }

    /**
     * Send request to AI server and receive analyze result response
     * @param req DTO contain content of diary
     * @return result of analyze
     * @throws RuntimeException Error in send
     */
    public DiaryAnalyzeResponse sendRequest(DiaryAnalyzeRequest req) throws RuntimeException {
        // Send request and get response.
        ResponseEntity<DiaryAnalyzeResponse> res = webClient.post()
                .uri("/analyze")
                .bodyValue(req) // will be mapping automatically
                .retrieve()
                .toEntity(DiaryAnalyzeResponse.class) // Decode to ResponseEntity with DiaryAnalyzeResponse
                .block(); // Get result synchronous (Because it will be called at @Async method)

        // Will be replaced to custom exception class
        if(res == null || (res.getStatusCode() != HttpStatus.OK)) {
            throw new RuntimeException("Failed to analyze Diary content : " + req.getDiaryContent());
        } else {
            return res.getBody();
        }
    }
}
