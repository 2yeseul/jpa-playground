package com.example.concurrency.domain

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface LockRepository : JpaRepository<Stock, Long> {
    @Query("select get_lock(:key, 3000)", nativeQuery = true)
    fun getLock(key: String)

    @Query("select release_lock(:key)", nativeQuery = true)
    fun releaseLock(key: String)
}
