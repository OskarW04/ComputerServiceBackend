package com.example.ComputerService.model;

import com.example.ComputerService.model.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String invoiceNumber;
    private LocalDate issueDate;

    // Kwota końcowa na fakturze
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @OneToOne
    @JoinColumn(name = "order_id", unique = true)
    private RepairOrder repairOrder;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;
}