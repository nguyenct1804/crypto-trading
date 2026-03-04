package com.logicentra.cryptotrading.repository;

import com.logicentra.cryptotrading.entity.CryptoPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CryptoPriceRepository extends JpaRepository<CryptoPrice, Long> {
    Optional<CryptoPrice> findTopBySymbolOrderByCreatedAtDesc(String symbol);
}