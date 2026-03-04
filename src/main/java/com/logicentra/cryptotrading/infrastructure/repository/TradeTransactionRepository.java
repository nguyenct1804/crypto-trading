package com.logicentra.cryptotrading.infrastructure.repository;

import com.logicentra.cryptotrading.domain.entity.TradeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TradeTransactionRepository extends JpaRepository<TradeTransaction, Long> {
    List<TradeTransaction> findByUserIdOrderByCreatedAtDesc(Long userId);
}