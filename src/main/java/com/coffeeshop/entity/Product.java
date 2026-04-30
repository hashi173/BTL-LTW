package com.coffeeshop.entity;

import jakarta.persistence.*;
import com.coffeeshop.util.EntityDisplayUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Product entity representing a menu item (beverage or food).
 * Prices are determined by {@link ProductSize}, not stored directly on Product.
 * The {@code tags} field provides comma-separated keywords for AI semantic search
 * (e.g., "milk,sữa,cream,sweet" enables matching when a user types "sữa").
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product extends BaseEntity {

    // --- Relationships ---




    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSize> sizes;

    // --- Basic Fields ---

    @Column(name = "product_code", length = 96)
    private String productCode;

    @Column(nullable = false, unique = true, length = 100)
    private String name;


    @Column(columnDefinition = "TEXT")
    private String description;


    /** Comma-separated search keywords for AI recommendation matching (EN + VI). */
    @Column(columnDefinition = "TEXT")
    private String tags;

    @Column(length = 500)
    private String image;

    @Column(precision = 10, scale = 2)
    private java.math.BigDecimal basePrice;

    @Column(name = "is_available")
    private boolean isAvailable = true;

    @Column(name = "avg_rating", precision = 3, scale = 2)
    private java.math.BigDecimal avgRating;

    @Column(name = "review_count")
    private Integer reviewCount = 0;

    @Transient
    public String getDisplayCode() {
        return org.springframework.util.StringUtils.hasText(productCode)
                ? productCode
                : EntityDisplayUtils.buildReadableCode("PRD", name, getId());
    }

    @Transient
    public String getResolvedImagePath() {
        return EntityDisplayUtils.resolveProductImagePath(image);
    }

    public static String resolveImagePath(String image) {
        return EntityDisplayUtils.resolveProductImagePath(image);
    }
}
