package com.example.ComputerService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CostEstimate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean approved;
    private LocalDateTime createdAt;
    private BigDecimal partsCost;
    private BigDecimal labourCost;
    private BigDecimal totalCost;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private RepairOrder repairOrder;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        BigDecimal labour = this.labourCost != null ? this.labourCost : BigDecimal.ZERO;
        BigDecimal parts = this.partsCost != null ? this.partsCost : BigDecimal.ZERO;
        this.totalCost = labour.add(parts);
    }
}
