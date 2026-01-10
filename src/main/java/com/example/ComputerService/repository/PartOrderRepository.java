package com.example.ComputerService.repository;

import com.example.ComputerService.model.PartOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartOrderRepository extends JpaRepository<PartOrder, Long> {
}
