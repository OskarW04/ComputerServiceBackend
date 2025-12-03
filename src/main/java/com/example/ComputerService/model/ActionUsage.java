package com.example.ComputerService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActionUsage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal currentPrice;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private RepairOrder repairOrder;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private ServiceAction serviceAction;
}
