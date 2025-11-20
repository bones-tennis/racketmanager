package com.example.racketmanager.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.racketmanager.model.RacketOrder;
import com.example.racketmanager.model.User;

public interface RacketOrderRepository extends JpaRepository<RacketOrder, Long> {
    // 51行目用
    List<RacketOrder> findAllByOrderByDueDateAsc();

    // CustomerController用（ユーザーに紐づく注文）
    List<RacketOrder> findByCustomer(User customer);
}
