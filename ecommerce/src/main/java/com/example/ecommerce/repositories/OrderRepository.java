package com.example.ecommerce.repositories;

import com.example.ecommerce.entities.Order;
import com.example.ecommerce.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByBuyer(User buyer);
    List<Order> findByTime(Date time);

    @Query("select p from Order p where p.time > ?1 and p.time < ?2 and p.buyer = ?3")
    List<Order> findByBuyerInPeriod(Date startDate, Date endDate, User user);

}
