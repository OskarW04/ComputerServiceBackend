package com.example.ComputerService.model;

import com.example.ComputerService.model.enums.PartOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate orderDate;
    private LocalDate estimatedDelivery;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private PartOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "spare_part_id")
    private SparePart sparePart;
}
