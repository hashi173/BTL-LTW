package com.coffeeshop;

import com.coffeeshop.dto.Cart;
import com.coffeeshop.dto.CartItem;
import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.OrderItem;
import com.coffeeshop.repository.OrderItemRepository;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.service.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private OrderService orderService;

    @Captor
    private ArgumentCaptor<OrderItem> orderItemCaptor;

    @Test
    void placeOrderStoresReadableSnapshotOptions() {
        CartItem item = new CartItem();
        item.setProductId(UUID.randomUUID());
        item.setProductName("Brown Sugar Milk Tea");
        item.setSizeName("Large");
        item.setPrice(65000.0);
        item.setQuantity(2);
        item.setToppingNames(List.of("Pearl", "Cheese Foam"));
        item.setNote("Less sweet");

        LinkedHashMap<String, String> attributes = new LinkedHashMap<>();
        attributes.put("sugar", "50%");
        attributes.put("ice", "30%");
        item.setAttributes(attributes);

        Cart cart = new Cart();
        cart.setItems(List.of(item));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        orderService.placeOrder(cart, null, "Test User", "0900000000", "123 Street", "Call on arrival");

        verify(orderItemRepository).save(orderItemCaptor.capture());
        OrderItem savedItem = orderItemCaptor.getValue();

        assertEquals("PENDING", savedItem.getOrder().getOrderStatus());
        assertTrue(savedItem.getSnapshotOptions().contains("Size: Large"));
        assertTrue(savedItem.getSnapshotOptions().contains("Options: Pearl, Cheese Foam"));
        assertTrue(savedItem.getSnapshotOptions().contains("Sugar: 50%"));
        assertTrue(savedItem.getSnapshotOptions().contains("Ice: 30%"));
        assertTrue(savedItem.getSnapshotOptions().contains("Note: Less sweet"));
    }
}
