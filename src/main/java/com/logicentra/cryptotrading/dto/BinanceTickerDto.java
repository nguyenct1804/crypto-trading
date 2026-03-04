package com.logicentra.cryptotrading.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class BinanceTickerDto {
    private String symbol;
    private BigDecimal bidPrice;
    private BigDecimal askPrice;
}