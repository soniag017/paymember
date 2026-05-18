package com.paymember.backend.service;

import com.paymember.backend.dto.SubscriptionDto;
import com.paymember.backend.model.AppUser;
import com.paymember.backend.model.Subscription;
import com.paymember.backend.repository.AppUserRepository;
import com.paymember.backend.repository.SubscriptionRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final AppUserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, AppUserRepository userRepository) {
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    public List<SubscriptionDto> listByUser(Long userId) {
        return subscriptionRepository.findByUserIdOrderByServiceNameAsc(userId).stream()
            .map(SubscriptionDto::fromEntity)
            .toList();
    }

    public SubscriptionDto getById(Long userId, Long id) {
        Subscription item = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        return SubscriptionDto.fromEntity(item);
    }

    public SubscriptionDto create(Long userId, SubscriptionDto dto) {
        AppUser user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Subscription entity = new Subscription();
        entity.setUser(user);
        applyDto(entity, dto);
        return SubscriptionDto.fromEntity(subscriptionRepository.save(entity));
    }

    public SubscriptionDto update(Long userId, Long id, SubscriptionDto dto) {
        Subscription entity = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        applyDto(entity, dto);
        return SubscriptionDto.fromEntity(subscriptionRepository.save(entity));
    }

    public void delete(Long userId, Long id) {
        Subscription entity = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        subscriptionRepository.delete(entity);
    }

    private void applyDto(Subscription target, SubscriptionDto dto) {
        target.setServiceName(dto.serviceName().trim());
        target.setPrice(dto.price());
        target.setBillingDay(dto.billingDay());
        target.setPeriod(dto.period());
        target.setReminderEnabled(dto.reminderEnabled());
        target.setReminderDaysBefore(dto.reminderDaysBefore());
        target.setNotes(dto.notes() == null || dto.notes().isBlank() ? null : dto.notes().trim());
        target.setStartDate(dto.startDate() == null ? LocalDate.now() : dto.startDate());
    }
}
