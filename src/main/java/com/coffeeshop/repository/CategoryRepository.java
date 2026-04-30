package com.coffeeshop.repository;

import com.coffeeshop.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, java.util.UUID> {
    java.util.Optional<Category> findByName(String name);

    java.util.Optional<Category> findByCategoryCodeIgnoreCase(String categoryCode);

    java.util.List<Category> findByNameContainingIgnoreCase(String name);

    org.springframework.data.domain.Page<Category> findAll(org.springframework.data.domain.Pageable pageable);

    @Query("SELECT c FROM Category c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(COALESCE(c.categoryCode, '')) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    org.springframework.data.domain.Page<Category> searchCategories(@Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}
