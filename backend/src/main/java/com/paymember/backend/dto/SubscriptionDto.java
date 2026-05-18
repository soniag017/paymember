package com.paymember.backend.dto;

import com.paymember.backend.model.BillingPeriod;
import com.paymember.backend.model.Subscription;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.time.LocalDate;

public record SubscriptionDto(
    Long id,
    @NotBlank String serviceName,
    @NotNull @Positive Double price,
    @NotNull @Min(1) @Max(31) Integer billingDay,
    @NotNull BillingPeriod period,
    @NotNull Boolean reminderEnabled,
    @NotNull @Min(0) @Max(30) Integer reminderDaysBefore,
    String notes,
    @PastOrPresent LocalDate startDate
) {
    public static SubscriptionDto fromEntity(Subscription entity) {
        LocalDate startDate = entity.getStartDate() == null ? LocalDate.now() : entity.getStartDate();
        return new SubscriptionDto(
            entity.getId(),
            entity.getServiceName(),
            entity.getPrice(),
            entity.getBillingDay(),
            entity.getPeriod(),
            entity.getReminderEnabled(),
            entity.getReminderDaysBefore(),
            entity.getNotes(),
            startDate
        );
    }
}
