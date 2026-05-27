package com.paymember.backend.service;

import com.paymember.backend.dto.SubscriptionDto;
import com.paymember.backend.model.AppUser;
import com.paymember.backend.model.Subscription;
import com.paymember.backend.repository.AppUserRepository;
import com.paymember.backend.repository.SubscriptionRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Locale;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class SubscriptionService {
    private static final long MAX_ICON_BYTES = 2L * 1024L * 1024L;

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

    public SubscriptionDto uploadIcon(Long userId, Long id, MultipartFile file) {
        Subscription entity = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Icon file is required");
        }
        if (file.getSize() > MAX_ICON_BYTES) {
            throw new IllegalArgumentException("Icon file is too large");
        }

        String contentType = file.getContentType() == null
            ? "application/octet-stream"
            : file.getContentType().toLowerCase(Locale.ROOT);
        if (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/webp")) {
            throw new IllegalArgumentException("Icon file must be JPG, PNG or WEBP");
        }

        try {
            entity.setCustomIconData(file.getBytes());
            entity.setCustomIconContentType(contentType);
            entity.setCustomIconUri(null);
            return SubscriptionDto.fromEntity(subscriptionRepository.save(entity));
        } catch (IOException ex) {
            throw new IllegalArgumentException("Could not read icon file");
        }
    }

    public SubscriptionIcon getIcon(Long userId, Long id) {
        Subscription entity = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        byte[] data = entity.getCustomIconData();
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Subscription icon not found");
        }
        String contentType = entity.getCustomIconContentType() == null
            ? "image/jpeg"
            : entity.getCustomIconContentType();
        return new SubscriptionIcon(data, contentType);
    }

    public SubscriptionDto deleteIcon(Long userId, Long id) {
        Subscription entity = subscriptionRepository.findByIdAndUserId(id, userId)
            .orElseThrow(() -> new IllegalArgumentException("Subscription not found"));
        entity.setCustomIconData(null);
        entity.setCustomIconContentType(null);
        entity.setCustomIconUri(null);
        return SubscriptionDto.fromEntity(subscriptionRepository.save(entity));
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

    public record SubscriptionIcon(byte[] data, String contentType) {}
}
