package com.coffeeshop;

import com.coffeeshop.controller.HomeController;
import com.coffeeshop.entity.JobPosting;
import com.coffeeshop.repository.JobPostingRepository;
import com.coffeeshop.service.CategoryService;
import com.coffeeshop.service.ProductService;
import com.coffeeshop.service.ToppingService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private ToppingService toppingService;

    @Mock
    private JobPostingRepository jobPostingRepository;

    @Mock
    private jakarta.servlet.http.HttpServletRequest request;

    @Mock
    private HttpSession session;

    @InjectMocks
    private HomeController homeController;

    @Test
    void homeUsesTopThreeJobsQuery() {
        List<JobPosting> jobs = List.of(new JobPosting(), new JobPosting(), new JobPosting());
        when(request.getSession(true)).thenReturn(session);
        when(productService.getAllProducts()).thenReturn(List.of());
        when(categoryService.getAllCategories()).thenReturn(List.of());
        when(jobPostingRepository.findTop3ByIsActiveTrueOrderByCreatedAtDesc()).thenReturn(jobs);

        ExtendedModelMap model = new ExtendedModelMap();
        String viewName = homeController.home(model, request);

        assertEquals("home", viewName);
        assertEquals(jobs, model.get("jobs"));
        verify(jobPostingRepository).findTop3ByIsActiveTrueOrderByCreatedAtDesc();
    }
}
