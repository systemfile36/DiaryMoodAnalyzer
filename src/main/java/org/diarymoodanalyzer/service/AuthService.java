package org.diarymoodanalyzer.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.config.jwt.TokenProvider;
import org.diarymoodanalyzer.domain.Expert;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.domain.UserAuthority;
import org.diarymoodanalyzer.dto.request.LoginRequest;
import org.diarymoodanalyzer.dto.request.SignUpRequest;
import org.diarymoodanalyzer.dto.response.LoginResponse;
import org.diarymoodanalyzer.exception.*;
import org.diarymoodanalyzer.repository.ExpertRepository;
import org.diarymoodanalyzer.repository.UserRepository;
import org.diarymoodanalyzer.util.AuthenticationUtils;
import org.diarymoodanalyzer.util.EmailValidator;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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

    private final ExpertRepository expertRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final NotificationService notificationService;

    private final EmailService emailService;

    /**
     * Login.
     * <br/>
     * Create access token and update refresh token when <code>email</code> and <code>password</code> is valid.
     * @param req Request DTO indicates authentication info
     * @return Response DTO contain access token and refresh token
     */
    @Transactional
    public LoginResponse login(LoginRequest req) {

        // Throw exception when User not found with email
        UserDetails userDetails;
        try {
            userDetails = userDetailService.loadUserByUsername(req.getEmail()); // Query 0
        } catch(UsernameNotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }

        // Check password
        if(bCryptPasswordEncoder.matches(req.getPassword(), userDetails.getPassword())) {

            // Cast UserDetails to User. (UserDetails loaded from UserDetailService is instance of User)
            User user = (User)userDetails;

            // Create access token and refresh token
            String accessToken = tokenProvider.createToken(user, TokenProvider.ACCESS_EXPIRE);
            String refreshToken = tokenProvider.createToken(user, TokenProvider.REFRESH_EXPIRE);

            // Save or update refresh token
            refreshTokenService.saveOrUpdate(user.getId(), refreshToken); // Query 1

            // Create instance of DTO
            LoginResponse response = new LoginResponse();
            response.setAccessToken(accessToken);
            response.setRefreshToken(refreshToken);

            return response;
        } else {
            // Throw exception when password not matches
            throw new InvalidPasswordException("Invalid password: " + req.getEmail());
        }

        // Total Query: [1, 2]
    }

    /**
     * Sign up. <br/>
     * Create proper entity and save it.
     * And initialize notification setting by default
     * @param req Request DTO contain info about signup
     */
    @Transactional
    public void signUp(SignUpRequest req) {

        // Validate format of Email by Regex
        if(!EmailValidator.isValidEmail(req.getEmail()))
            throw new InvalidEmailException("Invalid email: " + req.getEmail());

        // Check email is verified
        if(!emailService.isVerified(req.getEmail())) {
            throw new NotVerifiedEmailException("Email is not verified: " + req.getEmail());
        }

        // Check conflict of email
        if(userRepository.existsByEmail(req.getEmail())) {
            throw new DuplicateEmailException("Already exists email : " + req.getEmail());
        }

        // Check authority and create proper entity
        if(req.getAuthority() == UserAuthority.EXPERT) {
            Expert expert = new Expert(req.getEmail(),
                    bCryptPasswordEncoder.encode(req.getPassword()));

            expertRepository.save(expert);
        } else {
            User user = User.builder()
                    .email(req.getEmail())
                    .password(bCryptPasswordEncoder.encode(req.getPassword()))
                    .build();

            userRepository.save(user);
        }

        // Initialize notification setting by default
        notificationService.initializeDefaultNotificationSettings(req.getEmail());
    }

    /**
     * Logout current authenticated user.
     * <br/>
     * Delete refresh token of current authenticated user
     * @throws ResponseStatusException
     */
    public void logout() throws ResponseStatusException {
        // Load current authenticated user's email
        String currentUserEmail = AuthenticationUtils.getCurrentUserEmail()
                .orElseThrow(()->new NotAuthenticatedException("There is no Authentication"));

        // Load user's id by email
        Long userId = userRepository.findIdByEmail(currentUserEmail);

        // Delete refresh token when logout
        refreshTokenService.deleteByUserId(userId);

    }
}
