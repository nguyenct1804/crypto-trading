package com.logicentra.cryptotrading.presentation.controller;

import com.logicentra.cryptotrading.application.dto.TradeRequestDto;
import com.logicentra.cryptotrading.domain.entity.CryptoPrice;
import com.logicentra.cryptotrading.domain.entity.TradeTransaction;
import com.logicentra.cryptotrading.domain.entity.Wallet;
import com.logicentra.cryptotrading.application.service.TradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TradingController {

    private final TradingService tradingService;

    private static final Long CURRENT_USER_ID = 1L;

    @GetMapping("/prices/best")
    public ResponseEntity<CryptoPrice> getBestPrice(@RequestParam String symbol) {
        return ResponseEntity.ok(tradingService.getLatestBestPrice(symbol));
    }

    @PostMapping("/trades")
    public ResponseEntity<Void> trade(@Valid @RequestBody TradeRequestDto request) {
        tradingService.trade(CURRENT_USER_ID, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/wallets")
    public ResponseEntity<List<Wallet>> getWallets() {
        return ResponseEntity.ok(tradingService.getUserWallets(CURRENT_USER_ID));
    }

    @GetMapping("/trades")
    public ResponseEntity<List<TradeTransaction>> getTradeHistory() {
        return ResponseEntity.ok(tradingService.getUserTradeHistory(CURRENT_USER_ID));
    }
}