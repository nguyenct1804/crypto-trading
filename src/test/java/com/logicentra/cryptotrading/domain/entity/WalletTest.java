package com.logicentra.cryptotrading.domain.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void shouldCreditSuccessfully() {
        Wallet wallet = Wallet.builder()
                .userId(1L)
                .currency("USDT")
                .balance(new BigDecimal("100.0"))
                .build();

        wallet.credit(new BigDecimal("50.0"));

        assertEquals(new BigDecimal("150.0"), wallet.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenCreditNegativeAmount() {
        Wallet wallet = Wallet.builder()
                .userId(1L)
                .currency("USDT")
                .balance(new BigDecimal("100.0"))
                .build();

        assertThrows(IllegalArgumentException.class, () -> wallet.credit(new BigDecimal("-10.0")));
    }

    @Test
    void shouldDebitSuccessfully() {
        Wallet wallet = Wallet.builder()
                .userId(1L)
                .currency("USDT")
                .balance(new BigDecimal("100.0"))
                .build();

        wallet.debit(new BigDecimal("40.0"));

        assertEquals(new BigDecimal("60.0"), wallet.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenDebitAmountExceedsBalance() {
        Wallet wallet = Wallet.builder()
                .userId(1L)
                .currency("USDT")
                .balance(new BigDecimal("100.0"))
                .build();

        assertThrows(IllegalStateException.class, () -> wallet.debit(new BigDecimal("150.0")));
    }
}