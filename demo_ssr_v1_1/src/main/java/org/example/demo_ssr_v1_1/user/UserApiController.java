package org.example.demo_ssr_v1_1.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserApiController {

    private final UserService userService;
    private final MailService mailService;

    @PostMapping("/api/email/send")
    public ResponseEntity<?> 인증번호발송(@RequestBody UserRequest.EmailCheckDTO reqDTO) {

        // 1. 유효성 검사
        reqDTO.validate();

        // 2. 서비스단에서 구글 메일 서버 이메일 전송 처리
        mailService.인증번호발송(reqDTO.getEmail());
        return ResponseEntity.ok().body(Map.of("message", "인증번호가 발송되었습니다."));
    }

    @PostMapping("/api/email/verify")
    public ResponseEntity<?> 인증번호확인(@RequestBody UserRequest.EmailCheckDTO reqDTO) {

        reqDTO.validate();
        // 방어적 코드 - 인증번호 입력 확인
        if (reqDTO.getCode() == null || reqDTO.getCode().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호를 입력해주세요."));
        }

        // 메일 서비스단에서 인증번호 확인
        boolean isVerified = mailService.인증번호확인(reqDTO.getEmail(), reqDTO.getCode());

        // 결과값에 따라 분기 처리
        if (isVerified) {
            // 인증 성공
            return ResponseEntity.ok().body(Map.of("message", "인증되었습니다."));
        } else {
            return ResponseEntity.badRequest().body(Map.of("message", "인증번호가 일치하지 않습니다."));
        }

    }
}
