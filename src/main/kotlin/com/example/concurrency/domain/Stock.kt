package com.example.concurrency.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Version

@Entity
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    val productId: Long,

    var quantity: Long,

    @Version
    var version: Long = 0,
) {
    fun decrease(quantity: Long) {
        if (this.quantity - quantity < 0) {
            throw RuntimeException("Not enough stock")
        }

        this.quantity = this.quantity - quantity
    }
}
