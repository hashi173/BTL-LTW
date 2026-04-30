# Hashiji Cafe – Project Overview

## 1. Introduction

**Hashiji Cafe** là ứng dụng web quản lý quán cà phê do nhóm 3 thành viên xây dựng trong khuôn khổ bài tập lớn môn Lập trình Web, IT Năm 3, PTIT.

Hệ thống bao phủ toàn bộ luồng đặt hàng phía khách hàng (menu → giỏ hàng → thanh toán → tra cứu) và bảng điều khiển admin toàn diện cho quản lý vận hành quán.

---

## 2. Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2.2 |
| Template Engine | Thymeleaf |
| Security | Spring Security 6 |
| ORM | Spring Data JPA / Hibernate |
| Database | PostgreSQL 15 (production), H2 (tests) |
| PDF Export | OpenPDF (LibrePDF) |
| Caching | Redis (category/product cache), Spring Cache |
| Build Tool | Maven |

---

## 3. Key Features

### Customer Side (Public)
- **Duyệt menu** — Lọc theo danh mục, tìm kiếm từ khóa (AJAX, không reload trang)
- **Chi tiết sản phẩm** — Chọn size, chọn topping, thiết lập đường/đá
- **Giỏ hàng** — Session-based, cập nhật số lượng, xóa từng món
- **Checkout** — Nhập địa chỉ, số điện thoại, ghi chú; sinh tracking code
- **Tra cứu đơn hàng** — Tra theo tracking code; xem tiến trình (progress bar); huỷ đơn PENDING
- **Tải hoá đơn PDF** — `/invoice/{orderId}` tạo file PDF trực tiếp

### Admin Panel (`/admin`)
- **Dashboard** — KPI: doanh thu, chi phí, lợi nhuận, số đơn; Chart.js trend + top sản phẩm bán chạy
- **Sản phẩm** — CRUD đầy đủ, upload ảnh hoặc nhập URL, quản lý giá theo size, bật/tắt bán
- **Danh mục** — Quản lý category (Coffee, Tea, Smoothie, Juice...)
- **Topping** — Quản lý tùy chọn topping và giá
- **Đơn hàng** — Xem, tìm kiếm, lọc theo trạng thái, chuyển trạng thái theo luồng, huỷ
- **Chi phí** — Ghi nhận chi phí vận hành (Utilities, Ingredients, Rent, Payroll)
- **Tuyển dụng** — Đăng tin tuyển dụng, quản lý đơn CV, theo dõi trạng thái ứng viên
- **Lịch sử tài chính** — Tổng hợp doanh thu/chi phí/lợi nhuận theo tháng; drill-down chi tiết

---

## 4. Luồng trạng thái đơn hàng

```
PENDING → CONFIRMED → SHIPPING → COMPLETED
         ↘           ↘          ↘
          CANCELLED  CANCELLED  CANCELLED
```

| Trạng thái | Nút Action (Trang danh sách) |
|------------|------------------------------|
| PENDING | ✅ Accept · ❌ Cancel |
| CONFIRMED | 🚚 Ship · ❌ Cancel |
| SHIPPING | 🏁 Complete · ❌ Cancel |
| COMPLETED | (chỉ xem Details) |
| CANCELLED | (chỉ xem Details) |

---

## 5. Entity Codes (ID Format)

| Entity | Format | Ví dụ |
|--------|--------|-------|
| Category | `CAT-00001` | CAT-00001, CAT-00002 |
| Product | `PRD-00001` | PRD-00001, PRD-00006 |
| Order | `ORD-000001` | ORD-000001, ORD-000150 |
| Job Application | `CV-XXXXXXXX` | CV-A8F2C1D3 |

---

## 6. Architecture

```
Browser
  └─ HTTP Request
       └─ Spring Security Filter Chain
            └─ Controller (Thymeleaf MVC)
                 ├─ Service Layer (business logic + @Cacheable)
                 │    └─ Repository (Spring Data JPA)
                 │         └─ PostgreSQL
                 └─ Static view (HTML + CSS + JS)
```

**Các design decision quan trọng:**

| Decision | Lý do |
|----------|-------|
| Session cart (`HttpSession`) | Đơn giản, không cần bảng DB riêng; tự xóa khi session hết hạn |
| Snapshot trong `order_items` | Lịch sử đơn hàng không đổi khi sản phẩm bị sửa/xóa sau này |
| UUID primary keys | Tránh sequential ID guessing; globally unique |
| Thymeleaf fragments | Trả `home :: productList` cho AJAX, không reload toàn trang |
| Redis cache | `@Cacheable("categories")` và `"products"` giảm tải DB; evict khi có thay đổi |
| `cleanupData()` trong DataSeeder | Đảm bảo DB luôn sạch khi seed lại ở môi trường dev |

---

## 7. Roles & Access

| Role | Access |
|------|--------|
| `ROLE_ADMIN` | Toàn bộ `/admin/**`, tất cả tính năng |
| Anonymous | Public pages: home, menu, cart, checkout, tracking, careers |

Login redirects: ADMIN → `/admin/dashboard`.

---

## 8. Running Locally

```bash
# 1. Chuẩn bị PostgreSQL (xem DEPLOYMENT.md)
# 2. Chạy app với dev profile
$env:APP_PROFILE="dev"
.\mvnw.cmd spring-boot:run
```

Default URL: `http://localhost:8080`

Tài khoản dev: `admin` / `123456`

Xem chi tiết: [DEPLOYMENT.md](../DEPLOYMENT.md) | [SEEDING.md](../SEEDING.md)
