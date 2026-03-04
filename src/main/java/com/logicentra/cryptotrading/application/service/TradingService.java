package com.logicentra.cryptotrading.application.service;

import com.logicentra.cryptotrading.application.dto.TradeRequestDto;
import com.logicentra.cryptotrading.domain.entity.CryptoPrice;
import com.logicentra.cryptotrading.domain.entity.TradeTransaction;
import com.logicentra.cryptotrading.domain.entity.Wallet;

import java.util.List;

public interface TradingService {
    CryptoPrice getLatestBestPrice(String symbol);
    void trade(Long userId, TradeRequestDto request);
    List<Wallet> getUserWallets(Long userId);
    List<TradeTransaction> getUserTradeHistory(Long userId);
}