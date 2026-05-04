# Hashiji Cafe – Postman Testing Guide

## Setup

### 1. Create a Collection

1. Open Postman → **New** → **Collection** → Name it `Hashiji Cafe`
2. Under **Variables** tab, add:
   | Variable | Initial Value |
   |---|---|
   | `base_url` | `http://localhost:8080` |
   | `admin_username` | `admin` |
   | `admin_password` | `123456` |

### 2. Configure Session Authentication

Since the app uses form-based login (Spring Security), all admin requests need a session cookie.

1. Add a **Pre-request Script** at the collection level, or create a dedicated **Login** request:
   - Method: `POST`
   - URL: `{{base_url}}/do-login`
   - Body: `x-www-form-urlencoded`
     - `username` = `admin`
     - `password` = `123456`
2. Postman will automatically store the `JSESSIONID` cookie.
3. Make sure **"Automatically follow redirects"** is **ON** in collection settings.
4. All subsequent requests will send the cookie automatically.

> **Tip:** Run the login request first every time you start a testing session.

---

## Test Cases by Module

---

### 🍵 Products (Thành viên 1)

#### TC-01: Get Home Page (public)
- **GET** `{{base_url}}/`
- Expected: `200 OK`, HTML page with product list

#### TC-02: Filter Products by Category (AJAX fragment)
- **GET** `{{base_url}}/products/fragment?categoryId=<UUID>`
- Expected: `200 OK`, HTML fragment with filtered products
- Get a valid categoryId from TC-01 page source or admin categories list

#### TC-03: Product Detail
- **GET** `{{base_url}}/product/<UUID>`
- Expected: `200 OK`, product page with size and topping selectors

#### TC-04: Admin - List Products
- **GET** `{{base_url}}/admin/products`
- Expected: `200 OK` (requires session cookie from login)

#### TC-05: Admin - Create Product
- **POST** `{{base_url}}/admin/products/save`
- Body: `form-data`
  - `name` = `Test Latte`
  - `nameVi` = `Test Latte VI`
  - `description` = `A test product`
  - `category.id` = `<valid category UUID>`
  - `sizes[0].sizeName` = `M`
  - `sizes[0].price` = `45000`
  - `imageFile` = *(leave empty or attach an image file)*
  - `imageUrl` = `https://placehold.co/300`
- Expected: `302` redirect to `/admin/products` with flash "Product saved successfully!"

#### TC-06: Admin - Deactivate Product
- **GET** `{{base_url}}/admin/products/deactivate/<UUID>`
- Expected: `302` redirect

#### TC-07: Admin - Delete Product
- **GET** `{{base_url}}/admin/products/delete/<UUID>`
- Expected: `302` redirect

---

### 🛒 Cart & Orders (Thành viên 2)

#### TC-08: View Cart (empty)
- **GET** `{{base_url}}/cart`
- Expected: `200 OK`, empty cart page

#### TC-09: Add Item to Cart
- **POST** `{{base_url}}/cart/add`
- Body: `x-www-form-urlencoded`
  - `productId` = `<valid product UUID>`
  - `sizeId` = `<valid size UUID>`
  - `quantity` = `2`
  - `sugar` = `100%`
  - `ice` = `100%`
  - `note` = `extra hot`
- Expected: `302` redirect to `/cart`

#### TC-10: Update Cart Item Quantity
- **POST** `{{base_url}}/cart/update`
- Body: `x-www-form-urlencoded`
  - `index` = `0`
  - `quantity` = `3`
- Expected: `302` redirect to `/cart`

#### TC-11: Remove Cart Item
- **GET** `{{base_url}}/cart/remove/0`
- Expected: `302` redirect to `/cart`

#### TC-12: Show Checkout
- **GET** `{{base_url}}/checkout`
- Prerequisite: Cart must be non-empty (run TC-09 first)
- Expected: `200 OK`, checkout form

#### TC-13: Place Order
- **POST** `{{base_url}}/checkout/place-order`
- Body: `x-www-form-urlencoded`
  - `customerName` = `Nguyen Van A`
  - `phone` = `0901234567`
  - `address` = `123 Le Loi, Hanoi`
  - `note` = `Ring doorbell`
- Expected: `200 OK`, success page showing tracking code (format: `ORD-XXXXXX`)

#### TC-14: Track Order
- **GET** `{{base_url}}/tracking/search?code=ORD-XXXXXX`
- Use tracking code from TC-13
- Expected: `200 OK`, order details shown

#### TC-15: Download Invoice (PDF)
- **GET** `{{base_url}}/invoice/<order-UUID>`
- Expected: `200 OK`, `Content-Type: application/pdf`
- Tip: In Postman click **"Send and Download"** to save the PDF

#### TC-16: Admin - List Orders
- **GET** `{{base_url}}/admin/orders`
- Expected: `200 OK`, order management table

#### TC-17: Admin - Update Order Status
- **POST** `{{base_url}}/admin/orders/<order-UUID>/status`
- Body: `x-www-form-urlencoded`
  - `status` = `CONFIRMED`
- Expected: `302` redirect to `/admin/orders/<UUID>`

#### TC-18: Admin - Cancel Order
- **POST** `{{base_url}}/admin/orders/<order-UUID>/cancel`
- Expected: `302` redirect

---

### 👤 Admin Management (Quỳnh)


#### TC-19: Admin - List Job Applications
- **GET** `{{base_url}}/admin/recruitment`
- Expected: `200 OK`, application list

#### TC-20: Public - Submit Job Application
- **POST** `{{base_url}}/careers/apply`
- Body: `form-data`
  - `fullName` = `Le Van C`
  - `email` = `levanc@example.com`
  - `phone` = `0912345678`
  - `position` = `Barista`
  - `cvFile` = *(attach any PDF)*
- Expected: `302` redirect to `/#careers` with flash success and `trackingCode` (format: `CV-XXXXXXXX`)

#### TC-21: Admin - Update Application Status
- **POST** `{{base_url}}/admin/recruitment/<application-UUID>/status`
- Body: `x-www-form-urlencoded`
  - `status` = `ACCEPTED`
- Expected: `302` redirect

#### TC-22: Admin - List Job Postings
- **GET** `{{base_url}}/admin/recruitment/jobs`
- Expected: `200 OK`, list of job postings with codes (JOB-000001, etc.)

#### TC-23: Admin - Create Job Posting
- **POST** `{{base_url}}/admin/recruitment/jobs/save`
- Body: `x-www-form-urlencoded`
  - `title` = `Test Barista`
  - `location` = `Hanoi`
  - `type` = `FULL_TIME`
  - `description` = `Make great coffee`
  - `requirements` = `1 year experience`
- Expected: `302` redirect to `/admin/recruitment/jobs`

#### TC-24: Admin - Toggle Job Active Status
- **GET** `{{base_url}}/admin/recruitment/jobs/toggle/<job-UUID>`
- Expected: `302` redirect

---

## Common Issues

| Problem | Fix |
|---|---|
| `403 Forbidden` on admin routes | Run the login request first to get a session cookie |
| `302` redirect loops | Ensure "Follow redirects" is ON in Postman settings |
| Empty cart redirect | Add items to cart before testing checkout |
| `UUID` format errors | UUIDs must be format `xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx` |
| PDF not rendering | Use "Send and Download" in Postman for binary responses |
