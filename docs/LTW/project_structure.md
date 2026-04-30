# Hashiji Cafe – Project Structure

```
Hashiji-Cafe-main/
├── docs/                          # All project documentation
│   ├── project_overview.md        # Feature list, tech stack, architecture
│   ├── project_structure.md       ← this file
│   ├── database.md                # Schema, relationships, triggers, stored procedures
│   ├── api_reference.md           # All endpoints with member assignments
│   ├── postman_guide.md           # Step-by-step Postman testing guide
│   └── defense_script.md          # Oral defense Q&A scenario script
│
├── src/main/
│   ├── java/com/coffeeshop/
│   │   ├── CoffeShopApplication.java     # Spring Boot entry point
│   │   │
│   │   ├── config/                       # Spring configuration classes
│   │   │   ├── SecurityConfig.java       # HTTP security rules, roles, login/logout
│   │   │   ├── CustomAuthenticationSuccessHandler.java  # Role-based redirect on login
│   │   │   ├── MvcConfig.java            # Static resource mappings
│   │   │   ├── WebConfig.java            # CORS, multipart config
│   │   │   ├── RedisConfig.java          # Redis connection and serialization
│   │   │   ├── GlobalControllerAdvice.java  # Global model attributes (e.g. cart count)
│   │   │   └── DataSeeder.java           # Runs on startup to seed initial data
│   │   │
│   │   ├── controller/                   # MVC controllers (one per feature area)
│   │   │   ├── HomeController.java       # / , /product/{id}, /products/fragment
│   │   │   ├── CartController.java       # /cart — view, add, update, remove
│   │   │   ├── CheckoutController.java   # /checkout — form and order placement
│   │   │   ├── TrackingController.java   # /tracking — search, cancel order
│   │   │   ├── InvoiceController.java    # /invoice/{id} — PDF download
│   │   │   ├── PageController.java       # /about, /careers/apply, /info, /contact
│   │   │   ├── AdminController.java      # /admin/dashboard
│   │   │   ├── ProductController.java    # /admin/products — full CRUD
│   │   │   ├── CategoryController.java   # /admin/categories — CRUD
│   │   │   ├── AdminToppingController.java  # /admin/toppings — CRUD
│   │   │   ├── IngredientController.java # /admin/ingredients — stock management
│   │   │   ├── OrderController.java      # /admin/orders — list, detail, status update

│   │   │   ├── ExpenseController.java    # /admin/expenses — CRUD
│   │   │   ├── AdminJobController.java   # /admin/recruitment — applications + job postings
│   │   │   └── AdminHistoryController.java  # /admin/history — monthly financial overview
│   │   │
│   │   ├── service/                      # Business logic layer
│   │   │   ├── CartService.java          # Session cart operations
│   │   │   ├── OrderService.java         # Order creation, status updates, inventory deduction
│   │   │   ├── ProductService.java       # Product queries, search, status updates
│   │   │   ├── CategoryService.java
│   │   │   ├── ToppingService.java
│   │   │   ├── UserService.java
│   │   │   ├── ExpenseService.java
│   │   │   ├── WorkShiftService.java     # Shift start/end, payroll calculation
│   │   │   └── CustomUserDetailsService.java  # Spring Security user loading
│   │   │
│   │   ├── entity/                       # JPA entities (mapped to DB tables)
│   │   │   ├── BaseEntity.java           # Common fields: id (UUID), createdAt, updatedAt
│   │   │   ├── User.java
│   │   │   ├── Role.java                 # Enum: ADMIN, STAFF
│   │   │   ├── Product.java
│   │   │   ├── ProductSize.java          # Size tiers per product (S/M/L + price)
│   │   │   ├── ProductRecipe.java        # Ingredient → product mapping for inventory
│   │   │   ├── ProductReview.java        # User reviews (schema-level, not UI)
│   │   │   ├── Category.java
│   │   │   ├── Topping.java
│   │   │   ├── Ingredient.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java            # Snapshot of product name/price per order
│   │   │   ├── OrderStatus.java          # Enum: PENDING, CONFIRMED, SHIPPING, COMPLETED, CANCELLED
│   │   │   ├── Expense.java
│   │   │   ├── WorkShift.java
│   │   │   ├── ShiftStatus.java          # Enum: OPEN, CLOSED
│   │   │   ├── JobPosting.java
│   │   │   ├── JobApplication.java
│   │   │   ├── JobType.java
│   │   │   ├── UserAddress.java
│   │   │   └── Promotion.java
│   │   │
│   │   ├── dto/                          # Non-entity data transfer objects
│   │   │   ├── Cart.java                 # Session cart wrapper
│   │   │   └── CartItem.java             # Single cart line item (in-memory)
│   │   │
│   │   └── repository/                   # Spring Data JPA repositories
│   │       ├── ProductRepository.java
│   │       ├── ProductSizeRepository.java
│   │       ├── ProductRecipeRepository.java
│   │       ├── CategoryRepository.java
│   │       ├── ToppingRepository.java
│   │       ├── IngredientRepository.java
│   │       ├── OrderRepository.java      # Custom JPQL queries for revenue, top products
│   │       ├── OrderItemRepository.java
│   │       ├── UserRepository.java
│   │       ├── ExpenseRepository.java
│   │       ├── WorkShiftRepository.java
│   │       ├── JobPostingRepository.java
│   │       └── JobApplicationRepository.java
│   │
│   └── resources/
│       ├── application.properties        # DB URL, JPA config, Redis, file upload settings
│       ├── schema-advanced.sql           # PostgreSQL stored procedures, triggers, transaction demos
│       ├── seed-data.sql                 # Initial data (categories, products, admin user)
│       ├── messages_en.properties        # English i18n strings
│       ├── messages_vi.properties        # Vietnamese i18n strings
│       ├── static/
│       │   ├── css/style.css             # Global stylesheet
│       │   ├── js/table-pagination.js    # Reusable pagination component
│       │   └── images/                   # Static bundled images
│       └── templates/                    # Thymeleaf HTML templates
│           ├── home.html                 # SPA-style home (menu, about, careers, info)
│           ├── layout.html               # Public layout (nav + footer)
│           ├── login.html
│           ├── product/detail.html       # Product detail with topping/size picker
│           ├── cart/index.html
│           ├── checkout/index.html + success.html
│           ├── tracking/index.html
│           ├── pages/                    # Standalone public pages
│           └── admin/                    # Admin area templates (dashboard, products, orders…)
│
├── Dockerfile
├── docker-compose.yml                    # PostgreSQL + Redis + App services
├── pom.xml
└── README.md
```

---

## Key Design Decisions

| Decision | Rationale |
|---|---|
| Session cart (`HttpSession`) | Simple, no DB table needed; clears automatically on session expiry |
| Snapshot fields in `order_items` | Makes order history immutable when products are edited/deleted |
| UUID primary keys | Globally unique, avoids sequential ID guessing |
| Thymeleaf fragments | `home :: productList` returned for AJAX category filtering, avoiding a full page reload |
| Dual-table layout (active vs. history) | Separates operational data from archive without separate DB queries |
