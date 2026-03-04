package com.logicentra.cryptotrading.service.Impl;

import com.logicentra.cryptotrading.dto.TradeRequestDto;
import com.logicentra.cryptotrading.entity.CryptoPrice;
import com.logicentra.cryptotrading.entity.TradeTransaction;
import com.logicentra.cryptotrading.entity.TradeType;
import com.logicentra.cryptotrading.entity.Wallet;
import com.logicentra.cryptotrading.repository.CryptoPriceRepository;
import com.logicentra.cryptotrading.repository.TradeTransactionRepository;
import com.logicentra.cryptotrading.repository.WalletRepository;
import com.logicentra.cryptotrading.service.TradingService;
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

        if (usdtWallet.getBalance().compareTo(totalUsdtNeeded) < 0) {
            throw new IllegalStateException("Insufficient USDT balance");
        }

        usdtWallet.setBalance(usdtWallet.getBalance().subtract(totalUsdtNeeded));
        walletRepository.save(usdtWallet);

        Wallet cryptoWallet = getOrCreateWallet(userId, cryptoCurrency);
        cryptoWallet.setBalance(cryptoWallet.getBalance().add(amount));
        walletRepository.save(cryptoWallet);

        saveTransaction(userId, symbol, TradeType.BUY, price, amount, totalUsdtNeeded);
    }

    private void executeSell(Long userId, String cryptoCurrency, BigDecimal amount, BigDecimal price, String symbol) {
        Wallet cryptoWallet = getWallet(userId, cryptoCurrency);

        if (cryptoWallet.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient " + cryptoCurrency + " balance");
        }

        cryptoWallet.setBalance(cryptoWallet.getBalance().subtract(amount));
        walletRepository.save(cryptoWallet);

        BigDecimal totalUsdtEarned = amount.multiply(price);
        Wallet usdtWallet = getOrCreateWallet(userId, "USDT");
        usdtWallet.setBalance(usdtWallet.getBalance().add(totalUsdtEarned));
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