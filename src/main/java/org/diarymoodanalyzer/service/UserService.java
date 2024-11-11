package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.AddUserRequest;
import org.diarymoodanalyzer.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public Long save(AddUserRequest req) {
        return userRepository.save(User.builder()
                .email(req.getEmail())
                .password(bCryptPasswordEncoder.encode(req.getPassword())) //비밀 번호 암호화
                .build()).getId();
    }

    public User findById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(()->new IllegalArgumentException("not found: userId " + userId));
    }
}
