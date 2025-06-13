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

    /**
     * Login endpoint. <br/>
     * @param req Request DTO contain authentication info
     * @return Response DTO when logic complete without exception, otherwise {@link org.diarymoodanalyzer.exception.GlobalExceptionHandler} will handle exception.
     */
    @PostMapping("/api/auth/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        LoginResponse result = authService.login(req);

        return ResponseEntity.ok().body(result);
    }

    /**
     * Signup endpoint. <br/>
     * @param req Request DTO contain signup info
     * @return None. If exception occurred in process, {@link org.diarymoodanalyzer.exception.GlobalExceptionHandler} will handle it.
     */
    @PostMapping("/api/auth/signup")
    public ResponseEntity<Void> signUp(@RequestBody SignUpRequest req) {
        authService.signUp(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }

    /**
     * Logout endpoint.
     */
    @GetMapping("/api/logout")
    public ResponseEntity<Void> logout() {
        authService.logout();
        return ResponseEntity.ok().body(null);
    }
}
