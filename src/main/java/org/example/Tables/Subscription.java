package org.example.Tables;

import jakarta.persistence.*;

import java.time.LocalDateTime;

// Needs to be executed in SQL
// CREATE BITMAP INDEX idx_subscription_status ON Subscription(status);
// Creates bitmap indexes to fastly filter status of subscription
// CREATE INDEX idx_plan_name ON Subscription (UPPER(plan_name));
// Creates functional index on plan name to faster search different subscriptions
@Entity
@Table
public class Subscription {
    // Tracks user subscription plans
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "user_id")
    private User user;

    private String plan_name;
    private Integer price;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    @Enumerated(EnumType.STRING)
    private SubscriptionStatus status;

    public String getPlan_name() {
        return plan_name;
    }

    public Long getId() {
        return id;
    }

    public void setPlan_name(String plan_name) {
        this.plan_name = plan_name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime end_date) {
        this.end_date = end_date;
    }

    public SubscriptionStatus getStatus() {
        return status;
    }

    public void setStatus(SubscriptionStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
