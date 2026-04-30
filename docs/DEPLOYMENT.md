# Local PostgreSQL Deployment Guide

Tai lieu nay huong dan setup PostgreSQL local de chay Hashiji Cafe ma khong dung Docker.

## Seed modes

Project hien co 2 che do seed rieng:

1. `dev` profile
   - App tu seed du lieu qua [DataSeeder.java](/src/main/java/com/coffeeshop/config/DataSeeder.java)
   - Xoa sach va tao lai toan bo du lieu moi khi khoi dong
   - Phu hop cho local dev, UI demo, dashboard/history chart

2. SQL-only demo mode
   - Tat Java seed bang `APP_PROFILE=prod` hoac `app.seed-data=false`
   - Import thu cong [schema-advanced.sql](/src/main/resources/schema-advanced.sql) va [seed-data.sql](/src/main/resources/seed-data.sql)
   - Phu hop cho DBMS presentation

Khong nen dung ca 2 nguon seed cung luc, vi du lieu co the bi ghi de.

## Requirements

- PostgreSQL 15+
- `psql` hoac pgAdmin/DBeaver
- Java 17+

## Step 1: Create database and user

Dang nhap vao PostgreSQL bang superuser:

```bash
psql -U postgres
```

Chay:

```sql
CREATE DATABASE cafe_db;
CREATE USER cafe_admin WITH ENCRYPTED PASSWORD '123';
GRANT ALL PRIVILEGES ON DATABASE cafe_db TO cafe_admin;
\c cafe_db
GRANT ALL ON SCHEMA public TO cafe_admin;
\q
```

## Step 2: Configure Spring Boot

App hien doc datasource tu environment variables:

```properties
DB_URL=jdbc:postgresql://localhost:5432/cafe_db
DB_USERNAME=cafe_admin
DB_PASSWORD=123
```

Neu chay bang file properties, cac gia tri mac dinh trong [application.properties](/src/main/resources/application.properties) da tro ve local PostgreSQL.

## Step 3A: Run dev / UI demo mode

Dung cach nay neu ban muon co du lieu orders, expenses, history chart ngay trong app.

```bash
# Windows
$env:APP_PROFILE="dev"
.\mvnw.cmd spring-boot:run

# Mac/Linux
APP_PROFILE=dev ./mvnw spring-boot:run
```

Trong mode nay:
- Hibernate tao/cap nhat bang
- `DataSeeder` xoa sach va seed lai: categories, products, users, orders, expenses
- Du lieu duoc seed tu dong moi lan khoi dong
- Khong import `seed-data.sql`

**Tai khoan duy nhat trong dev mode:**
- `admin` / `123456` (ADMIN)

**Du lieu duoc seed (dev mode):**

| Entity | Format |
|--------|--------|
| Category | `CAT-00001`, `CAT-00002`, ... |
| Product  | `PRD-00001`, `PRD-00002`, ... |
| Order    | `ORD-000001`, `ORD-000002`, ... |

**Categories duoc seed:** Coffee, Tea, Smoothie, Juice

**Products duoc seed:**
- Cafe Latte, Espresso (Coffee)
- Peach Tea, Sakura Blossom Tea (Tea)
- Strawberry Smoothie (Smoothie)
- Coconut Juice (Juice)

**Revenue vs Expense:**
- ~100-150 orders moi thang (10 thang lich su)
- Revenue duoc dam bao cao hon Expense de dashboard hien thi co loi nhuan

## Step 3B: Run SQL-only DBMS demo mode

Dung cach nay neu ban can demo procedures, triggers, va raw SQL data.

### 1. Start app once without Java seed

Can cho app chay 1 lan de Hibernate tao cac bang co so.

```bash
# Windows
$env:APP_PROFILE="prod"
.\mvnw.cmd spring-boot:run

# Mac/Linux
APP_PROFILE=prod ./mvnw spring-boot:run
```

Cho den khi console bao app da start xong. Luc nay bang da duoc tao. Co the dung app sau do.

### 2. Import SQL scripts manually

```powershell
# Windows PowerShell
$Env:PGCLIENTENCODING = 'utf-8'
chcp 65001
psql -U cafe_admin -d cafe_db -f src/main/resources/schema-advanced.sql
psql -U cafe_admin -d cafe_db -f src/main/resources/seed-data.sql
```

```bash
# Mac/Linux
psql -U cafe_admin -d cafe_db -f src/main/resources/schema-advanced.sql
psql -U cafe_admin -d cafe_db -f src/main/resources/seed-data.sql
```

Trong mode nay:
- `schema-advanced.sql` them functions/triggers/procedures
- `seed-data.sql` nap bo du lieu SQL thu cong (khong co Tiramisu, khong co Cake)
- Khong bat lai `dev` profile neu ban muon giu nguyen bo du lieu SQL

**Tai khoan test theo `seed-data.sql`:**
- `admin` / `password`
- `user1` / `password`
- `user2` / `password`

## pgAdmin flow

Neu dung pgAdmin thay vi `psql`:

1. Chon database `cafe_db`
2. Mo `Query Tool`
3. Chay toan bo `schema-advanced.sql`
4. Neu dang o SQL-only demo mode, chay tiep `seed-data.sql`

## Recommendation

- Local dev va UI demo: dung `APP_PROFILE=dev`
- DBMS presentation: dung `APP_PROFILE=prod` va import SQL thu cong
- Xem them [docs/SEEDING.md](/docs/SEEDING.md)
