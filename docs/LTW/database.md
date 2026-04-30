# Hashiji Cafe – Database Documentation

## 1. Overview

- **DBMS:** PostgreSQL
- **Schema strategy:** JPA `ddl-auto=update` (Hibernate auto-creates/alters tables from entities)
- **Primary key type:** UUID (`uuid-ossp` extension)
- **Audit fields:** All entities extend `BaseEntity` → `id`, `created_at`, `updated_at`

---

## 2. Tables

### `users`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | Auto-generated |
| username | VARCHAR(50) | Unique, login credential |
| password | VARCHAR(255) | BCrypt-hashed |
| full_name | VARCHAR(100) | |
| email | VARCHAR(100) | Nullable, unique |
| phone | VARCHAR(20) | Nullable, unique |
| user_code | VARCHAR(20) | Nullable, unique, auto-generated |
| role | VARCHAR(20) | Enum: ADMIN, STAFF |
| hourly_rate | DOUBLE | Used for payroll estimation |
| is_active | BOOLEAN | Soft delete support |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

---

### `categories`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| name | VARCHAR(100) | e.g. "Coffee", "Tea" |
| name_vi | VARCHAR(100) | Vietnamese name |
| description | TEXT | |

---

### `products`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| category_id | UUID FK → categories | |
| name | VARCHAR(200) | |
| name_vi | VARCHAR(200) | |
| description | TEXT | |
| description_vi | TEXT | |
| image | VARCHAR(500) | URL or relative path |
| tags | TEXT | Comma-separated search tags |
| is_active | BOOLEAN | Soft-delete / hide from menu |
| avg_rating | NUMERIC(3,2) | Updated by trigger `trg_update_product_rating` |
| review_count | INT | Updated by trigger |

---

### `product_sizes`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| product_id | UUID FK → products | |
| size_name | VARCHAR(10) | e.g. "S", "M", "L" |
| price | DOUBLE | Price for this size |

---

### `toppings`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| name | VARCHAR(100) | |
| price | DOUBLE | Added to base size price |

---

### `ingredients`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| name | VARCHAR(100) | |
| unit | VARCHAR(20) | e.g. "g", "ml" |
| stock_quantity | DOUBLE | Current stock level |
| low_stock_threshold | DOUBLE | Alert threshold |

---

### `product_recipes`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| product_id | UUID FK → products | |
| ingredient_id | UUID FK → ingredients | |
| quantity_required | DOUBLE | Amount used per 1 unit of product |

---

### `orders`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| user_id | UUID FK → users | Nullable (anonymous checkout) |
| customer_name | VARCHAR(200) | Snapshot at order time |
| phone | VARCHAR(20) | |
| address | TEXT | Delivery address |
| note | TEXT | |
| order_type | VARCHAR(50) | e.g. "Online", "In-Store Order" |
| total_amount | DOUBLE | Sum of items + toppings |
| grand_total | NUMERIC(12,2) | Same as total_amount (legacy) |
| status | VARCHAR(20) | Enum: PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELLED |
| order_status | VARCHAR(20) | String mirror of status (legacy) |
| tracking_code | VARCHAR(20) | Unique, format: ORD-XXXXXX |
| created_at | TIMESTAMP | |
| updated_at | TIMESTAMP | |

---

### `order_items`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| order_id | UUID FK → orders | |
| product_id | UUID FK → products | Nullable (product may be deleted) |
| snapshot_product_name | VARCHAR(200) | Name at purchase time |
| snapshot_unit_price | NUMERIC(10,2) | Price at purchase time |
| quantity | INT | |
| sub_total | NUMERIC(12,2) | unit_price × quantity |
| snapshot_options | TEXT | Size, toppings string at purchase time |

---

### `expenses`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| description | VARCHAR(500) | |
| amount | DOUBLE | |
| expense_date | DATE | |
| category | VARCHAR(100) | e.g. "Supplies", "Utilities" |

---

