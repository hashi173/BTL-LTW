# Hashiji Cafe -- Coffee Shop Management System

A full-stack coffee shop management platform built with **Java 17 / Spring Boot 3**. Designed for real-world operations: online ordering, employee shift tracking, inventory management, AI-powered product recommendations, and a professional customer-facing storefront.

**Live Demo**: [Deploy your own instance -- see Deployment Guide below]

---

## Key Features

### Advanced DBMS Integration (Course Demo)
- **UUID Primary Keys**: Secure and globally unique identifiers used across all tables instead of predictable sequential IDs.
- **Stored Procedures**: Complex atomic operations (e.g., `place_order` for inventory deduction and cart processing, `get_revenue_report` for fast data aggregation) handled directly by PostgreSQL to minimize network roundtrips.
- **Database Triggers**: Automated data consistency rules (e.g., auto-updating product ratings upon new reviews, logging cart behaviors, enforcing single default addresses).
- **ACID Transactions**: Demonstrable rollback capabilities when placing an order with insufficient inventory.

### Customer Storefront (Public)
- Responsive SPA-style homepage with product carousel, careers portal, and contact form
- Full shopping cart with size and topping customization
- Order checkout with real-time tracking via unique tracking codes
- Clean and modern interface with localized content
### AI Recommendation Engine
- **5 strategies**: Cold Start (best sellers), Content-Based Filtering (TF cosine similarity), Collaborative Filtering (user-based KNN with Jaccard), Hybrid blending, and Semantic Search
- Semantic search leverages product **tags** for accurate matching -- e.g., typing "milk" correctly surfaces milk-based drinks only
- No external API dependencies; all algorithms run in-process

### Admin Dashboard
- Revenue analytics with Chart.js (monthly trends, daily breakdown)
- Top-selling product rankings with images
- Order management with status workflow (Pending -> Confirmed -> Shipping -> Completed)
- One-click order cancellation and direct customer contact (click-to-call)
- Monthly financial history with revenue, expenses, and net profit

### Human Resources
- Job posting management with public careers portal
- Application tracking with file upload (CV/resume)
- Applicant status pipeline: New -> Reviewed -> Interviewing -> Hired/Rejected
- Tracking codes for applicants to check status

### Inventory and Expenses
- Ingredient stock management with per-unit cost tracking
- Product recipe system linking ingredients to menu items
- Expense categorization and monthly reporting
- Automated inventory deduction based on recipes when orders complete

### Security
- Spring Security with role-based access (ADMIN, STAFF)
- BCrypt password hashing
- CSRF protection enabled by default
- Sensitive configs externalized via environment variables

---

## Tech Stack

| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Backend     | Java 17, Spring Boot 3.2, Spring Security, JPA |
| Database    | PostgreSQL (Supabase cloud, Docker, or local)   |
| Frontend    | Thymeleaf, Bootstrap 5, HTMX, Chart.js          |
| AI/ML       | Custom TF-IDF, Cosine Similarity, KNN (no external APIs) |
| Caching     | Spring Cache (Simple / Redis)                   |
| Build       | Maven with wrapper (no global install needed)   |

---

## Quick Start (DBMS Demo Mode)

### Prerequisites
- Docker & Docker Compose (for the local PostgreSQL instance)
- Java 17+

### 1. Run Everything via Docker Compose
The project includes a `docker-compose.yml` to spin up PostgreSQL 15, pgAdmin 4, and the Spring Boot application (Backend + Frontend) all at once.
```bash
docker compose up -d --build
```

### 2. Initialize Database & Run DBMS Demo
We have prepared a complete SQL script featuring Triggers, Stored Procedures, and Transactions for the DBMS course report.
1. In the default `dev` profile, the Spring Boot app seeds demo users, products, orders, expenses, and chart history through `DataSeeder.java`.
2. The web application will be available at `http://localhost:8080`.
3. Access the database via pgAdmin (`http://localhost:5050`) or `psql`.
4. If you want the PostgreSQL course/demo scripts, load `schema-advanced.sql` manually after the app has created its tables. Load `seed-data.sql` only for the DBMS demo flow, not together with the `dev` profile seed.
5. Follow the detailed steps in the [db_demo_script.md](db_demo_script.md) file to showcase Triggers, Stored Procedures, and Transactions to your instructor.
6. Seeding behavior is summarized in [docs/SEEDING.md](docs/SEEDING.md).

---

## Deployment (Render -- Free Tier)

1. Push code to GitHub (credentials are externalized, safe to push)
2. Create a [Render](https://render.com) Web Service connected to the repo
3. Set environment variables in Render dashboard:
   - `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (from Supabase)
   - `APP_PROFILE=prod`
4. Build command: `./mvnw clean install -DskipTests`
5. Start command: `java -jar target/*.jar`

---

## Project Structure

```
src/main/java/com/coffeeshop/
  config/       -- Security, data seeding, MVC, Redis configuration
  controller/   -- REST and MVC controllers (19 controllers)
  dto/          -- Data transfer objects for cart, etc.
  entity/       -- JPA entities (UUID based)
  repository/   -- Spring Data JPA repositories
  service/      -- Business logic and AI recommendation engine

src/main/resources/
  schema-advanced.sql -- Advanced PostgreSQL Features (Triggers/Procedures)
  seed-data.sql       -- Manual SQL demo data for DBMS presentation only
  application-dev.properties -- Enables Java-based dev/demo seeding
  templates/    -- Thymeleaf templates (admin, cart, checkout, tracking, etc.)
  static/       -- CSS, JS, product images
  messages.properties  -- Application message bundles
```

---

## License

This project is for educational and portfolio purposes.
