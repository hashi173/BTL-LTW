# Hashiji Cafe – Defense Script (Kịch bản bảo vệ bài tập lớn)

> Đây là kịch bản Q&A để chuẩn bị cho buổi bảo vệ môn Lập trình Web.
> Mỗi thành viên nên nắm vững phần API được phân công. Phần chung cả nhóm phải hiểu.

---

## 🔥 Câu hỏi chung (Cả nhóm)

---

**Q: Hệ thống của các em xây dựng bằng gì? Tại sao chọn Spring Boot?**

> Spring Boot giúp cấu hình tự động (auto-configuration), tích hợp Spring Security, Spring Data JPA, Thymeleaf dễ dàng. Phù hợp cho dự án web MVC vừa và nhỏ, build nhanh, có thể deploy lên Docker.

---

**Q: Database các em dùng gì? Giải thích cấu trúc chính?**

> PostgreSQL. Các bảng chính: `users`, `products`, `categories`, `orders`, `order_items`, `work_shifts`, `expenses`, `job_applications`.
> Primary key dùng UUID để tránh sequential ID guessing.
> `order_items` lưu snapshot (tên, giá tại thời điểm mua) vì sản phẩm có thể bị sửa/xóa sau này — đây là kỹ thuật phổ biến trong thương mại điện tử.

---

**Q: Giải thích Trigger và Stored Procedure trong project?**

> Có 2 Stored Procedure:
> - `place_order`: Tạo đơn hàng nguyên tử, kiểm tra địa chỉ, áp dụng khuyến mãi.
> - `get_revenue_report`: Thống kê doanh thu theo ngày trong khoảng thời gian.
>
> Có 3 Trigger:
> - `trg_update_product_rating`: Tự động cập nhật `avg_rating`, `review_count` khi có đánh giá mới.
> - `trg_log_cart_behavior`: Ghi log hành vi người dùng khi thêm vào giỏ hàng (ADD_TO_CART).
> - `trg_single_default_address`: Đảm bảo mỗi user chỉ có 1 địa chỉ `is_default = TRUE`.

---

**Q: Các em xử lý bảo mật như thế nào?**

> Dùng Spring Security 6:
> - Form login với `/do-login`, logout với `/logout`.
> - Phân quyền theo role: `ROLE_ADMIN` truy cập `/admin/**`, còn lại public hoặc authenticated.
> - Password hash bằng BCrypt.
> - CSRF protection được Spring Security bật mặc định.
> - Custom `AuthenticationSuccessHandler` redirect: ADMIN → `/admin/dashboard`, các role khác → `/`.

---

**Q: Giải thích luồng đặt hàng của khách?**

> 1. Khách vào trang chủ → Xem menu → Chọn sản phẩm (kích thước, topping, đường, đá)
> 2. Nhấn "Add to Cart" → `POST /cart/add` → Lưu vào `HttpSession`
> 3. Vào `/cart` xem giỏ → Checkout tại `/checkout/place-order`
> 4. Hệ thống tạo Order trong DB, sinh tracking code dạng `ORD-XXXXXX`
> 5. Khách dùng tracking code tra cứu tại `/tracking/search?code=ORD-XXXXXX`
> 6. Download hóa đơn PDF tại `/invoice/{orderId}`

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

## 📦 Câu hỏi cho Thành viên 2 (Orders, Cart, Checkout, Tracking)

**Q: Tracking code được sinh ra như thế nào?**
> Lấy UUID ngẫu nhiên, xóa dấu `-`, uppercase, lấy 6 ký tự đầu, prefix `ORD-`. Ví dụ: `ORD-A8F2C1`.

**Q: Làm sao đảm bảo tracking code là duy nhất?**
> Xác suất trùng với 6 ký tự hex là cực thấp. Trong production nên thêm DB unique constraint và retry nếu trùng.

**Q: Admin update status order thì inventory xử lý ra sao?**
> Khi status chuyển sang `COMPLETED` lần đầu (kiểm tra `previousStatus != COMPLETED`), `OrderService` sẽ duyệt `order_items`, theo `ProductRecipe` trừ `stock_quantity` của từng `Ingredient`.

