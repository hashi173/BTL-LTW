package com.coffeeshop.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import com.coffeeshop.util.EntityDisplayUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a Product Category (e.g., Coffee, Tea, Smoothie).
 */
@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Category extends BaseEntity {

    @jakarta.persistence.Column(name = "category_code", length = 64)
    private String categoryCode;

    @jakarta.persistence.Column(unique = true, nullable = false, length = 50)
    private String name;

    @jakarta.persistence.Column(length = 255)
    private String description;

    @Transient
    public String getDisplayCode() {
        return org.springframework.util.StringUtils.hasText(categoryCode)
                ? categoryCode
                : EntityDisplayUtils.buildReadableCode("CAT", name, getId());
    }
}
