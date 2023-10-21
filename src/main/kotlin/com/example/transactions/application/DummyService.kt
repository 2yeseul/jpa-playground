package com.example.transactions.application

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Entity
data class Dummy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
)

@Entity
data class AnotherDummy(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
)

interface DummyRepository : JpaRepository<Dummy, Long>

interface AnotherDummyRepository : JpaRepository<AnotherDummy, Long>

@Service
class DummyService(
    private val dummyRepository: DummyRepository,
) {
    @Transactional
    fun create() =
        dummyRepository.save(Dummy(0))
}

@Service
class AnotherDummyService(
    private val anotherDummyRepository: AnotherDummyRepository,
    private val dummyService: DummyService,
) {
    @Transactional(propagation = Propagation.REQUIRED) // default
    fun createWithDefault() =
        anotherDummyRepository.save(AnotherDummy(0))

    @Transactional(propagation = Propagation.SUPPORTS) // 트랜잭션이 존재하면 기존 트랜잭션 이용, 없으면 트랜잭션 없이 수행
    fun createWithSupport() {
        dummyService.create() // 이 함수의 트랜잭션 이용
        anotherDummyRepository.save(AnotherDummy(0))
    }

    @Transactional(propagation = Propagation.MANDATORY) // 트랜잭션이 존재하면 기존 트랜잭션 이용, 없으면 예외 발생
    fun createWithMandatory() {
        dummyService.create() // 없으면 예외 발생
        anotherDummyRepository.save(AnotherDummy(0))
    }

    @Transactional(propagation = Propagation.NEVER) // 트랜잭션이 존재하면 예외 발생, 없으면 정상수행(트랜잭션 없이)
    fun createWithNever() {
        dummyService.create() // 없으면 정상 수행, 현재는 예외 발생
        anotherDummyRepository.save(AnotherDummy(0))
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED) // 트랜잭션 존재 시 종료될 때 까지 대기한 후 실행, 미존재 시 없이 로직수행
    fun createWithNotSupported() {
        dummyService.create() // 얘가 종료될 떄 까지 대기함
        anotherDummyRepository.save(AnotherDummy(0))
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW) // 트랜잭션 존재 시 종료될 때 까지 대기한 후 새로운 트랜잭셩 생성하여 실행, 미존재 시 새로운 트랜잭션 생성
    fun createWithRequiresNew() {
        dummyService.create() // 얘가 종료될 떄 까지 대기함
        anotherDummyRepository.save(AnotherDummy(0)) // 새로운 트랜잭션 생성
    }
}
