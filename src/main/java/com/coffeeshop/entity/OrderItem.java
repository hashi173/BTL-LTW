package com.coffeeshop.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "snapshot_product_name", length = 255)
    private String snapshotProductName;

    @Column(name = "snapshot_unit_price", precision = 12, scale = 2)
    private BigDecimal snapshotUnitPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "snapshot_options", columnDefinition = "json")
    @JdbcTypeCode(SqlTypes.JSON)
    private String snapshotOptions;

    @Column(name = "sub_total", precision = 12, scale = 2)
    private BigDecimal subTotal;

    @Transient
    public String getProductName() {
        return snapshotProductName;
    }

    @Transient
    public String getSizeSelected() {
        return getDisplayOptions();
    }

    @Transient
    public String getDisplayOptions() {
        if (snapshotOptions == null || snapshotOptions.isBlank()) {
            return "";
        }

        try {
            JsonNode root = OBJECT_MAPPER.readTree(snapshotOptions);
            JsonNode summary = root.get("summary");
            if (summary != null && !summary.isNull()) {
                return summary.asText();
            }
        } catch (Exception ignored) {
            // Backward compatibility for legacy plain-text rows.
        }

        return snapshotOptions;
    }

    @Transient
    public BigDecimal getUnitPrice() {
        return snapshotUnitPrice;
    }
}
