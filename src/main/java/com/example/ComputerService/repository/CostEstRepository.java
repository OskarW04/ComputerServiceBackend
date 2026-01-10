package com.example.ComputerService.repository;

import com.example.ComputerService.model.CostEstimate;
import com.example.ComputerService.model.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CostEstRepository extends JpaRepository<CostEstimate, Long> {
    Optional<CostEstimate> findByRepairOrder(RepairOrder order);
}
