package com.coffeeshop;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.OrderStatus;
import com.coffeeshop.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
@ActiveProfiles("test")
class OrderRepositorySearchTest {

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void searchOrdersMatchesPhoneAndTrackingCode() {
        Order order = new Order();
        order.setCustomerName("Nguyen Van A");
        order.setPhone("0901234567");
        order.setTrackingCode("ORD-ABC123");
        order.setStatus(OrderStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING.name());
        order.setTotalAmount(50000.0);
        orderRepository.save(order);

        Page<Order> phoneResults = orderRepository.searchOrdersPaginated("090123", PageRequest.of(0, 10));
        Page<Order> trackingResults = orderRepository.searchOrdersPaginated("abc123", PageRequest.of(0, 10));

        assertEquals(1, phoneResults.getTotalElements());
        assertEquals(1, trackingResults.getTotalElements());
    }
}
