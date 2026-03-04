package com.logicentra.cryptotrading.application.dto;

import com.logicentra.cryptotrading.domain.entity.TradeType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class TradeRequestDto {

    @NotBlank
    private String symbol;

    @NotNull
    private TradeType type;

    @NotNull
    @DecimalMin("0.00000001")
    private BigDecimal amount;
}