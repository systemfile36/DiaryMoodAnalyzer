package org.diarymoodanalyzer.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Base64;

//발급자와 비밀 키 프로퍼티를 빈으로 등록하여 사용하기 위한 객체
@Setter
@Component
@ConfigurationProperties("jwt") //application.yml 에서 jwt 프로퍼티를 가져와서 해당 객체에 매핑함
public class JwtProperties {
    @Getter
    private String issuer;
    private String secretKey;

    /*
    비밀 키를 Base64로 인코딩하여 넘긴다.
    평문으로 넘기면 에러가 발생한다.
     */
    public String getSecretKey() {
        byte[] bytes = secretKey.getBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

}
