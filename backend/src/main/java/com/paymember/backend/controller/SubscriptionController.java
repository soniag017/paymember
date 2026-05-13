package com.paymember.backend.controller;

import com.paymember.backend.dto.SubscriptionDto;
import com.paymember.backend.service.SubscriptionService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @GetMapping
    public List<SubscriptionDto> getAll(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return subscriptionService.listByUser(userId);
    }

    @GetMapping("/{id}")
    public SubscriptionDto getById(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return subscriptionService.getById(userId, id);
    }

    @PostMapping
    public SubscriptionDto create(@Valid @RequestBody SubscriptionDto dto, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return subscriptionService.create(userId, dto);
    }

    @PutMapping("/{id}")
    public SubscriptionDto update(@PathVariable Long id, @Valid @RequestBody SubscriptionDto dto, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return subscriptionService.update(userId, id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        subscriptionService.delete(userId, id);
    }
}
