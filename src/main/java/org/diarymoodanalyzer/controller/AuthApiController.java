package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.LoginRequest;
import org.diarymoodanalyzer.dto.response.LoginResponse;
import org.diarymoodanalyzer.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final AuthService authService;

    /*
    AuthService를 통해 로그인 처리. 예외 발생 시, 에러 메시지를 보냄
    따라서 반환형의 제네릭을 <?>로 설정 
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            LoginResponse result = authService.login(req);
            
            //인증 성공시 응답
            return ResponseEntity.ok().body(result);
        } catch(IllegalArgumentException e) {
            //인증 실패 시 HTTP 401 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }
}
