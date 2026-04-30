# Hashiji Cafe – Project Overview

## 1. Introduction

**Hashiji Cafe** is a full-featured Coffee Shop Management web application built by Group 5, IT Year-3 Semester 2, PTIT.
The system covers the full customer ordering flow (menu → cart → checkout → tracking) and a comprehensive admin panel for day-to-day business management.

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.2 |
| Template Engine | Thymeleaf |
| Security | Spring Security 6 |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL (production), H2 (tests) |
| PDF Export | OpenPDF (LibrePDF) |
| Caching | Redis (session-level caching) |
| Build Tool | Maven |
| Containerization | Docker / Docker Compose |

---

## 3. Key Features

### Customer Side
- **Menu browsing** — Filter by category, keyword search (AJAX, no page reload)
- **Product detail** — Size selection, topping customization, sugar/ice level
- **Shopping cart** — Session-based cart with quantity update and removal
- **Checkout** — Address, phone, optional note; generates tracking code
- **Order tracking** — Look up order or job application by tracking code; cancel PENDING orders
- **Invoice download** — PDF invoice per order (`/invoice/{orderId}`)

### Admin Panel (`/admin`)
- **Dashboard** — Revenue KPIs, net profit, Chart.js monthly trend + top-selling products
- **Products** — CRUD with image upload or URL, size tiers, ingredient recipes, active/inactive toggle
- **Categories** — Manage drink categories
- **Toppings** — Manage topping options and prices
- **Ingredients** — Track stock quantity for inventory deduction on order completion
- **Orders** — View, filter, change status, cancel; paginated active vs. history tables
- **Users** — Create/deactivate staff accounts, role assignment
- **Work Shifts** — Admin creates/closes shifts; track orders per shift, calculate payroll
- **Expenses** — Log operational expenses for profit calculation
- **Recruitment** — Post job openings; manage CV applications; track application status
- **History** — Monthly financial summary with Chart.js overlay; drill-down to order list

---

## 4. Architecture

```
Browser
  └─ HTTP Request
       └─ Spring Security Filter Chain
            └─ Controller (Thymeleaf MVC)
                 ├─ Service Layer (business logic)
                 │    └─ Repository (Spring Data JPA)
                 │         └─ PostgreSQL
                 └─ Static view (HTML + CSS + JS)
```

- **Session-based cart** — Cart is stored in `HttpSession`, not the database.
- **Snapshot approach for orders** — `order_items` stores product name and price at time of purchase, so historical orders are unaffected by product edits.
- **Inventory deduction** — Triggered when an order status changes to `COMPLETED`.

---

## 5. Roles & Access

| Role | Access |
|---|---|
| `ROLE_ADMIN` | Full `/admin/**` access, all features |
| `ROLE_STAFF` | Standard authenticated pages |
| Anonymous | Public pages: home, menu, cart, checkout, tracking, careers |

Login redirects: ADMIN → `/admin/dashboard`, others → `/`.

---

## 6. Running Locally

```bash
# 1. Start PostgreSQL and Redis via Docker
docker-compose up -d

# 2. Run the application
./mvnw spring-boot:run
```

Default URL: `http://localhost:8080`

See [DEPLOYMENT.md](DEPLOYMENT.md) for production deployment on Render.
