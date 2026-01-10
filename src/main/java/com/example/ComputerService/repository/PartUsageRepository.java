package com.example.ComputerService.repository;

import com.example.ComputerService.model.PartUsage;
import com.example.ComputerService.model.RepairOrder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartUsageRepository extends JpaRepository<PartUsage, Long> {
    List<PartUsage> findByRepairOrder(RepairOrder order);
}
