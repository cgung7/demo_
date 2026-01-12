package org.demo.web_socket_step.polling.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service("PollingChatService") // 빈 이름 명시
@Transactional(readOnly = true)
public class ChatService {

    private final PollingChatRepository pollingChatRepository;

    @Transactional
    public void save(String sender, String message) {

        Chat chat = Chat.builder()
                .sender(sender)
                .message(message)
                .build();

        pollingChatRepository.save(chat);
    }

    public List<Chat> findAll() {

        Sort sort = Sort.by(Sort.Direction.ASC, "id");
        return pollingChatRepository.findAll(sort);
    }
}
