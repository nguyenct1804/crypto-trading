package com.logicentra.cryptotrading.application.service.Impl;

import com.logicentra.cryptotrading.application.dto.TradeRequestDto;
import com.logicentra.cryptotrading.domain.entity.CryptoPrice;
import com.logicentra.cryptotrading.domain.entity.TradeTransaction;
import com.logicentra.cryptotrading.domain.entity.TradeType;
import com.logicentra.cryptotrading.domain.entity.Wallet;
import com.logicentra.cryptotrading.infrastructure.repository.CryptoPriceRepository;
import com.logicentra.cryptotrading.infrastructure.repository.TradeTransactionRepository;
import com.logicentra.cryptotrading.infrastructure.repository.WalletRepository;
import com.logicentra.cryptotrading.application.service.TradingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TradingServiceImpl implements TradingService {

    private final WalletRepository walletRepository;
    private final CryptoPriceRepository cryptoPriceRepository;
    private final TradeTransactionRepository tradeTransactionRepository;

    @Override
    public CryptoPrice getLatestBestPrice(String symbol) {
        return cryptoPriceRepository.findTopBySymbolOrderByCreatedAtDesc(symbol)
                .orElseThrow(() -> new IllegalArgumentException("Price not found for symbol: " + symbol));
    }

    @Override
    @Transactional
    public void trade(Long userId, TradeRequestDto request) {
        CryptoPrice latestPrice = getLatestBestPrice(request.getSymbol());
        String cryptoCurrency = request.getSymbol().replace("USDT", "");

        if (request.getType() == TradeType.BUY) {
            executeBuy(userId, cryptoCurrency, request.getAmount(), latestPrice.getBestAsk(), request.getSymbol());
        } else {
            executeSell(userId, cryptoCurrency, request.getAmount(), latestPrice.getBestBid(), request.getSymbol());
        }
    }
    private void executeBuy(Long userId, String cryptoCurrency, BigDecimal amount, BigDecimal price, String symbol) {
        BigDecimal totalUsdtNeeded = amount.multiply(price);

        Wallet usdtWallet = getWallet(userId, "USDT");
        usdtWallet.debit(totalUsdtNeeded);
        walletRepository.save(usdtWallet);

        Wallet cryptoWallet = getOrCreateWallet(userId, cryptoCurrency);
        cryptoWallet.credit(amount);
        walletRepository.save(cryptoWallet);

        saveTransaction(userId, symbol, TradeType.BUY, price, amount, totalUsdtNeeded);
    }

    private void executeSell(Long userId, String cryptoCurrency, BigDecimal amount, BigDecimal price, String symbol) {
        Wallet cryptoWallet = getWallet(userId, cryptoCurrency);
        cryptoWallet.debit(amount);
        walletRepository.save(cryptoWallet);

        BigDecimal totalUsdtEarned = amount.multiply(price);
        Wallet usdtWallet = getOrCreateWallet(userId, "USDT");
        usdtWallet.credit(totalUsdtEarned);
        walletRepository.save(usdtWallet);

        saveTransaction(userId, symbol, TradeType.SELL, price, amount, totalUsdtEarned);
    }

    private Wallet getWallet(Long userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseThrow(() -> new IllegalStateException("Wallet not found for currency: " + currency));
    }

    private Wallet getOrCreateWallet(Long userId, String currency) {
        return walletRepository.findByUserIdAndCurrency(userId, currency)
                .orElseGet(() -> Wallet.builder()
                        .userId(userId)
                        .currency(currency)
                        .balance(BigDecimal.ZERO)
                        .build());
    }

    private void saveTransaction(Long userId, String symbol, TradeType type, BigDecimal price, BigDecimal amount, BigDecimal total) {
        TradeTransaction transaction = TradeTransaction.builder()
                .userId(userId)
                .symbol(symbol)
                .type(type)
                .price(price)
                .amount(amount)
                .total(total)
                .build();
        tradeTransactionRepository.save(transaction);
    }

    @Override
    public List<Wallet> getUserWallets(Long userId) {
        return walletRepository.findByUserId(userId);
    }

    @Override
    public List<TradeTransaction> getUserTradeHistory(Long userId) {
        return tradeTransactionRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}