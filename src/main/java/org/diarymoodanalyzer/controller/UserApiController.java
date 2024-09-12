package org.diarymoodanalyzer.controller;

import lombok.RequiredArgsConstructor;
import org.diarymoodanalyzer.dto.AddUserRequest;
import org.diarymoodanalyzer.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class UserApiController {

    private final UserService userService;

    //테스트를 위한 구현. 실제로는 이메일로 인증번호를 보내고 하는 등의 로직 추가 예정
    @PostMapping("/api/user")
    public ResponseEntity<String> signup(@RequestBody AddUserRequest req) {
        userService.save(req);
        
        //실제로는 ResponseEntity<String>이 아니라 DTO를 사용할 것
        return ResponseEntity.ok().body(req.getEmail());
    }

}
