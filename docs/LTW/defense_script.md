# Hashiji Cafe – Defense Script (Kịch bản bảo vệ bài tập lớn)

> Đây là kịch bản Q&A để chuẩn bị cho buổi bảo vệ môn Lập trình Web.
> Mỗi thành viên nên nắm vững phần API được phân công. Phần chung cả nhóm phải hiểu.

---

## 🔥 Câu hỏi chung (Cả nhóm)

---

**Q: Hệ thống của các em xây dựng bằng gì? Tại sao chọn Spring Boot?**

> Spring Boot giúp cấu hình tự động (auto-configuration), tích hợp Spring Security, Spring Data JPA, Thymeleaf dễ dàng. Phù hợp cho dự án web MVC vừa và nhỏ, build nhanh và test được bằng JUnit ngay trong project.

---

**Q: Database các em dùng gì? Giải thích cấu trúc chính?**

> PostgreSQL 15. Các bảng chính: `users`, `products`, `categories`, `product_sizes`, `toppings`, `orders`, `order_items`, `expenses`, `job_postings`, `job_applications`.
> Primary key dùng UUID để tránh sequential ID guessing.
> `order_items` lưu snapshot (tên sản phẩm, giá tại thời điểm mua) vì sản phẩm có thể bị sửa/xóa sau này — đây là kỹ thuật phổ biến trong thương mại điện tử.

---

**Q: Giải thích Trigger và Stored Procedure trong project?**

> Có trong `schema-advanced.sql`:
> - **Stored Procedure** `place_order`: Tạo đơn hàng nguyên tử, kiểm tra địa chỉ, áp dụng khuyến mãi.
> - **Stored Procedure** `get_revenue_report`: Thống kê doanh thu theo ngày trong khoảng thời gian.
>
> **Trigger:**
> - `trg_update_product_rating`: Tự động cập nhật `avg_rating`, `review_count` khi có đánh giá mới.
> - `trg_log_cart_behavior`: Ghi log hành vi khi thêm vào giỏ hàng.
> - `trg_single_default_address`: Đảm bảo mỗi user chỉ có 1 địa chỉ `is_default = TRUE`.

---

**Q: Các em xử lý bảo mật như thế nào?**

> Dùng Spring Security 6:
> - Form login với `/do-login`, logout với `/logout`.
> - Phân quyền theo role: `ROLE_ADMIN` truy cập `/admin/**`, còn lại public.
> - Password hash bằng BCrypt — không lưu plaintext.
> - CSRF protection được Spring Security bật mặc định.
> - Custom `AuthenticationSuccessHandler` redirect: ADMIN → `/admin/dashboard`.

---

**Q: Giải thích luồng đặt hàng của khách?**

> 1. Khách vào trang chủ → Xem menu → Chọn sản phẩm (kích thước, topping, đường, đá)
> 2. Nhấn "Add to Cart" → `POST /cart/add` → Lưu vào `HttpSession`
> 3. Vào `/cart` xem giỏ → Checkout tại `POST /checkout/place-order`
> 4. Hệ thống tạo Order trong DB, sinh tracking code dạng `ORD-XXXXXX`
> 5. Khách dùng tracking code tra cứu tại `/tracking/search?code=ORD-XXXXXX`
> 6. Download hoá đơn PDF tại `/invoice/{orderId}`

---

**Q: Giỏ hàng được lưu ở đâu? Tại sao không lưu DB?**

> Lưu trong `HttpSession` (bộ nhớ server-side). Lý do:
> - Đơn giản, không cần bảng DB riêng cho anonymous user.
> - Giỏ hàng tự xóa khi session hết hạn — không để lại rác trong DB.
> - Với hệ thống quy mô lớn hơn sẽ cần lưu DB hoặc Redis để persist qua nhiều server.

---

**Q: Transaction được xử lý như thế nào?**

> Method `placeOrder` trong `OrderService` dùng annotation `@Transactional`.
> Nếu lỗi xảy ra trong quá trình lưu Order hoặc OrderItems, Spring sẽ rollback toàn bộ.
> Trong `schema-advanced.sql` còn có ví dụ Transaction thủ công bằng SQL thuần (BEGIN/COMMIT/ROLLBACK).

---

**Q: Caching hoạt động thế nào trong project?**

> Dùng Spring Cache với Redis làm backend:
> - `@Cacheable("categories")` trong `CategoryService.getAllCategories()` — giảm tải DB khi menu hiển thị.
> - `@Cacheable("products")` trong `ProductService` — cache kết quả tìm kiếm sản phẩm.
> - `@CacheEvict` được gọi khi save/delete category hoặc product.
> - `DataSeeder` evict toàn bộ cache sau khi seed xong để tránh dữ liệu stale.

---

## 📦 Câu hỏi cho Phan — Products, Categories, Toppings, Dashboard

**Q: Upload ảnh sản phẩm xử lý ra sao?**
> File upload dùng `MultipartFile`, lưu vào thư mục `uploads/products/` với tên UUID-prefixed.
> Đường dẫn `/uploads/products/filename.jpg` được lưu vào cột `image` của `products`.
> Nếu nhập URL thay vì upload, hệ thống lưu nguyên URL đó.

**Q: Tại sao có cả `activate` và `deactivate` thay vì `delete`?**
> Soft-delete: Sản phẩm không hiện trên menu nhưng vẫn giữ lịch sử `order_items`.
> Xóa thật có thể gây lỗi FK constraint từ `order_items`.

