package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.request.LoginRequest;
import org.diarymoodanalyzer.dto.request.SignUpRequest;
import org.diarymoodanalyzer.dto.response.LoginResponse;
import org.diarymoodanalyzer.service.AuthService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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

    /*
    회원 가입 메소드. 성공 시 HTTP 201 Created 를 반환한다.
    이메일이 형식에 맞지 않으면 HTTP 400 Bad Request 와 에러 메시지를 반환한다.
    중복된 이메일이면 HTTP 409 Conflict 와 에러 메시지를 반환한다.
     */
    @PostMapping("/api/auth/signup")
    public ResponseEntity<?> signUp(@RequestBody SignUpRequest req) {
        try {
            authService.signUp(req);
        } catch(IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch(DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /*
    로그아웃
     */
    @GetMapping("/api/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().body(null);
    }
}
