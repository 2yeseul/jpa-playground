package com.example.concurrency.application

import com.example.concurrency.domain.Stock
import com.example.concurrency.domain.StockRepository
import com.example.concurrency.facade.OptimisticLockStockFacade
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.util.concurrent.CountDownLatch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@SpringBootTest
class StockServiceTest(
    @Autowired val stockRepository: StockRepository,
    @Autowired val stockService: StockService,
    @Autowired val pessimisticLockStockService: PessimisticLockStockService,
    @Autowired val optimisticLockStockService: OptimisticLockStockService,
    @Autowired val optimisticLockStockFacade: OptimisticLockStockFacade,
) {

    @BeforeEach
    fun before() {
        val stock: Stock = Stock(
            id = 1,
            productId = 1,
            quantity = 100,
        )

        stockRepository.saveAndFlush(stock)
    }

    @AfterEach
    fun after() {
        stockRepository.deleteAll()
    }

    @Test
    fun `stock_decrease`() {
        stockService.decreaseWithSynchronized(1, 1)

        assertEquals(99, stockService.getStock(1).quantity)
    }

    @Test
    fun `동시에_100개_요청_syncronized`() {
        request(stockService::decreaseWithSynchronized)

        val updated = stockService.getStock(1)

        assertEquals(0, updated.quantity)
    }

    @Test
    fun `동시에_100개_요청_pessimistic_lock`() {
        request(pessimisticLockStockService::decreaseWithPessimisticLock)

        val updated = stockService.getStock(1)

        assertEquals(0, updated.quantity)
    }

    @Test
    fun `동시에_100개_요청_optimistic_lock`() {
        request(optimisticLockStockFacade::decrease)

        val updated = optimisticLockStockService.getStockWithOptimisticLock(1)

        assertEquals(0, updated.quantity)
    }
}

fun request(decrease: (id: Long, quantity: Long) -> Stock) {
    val threadCount = 100
    val executorService: ExecutorService = Executors.newFixedThreadPool(32)

    // 다른 쓰레드에서 수행중인 작업이 완료될 때 까지 대기하도록 도와주는 클래스
    val latch = CountDownLatch(threadCount)

    for (i in 1..threadCount) {
        executorService.submit {
            try {
                decrease(1, 1)
            } finally {
                latch.countDown()
            }
        }
    }

    latch.await()
}
