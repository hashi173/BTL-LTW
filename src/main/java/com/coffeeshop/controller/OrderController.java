package com.coffeeshop.controller;

import com.coffeeshop.entity.Order;
import com.coffeeshop.entity.OrderStatus;
import com.coffeeshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class OrderController {

    private static final int PAGE_SIZE = 10;

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) OrderStatus status,
            @RequestParam(value = "activePage", defaultValue = "0") int activePage,
            @RequestParam(value = "historyPage", defaultValue = "0") int historyPage,
            Model model) {

        PageRequest activeRequest = PageRequest.of(activePage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));
        PageRequest historyRequest = PageRequest.of(historyPage, PAGE_SIZE, Sort.by(Sort.Direction.ASC, "id"));

        List<OrderStatus> activeStatuses = List.of(OrderStatus.PENDING, OrderStatus.CONFIRMED, OrderStatus.SHIPPING);
        List<OrderStatus> historyStatuses = List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED);

        if ((search != null && !search.isEmpty()) || status != null) {
            model.addAttribute("searchResults",
                    orderService.searchOrdersAndStatusPaginated(search, status, activeRequest));
            model.addAttribute("isSearching", true);
        } else {
            Page<Order> activeOrdersPage = orderService.getOrdersByStatusesPaginated(activeStatuses, activeRequest);
            Page<Order> historyOrdersPage = orderService.getOrdersByStatusesPaginated(historyStatuses, historyRequest);

            model.addAttribute("activeOrders", activeOrdersPage.getContent());
            model.addAttribute("activePage", activePage);
            model.addAttribute("totalActivePages", activeOrdersPage.getTotalPages());

            model.addAttribute("historyOrders", historyOrdersPage.getContent());
            model.addAttribute("historyPage", historyPage);
            model.addAttribute("totalHistoryPages", historyOrdersPage.getTotalPages());
            model.addAttribute("isSearching", false);
        }

        model.addAttribute("search", search);
        model.addAttribute("status", status);
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("totalItems", orderService.getTotalOrders());
        return "admin/orders/index";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable("id") java.util.UUID id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) {
            return "redirect:/admin/orders";
        }
        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "admin/orders/detail";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable("id") java.util.UUID id, @RequestParam("status") OrderStatus status,
            @RequestParam(value = "redirect", defaultValue = "list") String redirect) {
        orderService.updateOrderStatus(id, status);
        if ("detail".equals(redirect)) {
            return "redirect:/admin/orders/" + id;
        }
        return "redirect:/admin/orders";
    }

    @PostMapping("/{id}/cancel")
    public String cancelOrder(@PathVariable("id") java.util.UUID id) {
        orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
        return "redirect:/admin/orders/" + id;
    }
}
