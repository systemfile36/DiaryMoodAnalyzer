package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.LoginRequest;
import org.diarymoodanalyzer.dto.request.SignUpRequest;
import org.diarymoodanalyzer.dto.response.LoginResponse;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.EmailValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

/*
로그인/로그아웃/회원가입 등 인증 관련 서비스
 */
@RequiredArgsConstructor
@Service
public class AuthService {

    private final RefreshTokenService refreshTokenService;

    private final TokenProvider tokenProvider;

    private final UserDetailService userDetailService;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public LoginResponse login(LoginRequest req) throws IllegalArgumentException {

        //이메일을 기반으로 사용자 정보를 받아옴. 없는 이메일이면 예외 발생
        UserDetails userDetails = userDetailService.loadUserByUsername(req.getEmail());

        //요청의 비밀번호와 저장된 사용자의 비밀번호 일치 여부 비교
        if(bCryptPasswordEncoder.matches(req.getPassword(), userDetails.getPassword())) {

            //UserDetails 를 User 로 형변환 (원래 User 엔티티였기에 가능)
            User user = (User)userDetails;

            //인증되면 토큰 생성
            String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);
            String refreshToken = tokenProvider.createToken(user, TokenProvider.REFRESH_EXPIRE);

            //리프레쉬 토큰 저장
            refreshTokenService.save(user.getUserId(), refreshToken);

            //응답 DTO 구성
            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);

            return response;
        } else {
            //비밀 번호가 불일치하면 예외 던짐
            throw new IllegalArgumentException("invalid password");
        }
    }

    @Transactional
    public void signUp(SignUpRequest req) throws IllegalArgumentException {

        //이메일 형식이 유효한 지 체크
        if(!EmailValidator.isValidEmail(req.getEmail()))
            throw new IllegalArgumentException("Invalid email");

        //이메일이 DB에 이미 존재하는지 체크
        if(userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateKeyException("Already exists email : " + req.getEmail());
        }


        //모든 체크를 통과하면 User 엔티티 만들어서 저장
        User user = User.builder()
                .email(req.getEmail())
                .password(bCryptPasswordEncoder.encode(req.getPassword()))
                .build();

        userRepository.save(user);
    }
}