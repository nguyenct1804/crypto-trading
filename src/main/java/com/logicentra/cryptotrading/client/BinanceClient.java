package com.logicentra.cryptotrading.client;

import com.logicentra.cryptotrading.dto.BinanceTickerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "binanceClient", url = "https://api.binance.com")
public interface BinanceClient {

    @GetMapping("/api/v3/ticker/bookTicker")
    List<BinanceTickerDto> getTickers();
}