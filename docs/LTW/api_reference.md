# Hashiji Cafe – API Reference

> **Base URL:** `http://localhost:8080`
> **Auth methods:**
> - Session cookie (standard form login) — used by all browser-facing endpoints
> - `ROLE_ADMIN` required for all `/admin/**` routes
> - No auth required for public routes

---

## Member Assignment

| Member | Modules | Số API |
|--------|---------|--------|
| **Phan** | Products, Categories | 15 |
| **Hà** | Orders, Cart, Checkout, Tracking, Invoice, History | 16 |
| **Quỳnh** | Recruitment (Applications + Jobs), Auth, Toppings, Dashboard | 15 |

> **Lưu ý:** Ingredients và Work Shifts đã được loại bỏ khỏi scope ứng dụng (không có controller/UI tương ứng).

---

## 👤 Phan — Products, Categories

### Products (Public)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 1 | GET | `/` | Public | Home page — hiển thị menu sản phẩm và danh mục |
| 2 | GET | `/products/fragment?categoryId=&keyword=` | Public | AJAX fragment — lọc sản phẩm theo danh mục hoặc từ khóa |
| 3 | GET | `/product/{id}` | Public | Trang chi tiết sản phẩm — kích thước, topping |

### Products (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 4 | GET | `/admin/products` | ADMIN | Danh sách sản phẩm (active/inactive, phân trang, có tìm kiếm) |
| 5 | GET | `/admin/products/new` | ADMIN | Hiển thị form tạo sản phẩm mới |
| 6 | POST | `/admin/products/save` | ADMIN | Tạo hoặc cập nhật sản phẩm (multipart form, upload ảnh) |
| 7 | GET | `/admin/products/edit/{id}` | ADMIN | Hiển thị form chỉnh sửa sản phẩm |
| 8 | GET | `/admin/products/activate/{id}` | ADMIN | Kích hoạt sản phẩm (`active = true`) |
| 9 | GET | `/admin/products/deactivate/{id}` | ADMIN | Vô hiệu hóa sản phẩm (`active = false`) |
| 10 | GET | `/admin/products/delete/{id}` | ADMIN | Xóa sản phẩm (soft delete) |

### Categories (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 11 | GET | `/admin/categories` | ADMIN | Danh sách danh mục (phân trang, tìm kiếm, sort theo ID) |
| 12 | GET | `/admin/categories/new` | ADMIN | Hiển thị form tạo danh mục |
| 13 | POST | `/admin/categories/save` | ADMIN | Tạo hoặc cập nhật danh mục |
| 14 | GET | `/admin/categories/edit/{id}` | ADMIN | Hiển thị form chỉnh sửa |
| 15 | GET | `/admin/categories/delete/{id}` | ADMIN | Xóa danh mục (chặn nếu còn sản phẩm) |

---

## 📦 Hà — Orders, Cart, Checkout, Tracking, Invoice, History

### Cart (Public)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 16 | GET | `/cart` | Public | Xem giỏ hàng hiện tại |
| 17 | POST | `/cart/add` | Public | Thêm sản phẩm (productId, sizeId, quantity, toppingIds, sugar, ice, note) |
| 18 | POST | `/cart/update` | Public | Cập nhật số lượng theo index |
| 19 | GET | `/cart/remove/{index}` | Public | Xóa sản phẩm theo index |

### Checkout (Public)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 20 | GET | `/checkout` | Public | Hiển thị form thanh toán (redirect về /cart nếu giỏ trống) |
| 21 | POST | `/checkout/place-order` | Public | Đặt hàng (customerName, phone, address, note) → trang thành công kèm tracking code |

### Tracking (Public)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 22 | GET | `/tracking` | Public | Trang tra cứu đơn hàng |
| 23 | GET | `/tracking/search?code=` | Public | Tra cứu đơn hàng hoặc đơn tuyển dụng theo tracking code |
| 24 | POST | `/tracking/cancel` | Public | Huỷ đơn hàng PENDING (cần orderId + trackingCode) |

### Invoice (Public)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 25 | GET | `/invoice/{orderId}` | Public | Tải PDF hoá đơn đơn hàng |

### Orders (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 26 | GET | `/admin/orders` | ADMIN | Danh sách đơn hàng (active/history, tìm kiếm, lọc theo status, sort theo ID) |
| 27 | GET | `/admin/orders/{id}` | ADMIN | Chi tiết đơn hàng |
| 28 | POST | `/admin/orders/{id}/status` | ADMIN | Cập nhật trạng thái (`status` + `redirect` param) |
| 29 | POST | `/admin/orders/{id}/cancel` | ADMIN | Huỷ đơn hàng |

### History (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 30 | GET | `/admin/history` | ADMIN | Tổng quan tài chính theo tháng (Chart.js) |
| 31 | GET | `/admin/history/details?month=&year=` | ADMIN | Chi tiết tháng — danh sách đơn hàng + chi phí |

---

## 🧾 Quỳnh — Recruitment, Auth, Toppings, Dashboard

### Recruitment — Applications (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 32 | GET | `/admin/recruitment` | ADMIN | Danh sách đơn ứng tuyển (phân trang, tìm kiếm) |
| 33 | POST | `/admin/recruitment/{id}/status` | ADMIN | Cập nhật trạng thái đơn (NEW → REVIEWED → INTERVIEWING → HIRED/REJECTED) |
| 34 | POST | `/careers/apply` | Public | Nộp đơn ứng tuyển (fullName, email, phone, position, cvFile PDF) |

### Recruitment — Job Postings (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 35 | GET | `/admin/recruitment/jobs` | ADMIN | Danh sách tin tuyển dụng (active/closed, phân trang) |
| 36 | POST | `/admin/recruitment/jobs/save` | ADMIN | Tạo hoặc cập nhật tin tuyển dụng |
| 37 | GET | `/admin/recruitment/jobs/delete/{id}` | ADMIN | Xóa tin tuyển dụng |
| 38 | GET | `/admin/recruitment/jobs/toggle/{id}` | ADMIN | Bật/tắt trạng thái active |

### Auth & Public Pages

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 39 | GET | `/login` | Public | Trang đăng nhập |
| 40 | POST | `/do-login` | Public | Xử lý đăng nhập (Spring Security) — ADMIN → `/admin/dashboard` |
| 41 | GET | `/logout` | Auth | Đăng xuất |
| 42 | GET | `/about` | Public | Trang giới thiệu quán |

### Toppings (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 43 | GET | `/admin/toppings` | ADMIN | Danh sách topping |
| 44 | POST | `/admin/toppings/save` | ADMIN | Tạo hoặc cập nhật topping |
| 45 | GET | `/admin/toppings/delete/{id}` | ADMIN | Xóa topping |

### Dashboard (Admin)

| # | Method | URL | Auth | Description |
|---|--------|-----|------|-------------|
| 46 | GET | `/admin/dashboard` | ADMIN | Dashboard KPIs (doanh thu, lợi nhuận, đơn hàng) + Chart.js |

---

## Tổng kết phân chia

| Thành viên | Số API | Các route chính |
|-----------|--------|-----------------|
| **Phan** | 15 (#1–15) | `/`, `/product`, `/admin/products`, `/admin/categories` |
| **Hà** | 16 (#16–31) | `/cart`, `/checkout`, `/tracking`, `/invoice`, `/admin/orders`, `/admin/history` |
| **Quỳnh** | 15 (#32–46) | `/admin/recruitment`, `/careers/apply`, `/login`, `/admin/toppings`, `/admin/dashboard` |
