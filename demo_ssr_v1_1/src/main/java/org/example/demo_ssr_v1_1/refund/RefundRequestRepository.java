package org.example.demo_ssr_v1_1.refund;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RefundRequestRepository extends JpaRepository<RefundRequest, Long> {

    // 사용자 ID로 환불 요청 목록 조회
    // N + 1 방지를 위해 한번에 JOIN FETCH 사용
    @Query("""
                    SELECT r FROM RefundRequest r
                    JOIN FETCH r.payment p
                    JOIN FETCH p.user u
                    WHERE r.user.id = :userId
                    ORDER BY r.createdAt DESC
            """)
    List<RefundRequest> findAllByUserId(@Param("userId") Long userId);

    // 결제 아이디로 환불 요청 조회 여부 확인
    Optional<RefundRequest> findByPaymentId(Long paymentId);
}
