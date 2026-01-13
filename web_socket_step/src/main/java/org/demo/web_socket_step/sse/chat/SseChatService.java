package org.demo.web_socket_step.sse.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class SseChatService {

    private final SseChatRepository sseChatRepository;
    // 변수 데이터양이 많을 경우 자료구조(컬렉션 프레임워크)
    // 접속한 클라이언트들 보관(key-value) -> Map
    private final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    // 1. SSE 연결 - 클라이언트가 접속하면 호출 됨
    public SseEmitter createConnection(String clientId) {
        // 1. 1분간 연결 유지 (시간 설정)
        SseEmitter emitter = new SseEmitter(60 * 1000L);

        // 새로운 연결 요청자의 정보를 서버단 메모리에 관리
        emitterMap.put(clientId, emitter);
        log.info("SSE 연결 추가 : {}", clientId);

        // 연결 종료 / 타임아웃 시 목록에서 제거
        emitter.onCompletion(() -> emitterMap.remove(clientId));
        emitter.onTimeout(() -> emitterMap.remove(clientId));
        emitter.onError((e) -> emitterMap.remove(clientId));

        // 클라이언트로 메세지 발송
        try {
            emitter.send(SseEmitter.event().name("connect").data("연결됨"));
        } catch (IOException e) {
            log.error("초기 전송 실패 : ", e);
        }
        return emitter;
    }

    // 클라이언트에서 온 메세지를 DB에 저장 / 그 메세지를 연결된 구독자들에게 전파(브로드캐스트)
    public void addMessage(String message, String sender) {
        Chat chat = Chat.builder()
                .message(message)
                .sender(sender)
                .build();
        sseChatRepository.save(chat);
        // 접속한 모든 사람들에게 알림(Broadcast)
        // 데이터를 저장하자마자 바로 뿌림(출력)
        broadcast(message, sender);
    }

    // 실제 알림 보내기
    public void broadcast(String message, String sender) {
        // 간단하게 문자열로 조홥해서 보낼 예정 - JSON 형식 X
        String formattedMessage = sender + ":" + message;
        // js 측에서 홍길동: 만나서 반가워 -> : 기준으로 보낸이 추출, 메세지 추출

        emitterMap.forEach((id, emitter) -> {

            try {
                emitter.send(SseEmitter
                        .event()
                        .name("newMessage") // 클라이언트 이벤트 명
                        .data(formattedMessage));
            } catch (IOException e) {
                emitterMap.remove(id); // 전송 실패시 제거
            }
        });
    }

    // 전체 목록 조회 기능 - 신규 유저 진입 시 기존 채팅 목록 확인
    public List<Chat> findAll() {

        return sseChatRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
    }

}
