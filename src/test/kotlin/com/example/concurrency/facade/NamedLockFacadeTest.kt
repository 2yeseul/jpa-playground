package com.example.concurrency.facade

import com.example.concurrency.application.request
import com.example.concurrency.domain.Stock
import com.example.concurrency.domain.StockRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class NamedLockFacadeTest(
    @Autowired val namedLockFacade: NamedLockFacade,
    @Autowired val stockRepository: StockRepository,
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
    fun `동시에_100개_요청_named_lock`() {
        request(namedLockFacade::decrease)

        val updated = stockRepository.findByIdOrNull(1)

        Assertions.assertEquals(0, updated!!.quantity)
    }
}
