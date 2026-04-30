# Hashiji Cafe – API Reference

> **Base URL:** `http://localhost:8080`
> **Auth methods:**
> - Session cookie (standard form login) — used by all browser-facing endpoints
> - Admin role required for all `/admin/**` routes
> - No auth required for public routes

---

## Member Assignment

| Member | Modules |
|---|---|
| **Phan** | Products, Categories, Toppings, Ingredients |
| **Hà** | Orders, Cart, Checkout, Tracking, Invoice |
| **Quỳnh** | Expenses, Recruitment |

---

---

## 👤 Phan — Products, Categories, Toppings, Ingredients

### Products (Public)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 1 | GET | `/` | Public | Home page — lists all products and categories |
| 2 | GET | `/products/fragment?categoryId=&keyword=` | Public | AJAX fragment — filter products by category or keyword |
| 3 | GET | `/product/{id}` | Public | Product detail page — shows sizes and toppings |

### Products (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 4 | GET | `/admin/products` | ADMIN | Product list (active/inactive, paginated, searchable) |
| 5 | GET | `/admin/products/new` | ADMIN | Show create product form |
| 6 | POST | `/admin/products/save` | ADMIN | Create or update a product (multipart form) |
| 7 | GET | `/admin/products/edit/{id}` | ADMIN | Show edit product form |
| 8 | GET | `/admin/products/activate/{id}` | ADMIN | Set product active = true |
| 9 | GET | `/admin/products/deactivate/{id}` | ADMIN | Set product active = false |
| 10 | GET | `/admin/products/delete/{id}` | ADMIN | Delete a product |

### Categories (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 11 | GET | `/admin/categories` | ADMIN | List all categories |
| 12 | GET | `/admin/categories/new` | ADMIN | Show create category form |
| 13 | POST | `/admin/categories/save` | ADMIN | Create or update category |
| 14 | GET | `/admin/categories/edit/{id}` | ADMIN | Show edit form |
| 15 | GET | `/admin/categories/delete/{id}` | ADMIN | Delete category |

### Toppings (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 16 | GET | `/admin/toppings` | ADMIN | List all toppings |
| 17 | POST | `/admin/toppings/save` | ADMIN | Create or update topping |
| 18 | GET | `/admin/toppings/delete/{id}` | ADMIN | Delete topping |

### Ingredients (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 19 | GET | `/admin/ingredients` | ADMIN | List ingredients with stock levels |
| 20 | POST | `/admin/ingredients/save` | ADMIN | Create or update ingredient |
| 21 | GET | `/admin/ingredients/delete/{id}` | ADMIN | Delete ingredient |

---

---

## 📦 Hà — Orders, Cart, Checkout, Tracking, Invoice

### Cart

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 22 | GET | `/cart` | Public | View current cart |
| 23 | POST | `/cart/add` | Public | Add item (productId, sizeId, quantity, toppingIds, sugar, ice, note) |
| 24 | POST | `/cart/update` | Public | Update item quantity by index |
| 25 | GET | `/cart/remove/{index}` | Public | Remove item by index |

### Checkout

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 26 | GET | `/checkout` | Public | Show checkout form (redirects to /cart if empty) |
| 27 | POST | `/checkout/place-order` | Public | Place order (customerName, phone, address, note) → success page with tracking code |

### Tracking

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 28 | GET | `/tracking` | Public | Show tracking page |
| 29 | GET | `/tracking/search?code=` | Public | Look up order or job application by tracking code |
| 30 | POST | `/tracking/cancel` | Public | Cancel a PENDING order (orderId + trackingCode required) |

### Invoice

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 31 | GET | `/invoice/{orderId}` | Public | Download PDF invoice for an order |

### Orders (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 32 | GET | `/admin/orders` | ADMIN | Order list (active vs. history, search, filter by status) |
| 33 | GET | `/admin/orders/{id}` | ADMIN | Order detail view |
| 34 | POST | `/admin/orders/{id}/status` | ADMIN | Update order status (`status` param) |
| 35 | POST | `/admin/orders/{id}/cancel` | ADMIN | Cancel an order |

---

---

## 🧾 Quỳnh — Expenses, Recruitment

### Expenses (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 41 | GET | `/admin/shifts` | ADMIN | Shift list (open vs. closed, paginated, searchable) |
| 42 | GET | `/admin/shifts/{id}` | ADMIN | Shift detail — orders and product summary for that shift |
| 43 | POST | `/admin/shifts/create` | ADMIN | Create a shift (userId, startTime, optional endTime) |
| 44 | POST | `/admin/shifts/{id}/delete` | ADMIN | Delete a shift record |

### Expenses (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 45 | GET | `/admin/expenses` | ADMIN | Expense list (paginated, searchable) |
| 46 | POST | `/admin/expenses/save` | ADMIN | Create or update an expense entry |
| 47 | GET | `/admin/expenses/delete/{id}` | ADMIN | Delete expense |

### Recruitment — Applications (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 48 | GET | `/admin/recruitment` | ADMIN | Application list (paginated, searchable) |
| 49 | POST | `/admin/recruitment/{id}/status` | ADMIN | Update application status (PENDING / REVIEWED / ACCEPTED / REJECTED) |
| 50 | POST | `/careers/apply` | Public | Submit job application (fullName, email, phone, position, cvFile) |

### Recruitment — Job Postings (Admin)

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 51 | GET | `/admin/recruitment/jobs` | ADMIN | Job posting list (active/closed, paginated) |
| 52 | POST | `/admin/recruitment/jobs/save` | ADMIN | Create or update a job posting |
| 53 | GET | `/admin/recruitment/jobs/delete/{id}` | ADMIN | Delete job posting |
| 54 | GET | `/admin/recruitment/jobs/toggle/{id}` | ADMIN | Toggle active/inactive status |

---

---

## 🔐 Auth & Misc

| # | Method | URL | Auth | Description |
|---|---|---|---|---|
| 55 | GET | `/login` | Public | Login page |
| 56 | POST | `/do-login` | Public | Process login (Spring Security) |
| 57 | GET | `/logout` | Authenticated | Logout |
| 58 | GET | `/admin/dashboard` | ADMIN | Dashboard with KPIs and charts |
| 59 | GET | `/admin/history` | ADMIN | Monthly financial overview |
| 60 | GET | `/admin/history/details?month=&year=` | ADMIN | Monthly drill-down (orders + expenses) |
