package com.logicentra.cryptotrading.config;

import com.logicentra.cryptotrading.entity.Wallet;
import com.logicentra.cryptotrading.repository.WalletRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataSeeder {

    @Bean
    public CommandLineRunner initDatabase(WalletRepository walletRepository) {
        return args -> {
            Long defaultUserId = 1L;
            String defaultCurrency = "USDT";

            if (walletRepository.findByUserIdAndCurrency(defaultUserId, defaultCurrency).isEmpty()) {
                Wallet initialWallet = Wallet.builder()
                        .userId(defaultUserId)
                        .currency(defaultCurrency)
                        .balance(new BigDecimal("50000.00000000"))
                        .build();
                walletRepository.save(initialWallet);
            }
        };
    }
}