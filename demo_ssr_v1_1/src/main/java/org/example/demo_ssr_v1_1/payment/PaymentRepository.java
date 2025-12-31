package org.example.demo_ssr_v1_1.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // 기본적인 CRUD 만들어진 상태 ...

    // imp_uid 로 결제내역 조회
    // 포트원 결재 번호로 Payment(결제) 정보 조회 쿼리 자동 생성
    Optional<Payment> findByImpUid(String impUid);
    // 우리서버 주문번호 조회
    Optional<Payment> findByMerchantUid(String impUid);

    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.merchantUid = :merchantUid")
    boolean existsByMerchantUid(@Param("merchantUid") String merchantUid);




}