### `work_shifts`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| user_id | UUID FK → users | Staff member for this shift |
| start_time | TIMESTAMP | |
| end_time | TIMESTAMP | Null if still open |
| start_cash | DOUBLE | Cash in drawer at start |
| end_cash | DOUBLE | Cash counted at close |
| total_revenue | DOUBLE | Calculated from orders in this window |
| cash_variance | DOUBLE | end_cash - (start_cash + revenue) |
| status | VARCHAR(10) | Enum: OPEN, CLOSED |

---

### `job_postings`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| title | VARCHAR(200) | |
| description | TEXT | |
| requirements | TEXT | |
| salary_range | VARCHAR(100) | |
| is_active | BOOLEAN | Controls visibility on careers page |
| created_at | TIMESTAMP | |

---

### `job_applications`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| full_name | VARCHAR(200) | |
| email | VARCHAR(100) | |
| phone | VARCHAR(20) | |
| position | VARCHAR(200) | Free-text position applied for |
| cv_url | VARCHAR(500) | Uploaded CV file path |
| tracking_code | VARCHAR(20) | Unique, format: CV-XXXXXXXX |
| status | VARCHAR(20) | Enum: PENDING, REVIEWED, ACCEPTED, REJECTED |
| created_at | TIMESTAMP | |

---

### `user_addresses`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| user_id | UUID FK → users | |
| full_address | TEXT | |
| is_default | BOOLEAN | Enforced by trigger (only one default per user) |

---

### `promotions`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| code | VARCHAR(50) | Promo code |
| discount_type | VARCHAR(20) | PERCENTAGE or FIXED |
| discount_value | NUMERIC(10,2) | |
| min_order_value | NUMERIC(10,2) | Minimum cart total to apply |
| start_date | TIMESTAMP | |
| end_date | TIMESTAMP | |

---

### `product_reviews`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| product_id | UUID FK → products | |
| user_id | UUID FK → users | |
| rating_score | INT | 1–5 |
| comment | TEXT | |
| created_at | TIMESTAMP | |

---

### `shopping_sessions` + `cart_items` (DB-level, AI trigger targets)
These tables exist in the schema for the stored procedure and trigger demonstrations.
The application uses a **session-based cart (`HttpSession`)** and does not write to these tables during normal operation.

---

### `user_behavior_logs`
Tracks `ADD_TO_CART` events via a DB trigger. Not used by the application UI.
Kept in schema for the academic DB demo (trigger showcase).

---

## 3. Relationships (ERD Summary)

```
users ──< work_shifts
users ──< orders
users ──< job_applications
users ──< user_addresses

categories ──< products
products ──< product_sizes
products ──< product_recipes >── ingredients
products ──< product_reviews

orders ──< order_items >── products (nullable)
```

---

## 4. Stored Procedures (`schema-advanced.sql`)

### `place_order(user_id, address_id, promotion_id, items JSONB)`
- Validates delivery address ownership
- Loops through items JSONB, checks product availability
- Applies promotion discount if valid and min order met
- Inserts into `orders` + `order_items` atomically

### `get_revenue_report(from_date, to_date)`
- Returns per-day aggregation: `report_date`, `total_orders`, `total_revenue`, `avg_order_val`
- Filtered to `order_status = 'COMPLETED'`

---

## 5. Triggers (`schema-advanced.sql`)

### `trg_update_product_rating` (AFTER INSERT/UPDATE/DELETE on `product_reviews`)
Recalculates `avg_rating` and `review_count` on the parent `products` row.

### `trg_log_cart_behavior` (AFTER INSERT on `cart_items`)
Looks up the session owner and writes an `ADD_TO_CART` log to `user_behavior_logs` with weight `0.5`.

### `trg_single_default_address` (BEFORE INSERT/UPDATE on `user_addresses`)
Ensures only one `is_default = TRUE` address exists per user by unsetting all others.

---

## 6. Transaction Demo (in `schema-advanced.sql`)

A commented-out transaction block demonstrates the atomic checkout pattern:
1. Call `place_order(...)` stored procedure
2. Delete cart items from `cart_items`
3. Reset `shopping_sessions.total_amount` to 0
4. `COMMIT` (or `ROLLBACK` on any error)
