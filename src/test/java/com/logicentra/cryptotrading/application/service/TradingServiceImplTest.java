package com.logicentra.cryptotrading.application.service;

import com.logicentra.cryptotrading.application.dto.TradeRequestDto;
import com.logicentra.cryptotrading.application.service.Impl.TradingServiceImpl;
import com.logicentra.cryptotrading.domain.entity.CryptoPrice;
import com.logicentra.cryptotrading.domain.entity.TradeType;
import com.logicentra.cryptotrading.domain.entity.Wallet;
import com.logicentra.cryptotrading.infrastructure.repository.CryptoPriceRepository;
import com.logicentra.cryptotrading.infrastructure.repository.TradeTransactionRepository;
import com.logicentra.cryptotrading.infrastructure.repository.WalletRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TradingServiceImplTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CryptoPriceRepository cryptoPriceRepository;

    @Mock
    private TradeTransactionRepository tradeTransactionRepository;

    @InjectMocks
    private TradingServiceImpl tradingService;

    private Wallet usdtWallet;
    private CryptoPrice ethPrice;

    @BeforeEach
    void setUp() {
        usdtWallet = Wallet.builder()
                .userId(1L)
                .currency("USDT")
                .balance(new BigDecimal("50000.0"))
                .build();

        ethPrice = CryptoPrice.builder()
                .symbol("ETHUSDT")
                .bestBid(new BigDecimal("2000.0"))
                .bestAsk(new BigDecimal("2010.0"))
                .build();
    }

    @Test
    void shouldExecuteBuyTradeSuccessfully() {
        TradeRequestDto request = new TradeRequestDto();
        request.setSymbol("ETHUSDT");
        request.setType(TradeType.BUY);
        request.setAmount(new BigDecimal("2.0"));

        when(cryptoPriceRepository.findTopBySymbolOrderByCreatedAtDesc("ETHUSDT"))
                .thenReturn(Optional.of(ethPrice));
        when(walletRepository.findByUserIdAndCurrency(1L, "USDT"))
                .thenReturn(Optional.of(usdtWallet));
        when(walletRepository.findByUserIdAndCurrency(1L, "ETH"))
                .thenReturn(Optional.empty());

        tradingService.trade(1L, request);
        assertNotNull(usdtWallet.getBalance());
        verify(walletRepository, times(2)).save(any(Wallet.class));
        verify(tradeTransactionRepository, times(1)).save(any());
    }
}