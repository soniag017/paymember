package com.paymember.backend.dto;

import com.paymember.backend.model.BillingPeriod;
import com.paymember.backend.model.Subscription;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubscriptionDto(
    Long id,
    @NotBlank String serviceName,
    @NotNull @Positive Double price,
    @NotNull @Min(1) @Max(31) Integer billingDay,
    @NotNull BillingPeriod period,
    @NotNull Boolean reminderEnabled,
    @NotNull @Min(0) @Max(30) Integer reminderDaysBefore,
    String notes
) {
    public static SubscriptionDto fromEntity(Subscription entity) {
        return new SubscriptionDto(
            entity.getId(),
            entity.getServiceName(),
            entity.getPrice(),
            entity.getBillingDay(),
            entity.getPeriod(),
            entity.getReminderEnabled(),
            entity.getReminderDaysBefore(),
            entity.getNotes()
        );
    }
}
