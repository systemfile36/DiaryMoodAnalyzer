package org.diarymoodanalyzer.service;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.domain.User;
import org.diarymoodanalyzer.dto.request.AddUserRequest;
import org.diarymoodanalyzer.repository.UserRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    public Long save(AddUserRequest req) {
        return userRepository.save(User.builder()
                .email(req.getEmail())
                //실제로는 암호화가 들어가야 함! 아직은 테스트 목적이므로 생략
                .password(req.getPassword())
                .build()).getUserId();
    }
}
