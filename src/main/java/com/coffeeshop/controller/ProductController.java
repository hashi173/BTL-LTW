package com.coffeeshop.controller;

import com.coffeeshop.entity.Product;
import com.coffeeshop.entity.ProductSize;
import com.coffeeshop.service.CategoryService;
import com.coffeeshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "activePage", defaultValue = "0") int activePage,
            @RequestParam(value = "inactivePage", defaultValue = "0") int inactivePage,
            Model model) {

        int pageSize = 10;

        if (search != null && !search.isEmpty()) {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest
                    .of(activePage, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));
            org.springframework.data.domain.Page<Product> productPage = productService.searchProductsPaginated(search,
                    pageable);
            model.addAttribute("searchResults", productPage.getContent());
            model.addAttribute("productPage", productPage);
            model.addAttribute("currentPage", activePage);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("isSearching", true);
        } else {
            org.springframework.data.domain.Pageable activeRequest = org.springframework.data.domain.PageRequest
                    .of(activePage, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));
            org.springframework.data.domain.Pageable inactiveRequest = org.springframework.data.domain.PageRequest
                    .of(inactivePage, pageSize, org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "id"));

            org.springframework.data.domain.Page<Product> activeProductPage = productService
                    .getProductsByStatusPaginated(true, activeRequest);
            org.springframework.data.domain.Page<Product> inactiveProductPage = productService
                    .getProductsByStatusPaginated(false, inactiveRequest);

            model.addAttribute("activeProducts", activeProductPage.getContent());
            model.addAttribute("activePage", activePage);
            model.addAttribute("totalActivePages", activeProductPage.getTotalPages());
            model.addAttribute("totalActiveCount", activeProductPage.getTotalElements());

            model.addAttribute("inactiveProducts", inactiveProductPage.getContent());
            model.addAttribute("inactivePage", inactivePage);
            model.addAttribute("totalInactivePages", inactiveProductPage.getTotalPages());
            model.addAttribute("totalInactiveCount", inactiveProductPage.getTotalElements());

            model.addAttribute("isSearching", false);
        }

        model.addAttribute("search", search);
        return "admin/products/index";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Product product = new Product();
        product.setSizes(new ArrayList<>(List.of(new ProductSize())));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/products/form";
    }

    @PostMapping("/save")
    public String saveProduct(@ModelAttribute("product") Product product,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            RedirectAttributes redirectAttributes) {

        if (product.getSizes() != null) {
            product.getSizes().removeIf(size -> size.getSizeName() == null || size.getPrice() == null);
        }

        String existingImage = null;
        if (product.getId() != null) {
            existingImage = productService.getProductById(product.getId())
                    .map(Product::getImage)
                    .orElse(null);
        }

        if (!imageFile.isEmpty()) {
            try {
                Path uploadPath = Paths.get("uploads/products/");
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String originalFileName = StringUtils.cleanPath(
                        java.util.Optional.ofNullable(imageFile.getOriginalFilename()).orElse("product-image"));
                String safeFileName = Paths.get(originalFileName).getFileName().toString();
                String fileName = java.util.UUID.randomUUID() + "_" + safeFileName;
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Files.copy(inputStream, uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                    product.setImage("/uploads/products/" + fileName);
                }
            } catch (IOException exception) {
                redirectAttributes.addFlashAttribute("error",
                        "Could not save uploaded image: " + exception.getMessage());
                if (existingImage != null) {
                    product.setImage(existingImage);
                }
            }
        } else if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            String url = imageUrl.trim();
            if (!url.startsWith("/") && !url.toLowerCase().startsWith("http://")
                    && !url.toLowerCase().startsWith("https://")) {
                url = "https://" + url;
            }
            product.setImage(url);
        } else if (existingImage != null) {
            product.setImage(existingImage);
        }

        productService.saveProduct(product);
        redirectAttributes.addFlashAttribute("message", "Product saved successfully!");
        return "redirect:/admin/products";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable java.util.UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
            model.addAttribute("product", product);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "admin/products/form";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Product not found.");
            return "redirect:/admin/products";
        }
    }

    @GetMapping("/activate/{id}")
    public String activateProduct(@PathVariable java.util.UUID id, RedirectAttributes redirectAttributes) {
        productService.updateStatus(id, true);
        redirectAttributes.addFlashAttribute("message", "Product reactivated successfully!");
        return "redirect:/admin/products";
    }

    @GetMapping("/deactivate/{id}")
    public String deactivateProduct(@PathVariable java.util.UUID id, RedirectAttributes redirectAttributes) {
        productService.updateStatus(id, false);
        redirectAttributes.addFlashAttribute("message", "Product deactivated successfully!");
        return "redirect:/admin/products";
    }

    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable java.util.UUID id, RedirectAttributes redirectAttributes) {
        productService.deleteProduct(id);
        redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        return "redirect:/admin/products";
    }
}