**Q: AJAX filter menu hoạt động thế nào?**
> Frontend gọi `GET /products/fragment?categoryId=X` → Controller trả về Thymeleaf fragment `home :: productList` (một phần HTML).
> JS thay thế nội dung div mà không reload toàn trang.

**Q: ProductCode được tạo ra như thế nào?**
> `DataSeeder` dùng counter tăng dần: `PRD-00001`, `PRD-00002`, ...
> Với sản phẩm mới tạo qua UI, `CatalogMetadataBackfillRunner` tự động gán `displayCode` tuần tự cho bất kỳ entity nào chưa có code (products, categories, jobs).

**Q: Tại sao cần Redis cache cho categories?**
> Menu phía customer gọi `getAllCategories()` ở mọi request. Cache giúp tránh query DB lặp đi lặp lại.
> Khi admin thêm/sửa/xóa category thì `@CacheEvict` tự động xóa cache cũ.

---

## 📦 Câu hỏi cho Hà — Orders, Cart, Checkout, Tracking, Invoice, History

**Q: Tracking code được sinh ra như thế nào?**
> Lấy UUID ngẫu nhiên, xóa dấu `-`, uppercase, lấy 6 ký tự đầu, prefix `ORD-`. Ví dụ: `ORD-A8F2C1`.

**Q: Làm sao đảm bảo tracking code là duy nhất?**
> Xác suất trùng với 6 ký tự hex là cực thấp. Trong production nên thêm DB unique constraint và retry nếu trùng.

**Q: Luồng trạng thái đơn hàng hoạt động thế nào?**
> `PENDING → CONFIRMED → SHIPPING → COMPLETED`, có thể CANCELLED ở bất kỳ bước nào (trừ COMPLETED).
> Admin nhấn nút tương ứng trên trang danh sách: Accept / Ship / Complete / Cancel.
> Sau khi nhấn từ trang danh sách, redirect về `/admin/orders`. Từ trang detail thì ở lại detail (dùng hidden `redirect=detail`).

**Q: Invoice PDF được tạo bằng thư viện gì?**
> OpenPDF (fork của iText 4). Tạo trực tiếp trên `response.getOutputStream()`, không lưu file trên server.

**Q: Tracking page hiển thị trạng thái đơn như thế nào?**
> Progress bar 4 bước: PENDING=25%, CONFIRMED=50%, SHIPPING=75%, COMPLETED=100%.
> Nếu CANCELLED: ẩn progress bar, hiện thông báo đỏ.
> Badge màu: PENDING/CONFIRMED/SHIPPING = xanh, COMPLETED = xanh lá, CANCELLED = đỏ.

**Q: History tài chính được tính như thế nào?**
> `AdminHistoryController` query `OrderRepository` theo tháng/năm.
> Revenue = tổng `total_amount` của đơn COMPLETED trong tháng.
> Dữ liệu trả về Chart.js vẽ line chart 10 tháng gần nhất.

---

## 📦 Câu hỏi cho Quỳnh — Recruitment, Auth

**Q: Job Application tracking code khác Order tracking code không?**
> Có. Order: `ORD-XXXXXX`. Job application: `CV-XXXXXXXX` (8 ký tự).
> Cùng endpoint `/tracking/search` nhưng hệ thống tìm Order trước, nếu không thấy mới tìm JobApplication.

**Q: Trạng thái đơn ứng tuyển có những bước nào?**
> `NEW → REVIEWED → INTERVIEWING → HIRED` hoặc `REJECTED`.
> Admin cập nhật thủ công qua `POST /admin/recruitment/{id}/status`.

**Q: Upload CV xử lý ra sao?**
> Tương tự upload ảnh sản phẩm: `MultipartFile` lưu vào `uploads/cvs/`, chỉ chấp nhận file PDF.
> Đường dẫn file được lưu vào `job_applications.cv_file_path`.

**Q: Login redirect hoạt động thế nào?**
> `CustomAuthenticationSuccessHandler` kiểm tra role sau khi đăng nhập:
> - `ROLE_ADMIN` → redirect `/admin/dashboard`
> - Các role khác → redirect `/`

---

## 💡 Câu hỏi hay bị hỏi thêm

**Q: Có implement i18n không?**
> Có. `messages.properties` cho các thông báo lỗi (như "Không tìm thấy đơn hàng", "Nộp đơn thành công").
> `MessageSource` trả message theo `Locale` của request.

**Q: Làm sao biết product nào đang bán chạy?**
> `OrderRepository.findTopSellingProductByMonth()` — JPQL query group by `snapshotProductName`, sum `quantity`, limit 5.
> Kết quả dùng cho Chart.js trên dashboard và history.

**Q: DataSeeder làm gì khi app khởi động?**
> Xóa sạch theo thứ tự FK-safe: `order_items → orders → job_applications → job_postings → users → product_sizes → products → categories`.
> Seed lại toàn bộ với ID chuẩn (`CAT-00001`, `PRD-00001`, `ORD-000001`, `JOB-000001`).
> `CatalogMetadataBackfillRunner` chạy sau seeder, gán `displayCode` tuần tự cho bất kỳ entity nào thiếu code.
> Evict toàn bộ Redis cache sau khi seed xong để tránh dữ liệu stale.

**Q: Tại sao dùng UUID thay vì auto-increment?**
> UUID globally unique, tránh sequential ID guessing (bảo mật hơn khi ID xuất hiện trong URL).
> Tuy nhiên để dễ nhìn, mỗi entity có thêm `displayCode` (`ORD-000001`, `PRD-00001`, `JOB-000001`...) dùng để hiển thị.
