# Seeding Modes

This project uses two distinct seeding modes. They should not be used together unless you intentionally want one source to overwrite the other.

## 1. Dev / UI Demo Mode

- Active profile: `dev`
- Enabled by: `application-dev.properties` (`app.seed-data=true`)
- Seed source: `DataSeeder.java`

Use this mode when you want:
- Live demo data for orders, categories, products
- Dashboard/history chart with realistic revenue vs expense data
- A fresh dataset every time the app restarts

### What gets seeded

| Type | Count | Format |
|------|-------|--------|
| Categories | 4 | `CAT-00001` → `CAT-00004` |
| Products | 6 | `PRD-00001` → `PRD-00006` |
| Orders (history) | ~100–150 per month × 10 months | `ORD-000001`, `ORD-000002`, ... |
| Orders (active) | 5 | PENDING / CONFIRMED |
| Expenses | 4 per month × 10 months | Utilities, Ingredients, Rent, Payroll |

### Single account in dev mode

| Username | Password | Role |
|----------|----------|------|
| `admin`  | `123456` | ADMIN |

### Order status flow

```
PENDING → CONFIRMED → SHIPPING → COMPLETED
                               ↘ CANCELLED (any stage)
```

Admin actions per status (orders list page):
- **PENDING** → Accept button
- **CONFIRMED** → Ship button
- **SHIPPING** → Complete button
- Any active status → Cancel button

### Notes on cleanup

`cleanupData()` deletes in FK-safe order on every startup:
```
order_items → orders → expenses → users → product_sizes → products → categories
```
This ensures Cake, Tiramisu, and all stale data from old seeds are removed.

---

## 2. DBMS / SQL-only Demo Mode

- Active profile: `prod` or any profile with `app.seed-data=false`
- Seed source: `seed-data.sql`
- Advanced database objects: `schema-advanced.sql`

Use this mode when you want:
- A pure SQL demo
- Manual control via `psql` or pgAdmin
- To present triggers, procedures, and raw SQL data setup

### SQL seed data includes

- Categories: Coffee, Tea, Smoothie (no Cake)
- Products: Espresso, Latte, Peach Tea, Cappuccino, Americano, Matcha Latte, Mango Smoothie (no Tiramisu)
- 5 sample orders with order items
- 3 job postings

### Accounts in SQL seed

| Username | Password | Role |
|----------|----------|------|
| `admin`  | `password` (bcrypt) | ADMIN |
| `user1`  | `password` (bcrypt) | USER |
| `user2`  | `password` (bcrypt) | USER |

---

## Recommendation

- Use `dev` for local app development and UI demos.
- Use manual SQL import only for the DBMS presentation flow.
- See [DEPLOYMENT.md](./DEPLOYMENT.md) for full setup steps.
