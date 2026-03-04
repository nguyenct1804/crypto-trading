package com.logicentra.cryptotrading.application.dto;

import lombok.Data;
import java.util.List;

@Data
public class HuobiResponseDto {
    private List<HuobiTickerDto> data;
}