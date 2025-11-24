package com.example.racketmanager.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.model.User;

public interface RacketOrderRepository extends JpaRepository<RacketOrder, Long> {

    List<RacketOrder> findAllByOrderByDueDateAsc();

    List<RacketOrder> findByCustomer(User customer);

    List<RacketOrder> findByDueDateBetween(LocalDate start, LocalDate end);
}
