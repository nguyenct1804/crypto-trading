package com.logicentra.cryptotrading.service;

import com.logicentra.cryptotrading.dto.TradeRequestDto;
import com.logicentra.cryptotrading.entity.CryptoPrice;
import com.logicentra.cryptotrading.entity.TradeTransaction;
import com.logicentra.cryptotrading.entity.Wallet;

import java.util.List;

public interface TradingService {
    CryptoPrice getLatestBestPrice(String symbol);
    void trade(Long userId, TradeRequestDto request);
    List<Wallet> getUserWallets(Long userId);
    List<TradeTransaction> getUserTradeHistory(Long userId);
}