package com.coffeeshop;

import com.coffeeshop.entity.Expense;
import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.OrderItem;
import com.coffeeshop.entity.OrderStatus;
import com.coffeeshop.entity.Role;
import com.coffeeshop.entity.User;
import com.coffeeshop.repository.ExpenseRepository;
import com.coffeeshop.repository.OrderItemRepository;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.IntStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AdminPageRenderTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private ExpenseRepository expenseRepository;



    @Test
    @WithMockUser(roles = "ADMIN")
    void historyPageRenders() throws Exception {
        seedHistory();

        mockMvc.perform(get("/admin/history"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/history"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void historyDetailsPageRenders() throws Exception {
        seedHistory();

        mockMvc.perform(get("/admin/history/details")
                        .param("month", "4")
                        .param("year", "2026"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void orderDetailsPageRenders() throws Exception {
        UUID orderId = seedOrderDetail();

        mockMvc.perform(get("/admin/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/orders/detail"));
    }



    private void seedHistory() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        expenseRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("history-admin");
        admin.setPassword("secret");
        admin.setFullName("History Admin");
        admin.setRole(Role.ADMIN);
        admin.setUserCode("ADM99");
        admin.setPhone("0901234567");
        admin.setHourlyRate(50000.0);
        admin.setActive(true);
        User savedUser = userRepository.save(admin);

        IntStream.rangeClosed(1, 25).forEach(index -> {
            Order aprilOrder = new Order();
            aprilOrder.setUser(savedUser);
            aprilOrder.setCustomerName("Customer A" + index);
            aprilOrder.setOrderType(index % 2 == 0 ? "In-Store" : "Takeaway");
            aprilOrder.setStatus(OrderStatus.COMPLETED);
            aprilOrder.setOrderStatus(OrderStatus.COMPLETED.name());
            aprilOrder.setTrackingCode(String.format("APR%03d", index));
            aprilOrder.setTotalAmount(180000.0 + index * 1000);
            aprilOrder.setGrandTotal(BigDecimal.valueOf(180000.0 + index * 1000));
            aprilOrder.setCreatedAt(LocalDateTime.of(2026, 4, Math.min(index, 25), 9, 30));
            orderRepository.save(aprilOrder);
        });

        Order marchOrder = new Order();
        marchOrder.setUser(savedUser);
        marchOrder.setCustomerName("Customer B");
        marchOrder.setOrderType("Takeaway");
        marchOrder.setStatus(OrderStatus.COMPLETED);
        marchOrder.setOrderStatus(OrderStatus.COMPLETED.name());
        marchOrder.setTrackingCode("MAR001");
        marchOrder.setTotalAmount(125000.0);
        marchOrder.setGrandTotal(BigDecimal.valueOf(125000.0));
        marchOrder.setCreatedAt(LocalDateTime.of(2026, 3, 15, 14, 0));
        orderRepository.save(marchOrder);

        Expense aprilExpense = new Expense();
        aprilExpense.setDescription("April beans");
        aprilExpense.setCategory("Ingredients");
        aprilExpense.setAmount(70000.0);
        aprilExpense.setExpenseDate(LocalDate.of(2026, 4, 5));
        expenseRepository.save(aprilExpense);

        Expense marchExpense = new Expense();
        marchExpense.setDescription("March utilities");
        marchExpense.setCategory("Utilities");
        marchExpense.setAmount(55000.0);
        marchExpense.setExpenseDate(LocalDate.of(2026, 3, 8));
        expenseRepository.save(marchExpense);
    }

    private UUID seedOrderDetail() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        userRepository.deleteAll();

        User admin = new User();
        admin.setUsername("orders-admin");
        admin.setPassword("secret");
        admin.setFullName("Orders Admin");
        admin.setRole(Role.ADMIN);
        admin.setUserCode("ADM10");
        admin.setPhone("0901111111");
        admin.setHourlyRate(60000.0);
        admin.setActive(true);
        User savedUser = userRepository.save(admin);

        Order order = new Order();
        order.setUser(savedUser);
        order.setCustomerName("Order Detail Customer");
        order.setPhone("0902222222");
        order.setAddress("123 Nguyen Trai");
        order.setNote("Less sugar");
        order.setStatus(OrderStatus.PENDING);
        order.setOrderStatus(OrderStatus.PENDING.name());
        order.setTrackingCode("ORD-DETAIL");
        order.setTotalAmount(120000.0);
        order.setGrandTotal(BigDecimal.valueOf(120000.0));
        Order savedOrder = orderRepository.save(order);

        OrderItem item = new OrderItem();
        item.setOrder(savedOrder);
        item.setSnapshotProductName("Matcha Latte");
        item.setSnapshotUnitPrice(BigDecimal.valueOf(60000.0));
        item.setQuantity(2);
        item.setSubTotal(BigDecimal.valueOf(120000.0));
        item.setSnapshotOptions("{\"summary\":\"Size: Large | Options: Pearl | Sugar: 50% | Ice: 100%\"}");
        orderItemRepository.save(item);

        return savedOrder.getId();
    }
}
