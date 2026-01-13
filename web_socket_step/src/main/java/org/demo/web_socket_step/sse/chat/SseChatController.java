package org.demo.web_socket_step.sse.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class SseChatController {

    private final SseChatService sseChatService;

    // 1. 채팅방 화면 (기존 글 목록 + SSE 연결 JS 포함)
    @GetMapping(value = "/sse/chat")
    public String index(Model model) {

        model.addAttribute("chatList", sseChatService.findAll());

        return "sse/index";
    }

    // produces : 나는 이런 종류에 데이터를 생성한다.
    // produces = MediaType.TEXT_HTML_VALUE - HTML 파일 형식
    // produces = MediaType.APPLICATION_JSON_VALUE - 데이터(JSON 형식 문자열)

    // 2. ** [SSE 연결] 클라이언트 구독 (이 경로로 진입 시)
    // 중요 ** 응답할 때 HTTP 메세지 헤더에 지속 연결 명시

    // HTTP/1.1 성공- 200 OK
    // HEADER -> Content-Type: Application/json -> text/event-stream;chatSet=UTF-8
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseBody
    public SseEmitter connect() {
        // SseEmitter 객체를 설정하고 준비 후 js 측 반환
        // 서비스 단에 처리
        return sseChatService.createConnection(UUID.randomUUID().toString());
    }

    // 3. 메세지 전송
    @PostMapping("/sse/send")
    public String sendMessage(@RequestParam(name = "message") String message,
                              @RequestParam(name = "sender") String sender
    ) {
        // 서비스가 메세지 저장과 알림을 모두 수행
        sseChatService.addMessage(message, sender);

        // 다시 채팅방으로 돌아옴
        return "redirect:/sse/chat";
    }
}
