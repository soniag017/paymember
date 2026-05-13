package com.paymember.backend.repository;

import com.paymember.backend.model.Subscription;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    List<Subscription> findByUserIdOrderByServiceNameAsc(Long userId);
    Optional<Subscription> findByIdAndUserId(Long id, Long userId);
}
