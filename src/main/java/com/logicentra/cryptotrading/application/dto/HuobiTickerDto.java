package com.logicentra.cryptotrading.application.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class HuobiTickerDto {
    private String symbol;
    private BigDecimal bid;
    private BigDecimal ask;
}