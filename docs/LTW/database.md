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
| description | TEXT | |

---

### `products`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| category_id | UUID FK → categories | |
| name | VARCHAR(200) | |
| description | TEXT | |
| image | VARCHAR(500) | URL or relative path |
| is_active | BOOLEAN | Soft-delete / hide from menu |

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

### `job_postings`
| Column | Type | Notes |
|---|---|---|
| id | UUID PK | |
| title | VARCHAR(100) | |
| location | VARCHAR(100) | e.g. "Hanoi", "Ho Chi Minh" |
| type | VARCHAR(50) | Enum: FULL_TIME, PART_TIME, INTERNSHIP |
| description | TEXT | |
| requirements | TEXT | |
| job_code | VARCHAR(20) | Unique, format: JOB-000001 |
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

## 3. Relationships (ERD Summary)
```
users ──< orders

categories ──< products
products ──< product_sizes

orders ──< order_items >── products (nullable)
```
