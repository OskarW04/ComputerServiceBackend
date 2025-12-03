package com.example.ComputerService.model;

import com.example.ComputerService.model.enums.RepairOrderStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderNumber;
    private LocalDateTime createdAt;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Column(length = 500)
    private String deviceDescription;

    @Column(length = 500)
    private String problemDescription;

    @Enumerated(EnumType.STRING)
    private RepairOrderStatus status;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "assigned_technician_id")
    private Employee assignedTechnician;

    @Column(columnDefinition = "TEXT")
    private String managerNotes;
}
