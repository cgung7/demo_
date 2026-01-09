package org.example.demo_ssr_v1_1.refund;

import lombok.Data;
import org.example.demo_ssr_v1_1._core.utils.MyDateUtil;

public class RefundResponse {

    @Data
    public static class ListDTO {
        private Long id;
        private Long paymentId;
        private Integer amount;
        private String reason;
        private String rejectReason; // 환불 거절 사유(관리자), 환불 승인 시 필요 X
        private String statusDisplay; // 화면 표시용 (대기중, 승인, 거절)

        // 상태별 플래그 변수 사용 (화면 표시용)
        private boolean isPending; // 대기중
        private boolean isApproved; // 승인
        private boolean isRejected; // 거절

        public ListDTO(RefundRequest refund) {
            this.id = refund.getId();
            this.paymentId = refund.getPayment().getId();
            this.amount = refund.getPayment().getAmount();
            this.reason = refund.getReason();
            this.rejectReason = (refund.getRejectReason() == null) ? "" : refund.getRejectReason();

            // 스위치 표현식(14버전 부터 사용 가능)
            switch (refund.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }

            this.isPending = (refund.getStatus() == RefundStatus.PENDING);
            this.isApproved = (refund.getStatus() == RefundStatus.APPROVED);
            this.isRejected = (refund.getStatus() == RefundStatus.REJECTED);
        }
    }

    @Data
    public static class AdminListDTO {
        private Long id;
        private String username;
        private Long paymentId; // 결제 PK
        private String merchantUid; // 주문번호 (가맹점)
        private String impUit; // 포트원으로 승인 요청할 때;
        private Integer amount;
        private String requestAt; // 환불요청 일시
        private RefundStatus status;
        private String statusDisplay; // 머스테치용 표시
        private String reason;
        private String rejectReason;

        public AdminListDTO(RefundRequest refundRequest) {
            this.id = refundRequest.getId();
            this.username = refundRequest.getUser().getUsername();
            this.paymentId = refundRequest.getPayment().getId();
            this.merchantUid = refundRequest.getPayment().getMerchantUid();
            this.impUit = refundRequest.getPayment().getImpUid();
            this.amount = refundRequest.getPayment().getAmount();

            this.status = refundRequest.getStatus();

            this.reason = refundRequest.getReason();
            this.rejectReason = refundRequest.getRejectReason();

            // 변환 -> 대기중 / 승인됨 / 거절됨
            this.statusDisplay = statusDisplay;
            // 스위치 표현식 사용 -- JDK 14 버전 이상부터 사용
            switch (refundRequest.getStatus()) {
                case PENDING -> this.statusDisplay = "대기중";
                case APPROVED -> this.statusDisplay = "승인됨";
                case REJECTED -> this.statusDisplay = "거절됨";
            }
            // refundRequest.getCreatedAt() --> pc --> DB
            // 테스트 --> 셈플로 직점 insert 처리(createAt 비워질 수 있음)
            if (refundRequest.getCreatedAt() != null) {
                this.requestAt = MyDateUtil.timestampFormat(refundRequest.getCreatedAt());
            }
//            this.requestAt = refundRequest.getCreatedAt();
        }
    }
}
