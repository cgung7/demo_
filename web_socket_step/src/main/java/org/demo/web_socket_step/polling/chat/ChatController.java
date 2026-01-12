package org.demo.web_socket_step.polling.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@Controller("PollingChatController")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/")
    public String index(Model model) {
      log.debug("Polling 페이지가 새로고침 되었습니다.");

      model.addAttribute("chatList", chatService.findAll());

      return "polling/index";
    }

    @PostMapping("/chat")
    public String save(@RequestParam(name = "message") String message,
                       @RequestParam(name = "sender", defaultValue = "익명") String sender
    ) {
        chatService.save(message, sender);
        // PRG 패턴
        return "redirect:/";
    }
}
