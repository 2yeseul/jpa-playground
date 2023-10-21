package com.example.concurrency.facade

import com.example.concurrency.application.StockService
import com.example.concurrency.domain.LockRepository
import com.example.concurrency.domain.Stock
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class NamedLockFacade(
    private val lockRepository: LockRepository,
    private val stockService: StockService,
) {
    @Transactional
    fun decrease(id: Long, quantity: Long): Stock {
        try {
            lockRepository.getLock(id.toString())
            return stockService.decreaseWithSynchronized(id, quantity)
        } finally {
            lockRepository.releaseLock(id.toString())
        }
    }
}
