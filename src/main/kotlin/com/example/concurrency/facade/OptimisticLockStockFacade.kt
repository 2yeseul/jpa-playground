package com.example.concurrency.facade

import com.example.concurrency.application.OptimisticLockStockService
import com.example.concurrency.domain.Stock
import org.springframework.stereotype.Service

@Service
class OptimisticLockStockFacade(private val optimisticLockStockService: OptimisticLockStockService) {

    @Throws(InterruptedException::class)
    fun decrease(id: Long, quantity: Long): Stock {
        while (true) {
            try {
                return optimisticLockStockService.decreaseWithOptimisticLock(id, quantity)
                break
            } catch (e: Exception) {
                Thread.sleep(50)
            }
        }
    }
}
