package com.logicentra.cryptotrading.scheduler;

import com.logicentra.cryptotrading.client.BinanceClient;
import com.logicentra.cryptotrading.client.HuobiClient;
import com.logicentra.cryptotrading.dto.BinanceTickerDto;
import com.logicentra.cryptotrading.dto.HuobiTickerDto;
import com.logicentra.cryptotrading.entity.CryptoPrice;
import com.logicentra.cryptotrading.repository.CryptoPriceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class PriceAggregatorScheduler {

    private final BinanceClient binanceClient;
    private final HuobiClient huobiClient;
    private final CryptoPriceRepository cryptoPriceRepository;

    private static final List<String> SUPPORTED_SYMBOLS = List.of("ETHUSDT", "BTCUSDT");

    @Scheduled(fixedRate = 10000)
    public void aggregatePrices() {
        try {
            List<BinanceTickerDto> binanceTickers = binanceClient.getTickers();
            List<HuobiTickerDto> huobiTickers = huobiClient.getTickers().getData();

            for (String symbol : SUPPORTED_SYMBOLS) {
                Optional<BinanceTickerDto> binanceData = binanceTickers.stream()
                        .filter(t -> t.getSymbol().equalsIgnoreCase(symbol))
                        .findFirst();

                Optional<HuobiTickerDto> huobiData = huobiTickers.stream()
                        .filter(t -> t.getSymbol().equalsIgnoreCase(symbol))
                        .findFirst();

                if (binanceData.isPresent() && huobiData.isPresent()) {
                    BigDecimal binanceBid = binanceData.get().getBidPrice();
                    BigDecimal binanceAsk = binanceData.get().getAskPrice();

                    BigDecimal huobiBid = huobiData.get().getBid();
                    BigDecimal huobiAsk = huobiData.get().getAsk();

                    BigDecimal bestBid = binanceBid.max(huobiBid);
                    BigDecimal bestAsk = binanceAsk.min(huobiAsk);

                    CryptoPrice cryptoPrice = CryptoPrice.builder()
                            .symbol(symbol.toUpperCase())
                            .bestBid(bestBid)
                            .bestAsk(bestAsk)
                            .build();

                    cryptoPriceRepository.save(cryptoPrice);
                    log.info("Saved aggregated price for {}: Bid={}, Ask={}", symbol, bestBid, bestAsk);
                }
            }
        } catch (Exception e) {
            log.error("Error aggregating prices", e);
        }
    }
}