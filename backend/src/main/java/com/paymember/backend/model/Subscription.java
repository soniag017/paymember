package com.paymember.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Column(nullable = false)
    private String serviceName;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private Integer billingDay;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BillingPeriod period;

    @Column(nullable = false)
    private Boolean reminderEnabled;

    @Column(nullable = false)
    private Integer reminderDaysBefore;

    @Column
    private String notes;

    @Column
    private LocalDate startDate;

    @Column
    private String customIconUri;

    @Lob
    @Column
    private byte[] customIconData;

    @Column(length = 100)
    private String customIconContentType;

    @PrePersist
    void applyDefaults() {
        if (startDate == null) {
            startDate = LocalDate.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public AppUser getUser() { return user; }
    public void setUser(AppUser user) { this.user = user; }
    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Integer getBillingDay() { return billingDay; }
    public void setBillingDay(Integer billingDay) { this.billingDay = billingDay; }
    public BillingPeriod getPeriod() { return period; }
    public void setPeriod(BillingPeriod period) { this.period = period; }
    public Boolean getReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(Boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public Integer getReminderDaysBefore() { return reminderDaysBefore; }
    public void setReminderDaysBefore(Integer reminderDaysBefore) { this.reminderDaysBefore = reminderDaysBefore; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public String getCustomIconUri() { return customIconUri; }
    public void setCustomIconUri(String customIconUri) { this.customIconUri = customIconUri; }
    public byte[] getCustomIconData() { return customIconData; }
    public void setCustomIconData(byte[] customIconData) { this.customIconData = customIconData; }
    public String getCustomIconContentType() { return customIconContentType; }
    public void setCustomIconContentType(String customIconContentType) { this.customIconContentType = customIconContentType; }
}
