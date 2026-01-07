package org.example.demo_ssr_v1_1.refund;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.demo_ssr_v1_1.payment.Payment;
import org.example.demo_ssr_v1_1.user.User;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Table(name = "refund_request_tb")
@Entity
public class RefundRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 환불에 대한 요청자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 결제 정보(payment): 환불 정책 - 전체 환불 -> 1:1
    // 추후 확장성을 위해 부분 환불을 도입 시 1:N 설계
    //      -> @OneToOne 대신 @ManyToOne + unique 제약조건을 걸어 1:1 구현
    // 부분 환불 도입 시 unique만 삭제
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    // 사용자가 환불할 사유 저장
    @Column(length = 500)
    private String reason;

    // 환불 상태 enum(대기. 승인 . 거절)
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RefundStatus status =  RefundStatus.PENDING; // 기본값 대기

    // 관리자 환불 거절 사유
    @Column(length = 500)
    private String rejectReason;

    // 생성 시간
    @CreationTimestamp
    private Timestamp createdAt;

    // 수정 시간
    @UpdateTimestamp
    private Timestamp updatedAt;

    // 사용자가 먼저 환불 요청 -> row 생성 (reason: 환불 사유)
    @Builder
    public RefundRequest(User user, Payment payment, String reason) {
        this.user = user;
        this.payment = payment;
        this.reason = reason;
        this.status = RefundStatus.PENDING;
    }

    // 편의 기능

    // 환불 승인 처리
    public void approve() {
        this.status = RefundStatus.APPROVED;
    }

    // 관리자 -
    // 환불 거절 처리
    public void reject(String rejectReason) {
        this.status = RefundStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    // 현재 상태 확인(대기중인 상태 확인)
    public boolean isPending() {
        return this.status == RefundStatus.PENDING;
    }

    // 현재 상태 확인(승인 상대 확인)
    public boolean isApproved() {
        return this.status == RefundStatus.APPROVED;
    }

    // 현재 상태 확인(환불 거절 확인)
    public boolean isRejected() {
       return this.status == RefundStatus.REJECTED;
    }
}
