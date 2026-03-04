package com.logicentra.cryptotrading.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "crypto_price")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CryptoPrice {
    //This can be a UUID in real project to make sure it is unique in microservices environment
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String symbol;

    @Column(name = "best_bid", nullable = false, precision = 19, scale = 8)
    private BigDecimal bestBid;

    @Column(name = "best_ask", nullable = false, precision = 19, scale = 8)
    private BigDecimal bestAsk;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}