**Q: Invoice PDF được tạo bằng thư viện gì?**
> OpenPDF (fork của iText 4). Tạo trực tiếp trên `response.getOutputStream()`, không lưu file.

---

## 📦 Câu hỏi cho Thành viên 1 (Products, Categories, Toppings, Ingredients)

**Q: Upload ảnh sản phẩm xử lý ra sao?**
> File upload dùng `MultipartFile`, lưu vào thư mục `uploads/products/` với tên UUID-prefixed. Đường dẫn `/uploads/products/filename.jpg` được lưu vào cột `image` của `products`.

**Q: Tại sao có cả `activate` và `deactivate` thay vì `delete`?**
> Soft-delete: Sản phẩm không hiện trên menu nhưng vẫn giữ lịch sử order_items. Xóa thật có thể gây lỗi FK constraint từ orders.

**Q: Ingredient và ProductRecipe liên quan nhau thế nào?**
> `product_recipes` là bảng junction: mỗi dòng ánh xạ một `product` với một `ingredient` và số lượng cần dùng (`quantity_required`). Khi order hoàn thành, system trừ tồn kho theo recipe.

**Q: AJAX filter menu hoạt động thế nào?**
> Frontend gọi `GET /products/fragment?categoryId=X` → Controller trả về Thymeleaf fragment `home :: productList` (một phần HTML). JS thay thế nội dung div mà không reload toàn trang.

---

## 📦 Câu hỏi cho Quỳnh Phan Hà (Users, Shifts, Expenses, Recruitment)

**Q: Tạo User mới thì password mặc định là gì?**
> `123456`, được BCrypt hash trước khi lưu. Admin cần hướng dẫn nhân viên đổi password sau lần đăng nhập đầu.

**Q: Work Shift quản lý ca làm việc thế nào?**
> Admin tạo ca (userId + startTime). Khi kết thúc ca, system tính `total_revenue` từ orders trong khoảng thời gian đó, tính `cash_variance = end_cash - (start_cash + revenue)`.

**Q: Payroll được tính thế nào?**
> `WorkShiftService.calculateTotalPayroll()` lặp qua tất cả CLOSED shifts, tính `hours * user.hourlyRate` rồi cộng tổng. Đây là ước tính MVP; hệ thống thực tế cần xử lý phức tạp hơn (OT, tax, v.v.)

**Q: Job Application tracking code khác Order tracking code không?**
> Có. Order: `ORD-XXXXXX`. Job application: `CV-XXXXXXXX` (8 ký tự). Cùng endpoint `/tracking/search` nhưng hệ thống tìm Order trước, nếu không thấy mới tìm JobApplication.

**Q: Tại sao deleteUser có thể fallback sang deactivate?**
> Nếu user đã có orders hoặc shifts thì xóa thật sẽ vi phạm FK constraint. Thay vì hiện lỗi xấu, hệ thống set `is_active = false` và thông báo rõ lý do cho admin.

---

## 💡 Câu hỏi hay bị hỏi thêm

**Q: Redis dùng để làm gì trong project?**
> Cấu hình trong `RedisConfig.java` và `docker-compose.yml`. Hiện tại dùng để lưu HTTP session (Spring Session), cho phép session persist qua server restart.

**Q: Có implement i18n không?**
> Có. `messages_en.properties` và `messages_vi.properties`. Các thông báo lỗi (như order not found, apply success/error) được lấy qua `MessageSource` theo locale của browser.

**Q: Làm sao biết product nào đang bán chạy?**
> `OrderRepository.findTopSellingProductByMonth()` — JPQL query group by `product_id`, sum `quantity`, limit 5. Kết quả dùng cho Chart.js trên dashboard và history.

**Q: Deployment thế nào?**
> Docker Compose: 3 service — PostgreSQL, Redis, Spring Boot app. Xem `Dockerfile` và `docker-compose.yml`. Chi tiết trong `docs/DEPLOYMENT.md`.
