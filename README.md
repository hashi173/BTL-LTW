# Hashiji Cafe -- Coffee Shop Management System

A full-stack coffee shop management platform built with **Java 17 / Spring Boot 3**. Designed for real-world operations: online ordering, recruitment management, financial tracking, and a professional customer-facing storefront.

**Live Demo**: [Deploy your own instance -- see Deployment Guide below]

---

## Key Features

### Advanced DBMS Integration (Course Demo)
- **UUID Primary Keys**: Secure and globally unique identifiers used across all tables instead of predictable sequential IDs.
- **Stored Procedures**: Complex atomic operations (e.g., `place_order` for inventory deduction and cart processing, `get_revenue_report` for fast data aggregation) handled directly by PostgreSQL to minimize network roundtrips.
- **Database Triggers**: Automated data consistency rules (e.g., auto-updating product ratings upon new reviews, logging cart behaviors, enforcing single default addresses).
- **ACID Transactions**: Demonstrable rollback capabilities when placing an order with insufficient inventory.

### Customer Storefront (Public)
- **Responsive Homepage**: Modern design with product carousels, category filtering, and smooth transitions.
- **Full Shopping Cart**: Support for product customization including sizes, toppings, and special notes.
- **Recruitment Portal**: Public job board with paginated job listings and CV upload functionality.
- **Order Tracking**: Real-time order status lookup using unique tracking codes.
- **Clean UI**: Built with Bootstrap 5 and customized CSS for a premium, localized experience.

### Admin Dashboard
- **Revenue Analytics**: Visual reporting with Chart.js showing monthly trends and daily breakdowns.
- **Product Performance**: Top-selling product rankings with visual metrics.
- **Order Management**: Comprehensive status workflow (Pending -> Confirmed -> Shipping -> Completed).
- **One-click Actions**: Quick status updates, order cancellations, and direct customer contact shortcuts.

### Recruitment & Careers
- **Job Posting Management**: Create and manage job openings with specific types and requirements.
- **Application Pipeline**: Track applicants from "New" through "Interviewing" to "Hired/Rejected".
- **CV Management**: Centralized storage and viewing of applicant resumes.
- **Status Tracking**: Applicants can check their progress using unique tracking codes.

### Financial Tracking
- **Profit Reporting**: Automatic calculation of net profit by comparing revenue against costs.
- **Financial History**: Monthly archive of financial performance for historical analysis.

### Security
- **Spring Security**: Robust role-based access control (ADMIN, STAFF).
- **Data Protection**: BCrypt password hashing and CSRF protection.
- **Environment Safety**: Sensitive configurations externalized via environment variables.

---

## Tech Stack

| Layer       | Technology                                      |
|-------------|------------------------------------------------|
| Backend     | Java 17, Spring Boot 3.2, Spring Security, JPA |
| Database    | PostgreSQL (Supabase cloud, Docker, or local)   |
| Frontend    | Thymeleaf, Bootstrap 5, HTMX, Chart.js          |
| Caching     | Spring Cache (Simple / Redis)                   |
| Build       | Maven with wrapper (no global install needed)   |

---

## Quick Start (DBMS Demo Mode)

### Prerequisites
- Docker & Docker Compose (for the local PostgreSQL instance)
- Java 17+

### 1. Run Everything via Docker Compose
The project includes a `docker-compose.yml` to spin up PostgreSQL 15, pgAdmin 4, and the Spring Boot application (Backend + Frontend) all at once. See the full [Docker Guide](docs/DOCKER_GUIDE.md) for details.
```bash
docker compose up -d --build
```

### 2. Initialize Database
1. In the default `dev` profile, the Spring Boot app automatically seeds demo users, products, orders, and chart history through `DataSeeder.java`.
2. The web application will be available at `http://localhost:8080`.
3. Access the database via pgAdmin (`http://localhost:5050`) or `psql` using the credentials in `application.properties`.

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
  config/       -- Security, data seeding, MVC, Redis, metadata backfill
  controller/   -- MVC controllers (14 controllers)
  dto/          -- Data transfer objects for cart and requests
  entity/       -- JPA entities (UUID based)
  repository/   -- Spring Data JPA repositories
  service/      -- Core business logic services

src/main/resources/

  seed-data.sql       -- Manual SQL demo data for DBMS presentation only
  application.properties -- Primary configuration
  templates/    -- Thymeleaf templates (admin, storefront, dashboard, etc.)
  static/       -- CSS, JS, and asset files
  messages.properties  -- Localization bundles
```

---

## Documentation

Detailed documentation is available in the `/docs` directory:
- [API Reference](docs/LTW/api_reference.md)
- [Postman Guide](docs/LTW/postman_guide.md)
- [Deployment Guide](docs/DEPLOYMENT.md)
- [Docker Guide](docs/DOCKER_GUIDE.md)
- [Database Schema](docs/LTW/database.md)
- [Project Structure](docs/LTW/project_structure.md)
- [Defense Script](docs/LTW/defense_script.md)

---

## License

This project is for educational and portfolio purposes.
