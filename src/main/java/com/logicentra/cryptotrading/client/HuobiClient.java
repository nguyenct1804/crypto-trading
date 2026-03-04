package com.logicentra.cryptotrading.client;

import com.logicentra.cryptotrading.dto.HuobiResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "huobiClient", url = "https://api.huobi.pro")
public interface HuobiClient {

    @GetMapping("/market/tickers")
    HuobiResponseDto getTickers();
}