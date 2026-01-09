package org.example.demo_ssr_v1_1.refund;

import lombok.RequiredArgsConstructor;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception400;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception403;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception404;
import org.example.demo_ssr_v1_1._core.errors.exception.Exception500;
import org.example.demo_ssr_v1_1.payment.Payment;
import org.example.demo_ssr_v1_1.payment.PaymentRepository;
import org.example.demo_ssr_v1_1.payment.PaymentResponse;
import org.example.demo_ssr_v1_1.user.User;
import org.example.demo_ssr_v1_1.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class RefundService {

    private final RefundRequestRepository refundRequestRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;

    @Value("${portone.imp-key}")
    private String impKey;

    @Value("${portone.imp-secret-key}")
    private String impSecret;

    // 0단계 : 환불 요청 화면 진입 시 검증
    public Payment 환불요청폼화면검증(Long paymentId, Long userId) {

        // 결제 내역 정보 확인 --> 결제 정보(paymentId), 누가 결제했는지 정보(userId)
        // paymentId
        // 1. 결제 내역 조회 (User 정보)
        Payment payment = paymentRepository.findByIdWithUser(paymentId);
        // 2. 본인 확인
        if (!payment.getUser().getId().equals(userId)) {
            throw new Exception403("본인 결제 내역만 환불 요청할 수 있습니다.");
        }
        // 3. 결제 완료 상태 확인 -> PAID 일 경우에만 폼을 열어 둘 예정
        if (!"paid".equals(payment.getStatus())) {
            throw new Exception400("결제 완료된 상태만 환불 요청할 수 있습니다.");
        }
        // 4. 이미 환불 요청한 상태인지 확인
        if (refundRequestRepository.findByPaymentId(paymentId).isPresent()) {
            throw new Exception400("이미 환불 요청이 진행 중 입니다.");
        }

        if (!"cancelled".equals(payment.getStatus())) {
            throw new Exception400("환불 거절된 상태는 환불 신청할 수 없습니다.");
        }

        return payment;
    }

    // 1. 환불 요청 (사용자)
    @Transactional
    public void 환불요청(Long userId, RefundRequestDTO.RequestDTO reqDTO) {

        // 화면 검증 로직 재사용
        Payment payment = 환불요청폼화면검증(reqDTO.getPaymentId(), userId);

        // 사용자 조회 (세션값으로 넘어온 id가 실제 존재하는지 검증)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new Exception404("사용자를 찾을 수 없습니다."));

        // 환불 요청 테이블에 이력 저장
        RefundRequest refundRequest = RefundRequest.builder()
                .user(user)
                .payment(payment)
                .reason(reqDTO.getReason())
                .build();

        refundRequestRepository.save(refundRequest);
    }

    public List<RefundResponse.ListDTO> 환불요청목록조회(Long userId) {

        List<RefundRequest> refundList = refundRequestRepository.findAllByUserId(userId);

        return refundList.stream()
                .map(RefundResponse.ListDTO::new)
                .toList();
    }

    public List<RefundResponse.AdminListDTO> 관리자환불요청목록조회() {
        List<RefundRequest> refundRequestList = refundRequestRepository.findAllWithUserAndPayment();

        return refundRequestList.stream()
                .map(RefundResponse.AdminListDTO::new)
                .toList();
    }

    @Transactional
    public void 환불거절(Long refundRequestId, String rejectReason) {
        RefundRequest refundRequest = refundRequestRepository.findById(refundRequestId)
                .orElseThrow(() -> new Exception403("환불 요청을 찾을 수 없습니다."));

        if (!refundRequest.isPending()) {
            throw new Exception400("대기 중인 환불 요청만 거절할 수 있습니다.");
        }
        if (rejectReason == null || rejectReason.trim().isEmpty()) {
            throw new Exception400("거절 사유를 입력해주세요.");
        }
        // RefundRequest 내 reject() 편의 메서드 생성함
        refundRequest.reject(rejectReason);
        // 더티 체킹(트랜젝션이 끝나면 자동 반영)
    }

    @Transactional
    public void 환불승인(Long id) {

        // 환불 테이블 - 포트원 고유 번호, 가맹점 번호
        // 1. 환불 요청 테이블 조회
        // 환불(PK)
        RefundRequest refundRequest = refundRequestRepository.findByIdWithUserAndPayment(id)
                .orElseThrow(() -> new Exception404("환불 요청을 찾을 수 없습니다."));

        // 2. 환불
        if (!refundRequest.isPending()) {
            throw new Exception400("대기 중인 환불 요청만 승인할 수 있습니다.");
        }

        // 3. 포인트 잔액 검증
        Payment payment = refundRequest.getPayment();
        User user = refundRequest.getUser();
        Integer refundAmount = payment.getAmount(); // 결제 금액

        if (user.getPoint() < refundAmount) {
            // 충전 금액보다 사용을 했을 경우
            throw new Exception400("사용자의 포인트 잔액이 부족합니다.");
        }
        포트원결제취소(payment.getImpUid(), payment.getAmount());
        // 포티원 엑세스 토큰 발급 요청(포트원 인증서버)
        // 포트원 자원 서버에서 update 요청 (결제 취소)
        // 내 포인트 잔액 -> 환불한 금액 만큼 차감 처리
        user.deductPoint(refundAmount); // 더티 채킹 - 포인트 차감
        payment.setStatus("cancelled"); // 결제 상태 paid -> cancelled 변경
        refundRequest.approve();        // 환불상태 승인으로 변경

    }

    private void 포트원결제취소(String impUid, Integer amount) {
        // 1. 엑세스 토큰 발급
        String accessToken = 포트원액세스토큰발급();
        System.out.println("accessToken: " + accessToken);

        // 2. 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // 3. 요청 바디 설정
        Map<String, Object> body = new HashMap<>();
        body.put("imp_uid", impUid);
        body.put("amount", amount);
        body.put("reason", "관리자 환불 승인");

        // 4. HTTP 요청 메세지 만들기
        HttpEntity<Map<String, Object>> requestEntity =
                new HttpEntity<>(body, headers);

        // 5. HTTP 클라이언트 객체 --> RestTemplate 사용
        RestTemplate restTemplate = new RestTemplate();
        try {
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.iamport.kr/payments/cancel",
                HttpMethod.POST,
                requestEntity,
                Map.class
        );

        // 6. 응답 처리
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new Exception500("포트원 응답이 비어있습니다.");
        }

        Integer code = (Integer) responseBody.get("code");

        if (code != 0) {
            String message = (String) responseBody.get("message");
            throw new Exception400("환불 실패 :" + message);
        }

        } catch (Exception e) {
            throw new Exception500("포트원 결제 취소 중 오류 발생");
        }
    }

    private String 포트원액세스토큰발급() {
        // 외부통신 경우 try-catch 정의 권장
        try {
            // "https://api.iamport.kr/users/getToken"
            RestTemplate restTemplate = new RestTemplate();

            // HTTP 메세지 헤더 생성
            HttpHeaders headers = new HttpHeaders();
            // headers: { "Content-Type": "application/json" }
            headers.setContentType(MediaType.APPLICATION_JSON);

            // HTTP 메세지 바디 생성
            Map<String, String> body = new HashMap<>();
            // 포트원에 발급 받았던 REST API KEY 값
            body.put("imp_key", impKey);
            body.put("imp_secret", impSecret);

            // 헤더 + body 결합
            HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

            // 통신 요청
            ResponseEntity<PaymentResponse.PortOneTokenResponseDTO> response = restTemplate.exchange(
                    // 1) 주소
                    "https://api.iamport.kr/users/getToken",
                    // 2) CRUD 형식
                    HttpMethod.POST,
                    // 3) 헤더 + 바디값
                    request,
                    // 3) 어떤 타입인지?
                    PaymentResponse.PortOneTokenResponseDTO.class
            );

            // 응답 받은 엑세스 토큰 리턴
            return response.getBody().getResponse().getAccessToken();
        } catch (Exception e) {
            throw new Exception400("포트원 인증 실패: 관리자 설정을 확인해주세요.");
        }
    }
}
