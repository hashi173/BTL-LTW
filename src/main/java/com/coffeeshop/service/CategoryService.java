package com.coffeeshop.service;

import com.coffeeshop.entity.Category;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.util.EntityDisplayUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @org.springframework.cache.annotation.Cacheable("categories")
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public java.util.Optional<Category> getCategoryById(@org.springframework.lang.NonNull java.util.UUID id) {
        return categoryRepository.findById(id);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public Category saveCategory(@org.springframework.lang.NonNull Category category) {
        if (!StringUtils.hasText(category.getCategoryCode())) {
            category.setCategoryCode(EntityDisplayUtils.buildReadableCode("CAT", category.getName(), category.getId()));
        }
        return categoryRepository.save(category);
    }

    @org.springframework.cache.annotation.CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(@org.springframework.lang.NonNull java.util.UUID id) {
        categoryRepository.deleteById(id);
    }

    public List<Category> searchCategories(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    public org.springframework.data.domain.Page<Category> getAllCategoriesPaginated(
            org.springframework.data.domain.Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public org.springframework.data.domain.Page<Category> searchCategoriesPaginated(String keyword,
            org.springframework.data.domain.Pageable pageable) {
        return categoryRepository.searchCategories(keyword, pageable);
    }

}
