package com.coffeeshop.entity;

/** Lifecycle statuses for a customer order. */
public enum OrderStatus {
    PENDING,    // Awaiting confirmation
    CONFIRMED,  // Accepted by admin
    SHIPPING,   // Out for delivery
    COMPLETED,  // Successfully delivered
    CANCELLED   // Cancelled by user or admin
}
