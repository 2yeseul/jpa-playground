package com.example.concurrency.application

import com.example.concurrency.domain.Stock
import com.example.concurrency.domain.StockRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class StockService(
    private val stockRepository: StockRepository,
) {
    fun getStock(id: Long): Stock =
        stockRepository.findByIdOrNull(id) ?: throw RuntimeException("Stock not found")

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Synchronized
    fun decreaseWithSynchronized(id: Long, quantity: Long) =
        with(getStock(id)) {
            this.decrease(quantity)
            stockRepository.saveAndFlush(this)
        }
}

@Service
class PessimisticLockStockService(
    private val stockRepository: StockRepository,
) {

    fun getStockWithPessimisticLock(id: Long): Stock =
        stockRepository.findByIdWithPessimisticLock(id) ?: throw RuntimeException("Stock not found")

    @Transactional
    fun decreaseWithPessimisticLock(id: Long, quantity: Long) =
        with(getStockWithPessimisticLock(id)) {
            this.decrease(quantity)
            stockRepository.saveAndFlush(this)
        }
}

@Service
class OptimisticLockStockService(
    private val stockRepository: StockRepository,
) {

    fun getStockWithOptimisticLock(id: Long): Stock =
        stockRepository.findByIdWithOptimisticLock(id) ?: throw RuntimeException("Stock not found")

    @Transactional
    fun decreaseWithOptimisticLock(id: Long, quantity: Long) =
        with(getStockWithOptimisticLock(id)) {
            this.decrease(quantity)
            stockRepository.saveAndFlush(this)
        }
}
