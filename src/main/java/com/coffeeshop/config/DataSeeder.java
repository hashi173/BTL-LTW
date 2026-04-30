package com.coffeeshop.config;

import com.coffeeshop.entity.JobPosting;
import com.coffeeshop.entity.Role;
import com.coffeeshop.repository.CategoryRepository;
import com.coffeeshop.repository.ExpenseRepository;
import com.coffeeshop.repository.JobPostingRepository;
import com.coffeeshop.repository.OrderItemRepository;
import com.coffeeshop.repository.OrderRepository;
import com.coffeeshop.repository.ProductRepository;
import com.coffeeshop.repository.ProductSizeRepository;
import com.coffeeshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import org.springframework.cache.CacheManager;

import java.math.BigDecimal;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

        private final JobPostingRepository jobPostingRepository;
        private final CategoryRepository categoryRepository;
        private final ProductRepository productRepository;
        private final ProductSizeRepository productSizeRepository;
        private final OrderRepository orderRepository;
        private final OrderItemRepository orderItemRepository;
        private final ExpenseRepository expenseRepository;
        private final UserRepository userRepository;
        private final PasswordEncoder passwordEncoder;
        private final CacheManager cacheManager;

        private int categoryCounter = 0;
        private int productCounter = 0;
        private int orderCounter = 0;

        @Override
        public void run(String... args) {
                cleanupData();
                categoryCounter = 0;
                productCounter = 0;
                orderCounter = 0;
                seedJobs();
                seedProducts();
                seedUsers();
                seedHistory();
                seedActiveData();
                // Evict all caches so stale category/product data is cleared
                cacheManager.getCacheNames().forEach(name -> {
                        var cache = cacheManager.getCache(name);
                        if (cache != null) cache.clear();
                });
                log.info("All caches cleared after seeding.");
        }

        private void cleanupData() {
                try {
                        orderItemRepository.deleteAll();
                        orderRepository.deleteAll();
                        expenseRepository.deleteAll();
                        userRepository.deleteAll();
                        productSizeRepository.deleteAll();
                        productRepository.deleteAll();
                        categoryRepository.deleteAll();
                } catch (Exception e) {
                        log.warn("Warning during cleanup: {}", e.getMessage());
                }
        }

        private void seedUsers() {
                if (userRepository.findByUsername("admin").isEmpty()) {
                        com.coffeeshop.entity.User admin = new com.coffeeshop.entity.User();
                        admin.setUsername("admin");
                        admin.setPassword(passwordEncoder.encode("123456"));
                        admin.setFullName("System Administrator");
                        admin.setRole(Role.ADMIN);
                        admin.setUserCode("ADM01");
                        admin.setActive(true);
                        userRepository.save(admin);
                }
        }



        private void seedHistory() {
                com.coffeeshop.entity.User admin = userRepository.findByUsername("admin").orElse(null);
                if (admin == null) {
                        return;
                }

                java.util.List<com.coffeeshop.entity.Product> products = productRepository.findAll();
                if (products.isEmpty()) {
                        return;
                }

                java.util.Random rand = new java.util.Random(20260501L);
                java.time.LocalDate today = java.time.LocalDate.now();
                java.util.List<com.coffeeshop.entity.User> activeUsers = userRepository.findAll().stream()
                                .filter(com.coffeeshop.entity.User::isActive)
                                .toList();

                for (int offset = 9; offset >= 0; offset--) {
                        java.time.YearMonth period = java.time.YearMonth.from(today.minusMonths(offset));
                        int month = period.getMonthValue();
                        int year = period.getYear();
                        int ordersCount = 100 + ((9 - offset) % 4) * 15 + (offset == 0 ? 10 : 0);

                        for (int sequence = 1; sequence <= ordersCount; sequence++) {
                                int safeDay = 1 + (((sequence - 1) * 2) % period.lengthOfMonth());
                                java.time.LocalDate orderDate = period.atDay(safeDay);
                                if (period.equals(java.time.YearMonth.from(today)) && orderDate.isAfter(today)) {
                                        orderDate = today.minusDays((sequence - 1) % Math.max(today.getDayOfMonth(), 1));
                                }

                                com.coffeeshop.entity.User orderUser = !activeUsers.isEmpty()
                                                ? activeUsers.get((sequence - 1) % activeUsers.size())
                                                : admin;

                                seedCompletedOrder(
                                                orderUser,
                                                products,
                                                rand,
                                                orderDate.atTime(8 + ((sequence - 1) % 10), ((sequence - 1) * 7) % 60),
                                                month,
                                                sequence);
                        }

                        seedExpense(period, "Utilities", "Utilities " + month + "/" + year,
                                        1_200_000.0 + (9 - offset) * 45_000, 5);
                        seedExpense(period, "Ingredients", "Ingredients Supply " + month + "/" + year,
                                        2_800_000.0 + (9 - offset) * 150_000, 10);
                        seedExpense(period, "Rent", "Shop Rent " + month + "/" + year, 5_000_000.0, 1);
                        seedExpense(period, "Payroll", "Payroll " + month + "/" + year,
                                        1_800_000.0 + activeUsers.size() * 180_000, 25);

                        log.info("Seeded {} history orders for {}/{}", ordersCount, month, year);
                }

                log.info("History data seeded for the last 10 months.");
        }

        private void seedCompletedOrder(com.coffeeshop.entity.User user,
                        java.util.List<com.coffeeshop.entity.Product> products,
                        java.util.Random rand,
                        java.time.LocalDateTime createdAt,
                        int month,
                        int sequence) {
                com.coffeeshop.entity.Order order = new com.coffeeshop.entity.Order();
                order.setUser(user);
                order.setCustomerName("History Customer " + month + "-" + sequence);
                order.setOrderType(sequence % 2 == 0 ? "In-Store Order" : "Takeaway");
                order.setStatus(com.coffeeshop.entity.OrderStatus.COMPLETED);
                order.setOrderStatus(com.coffeeshop.entity.OrderStatus.COMPLETED.name());
                order.setCreatedAt(createdAt);

                double total = 0;
                int itemsCount = 1 + rand.nextInt(3);
                java.util.List<com.coffeeshop.entity.OrderItem> details = new java.util.ArrayList<>();

                for (int index = 0; index < itemsCount; index++) {
                        com.coffeeshop.entity.Product product = products.get((sequence + index) % products.size());
                        double basePrice = product.getBasePrice() != null
                                        ? product.getBasePrice().doubleValue()
                                        : 40_000.0;
                        double price = basePrice + (rand.nextInt(3) * 5_000);
                        int quantity = 1 + ((sequence + index) % 2);

                        com.coffeeshop.entity.OrderItem item = new com.coffeeshop.entity.OrderItem();
                        item.setOrder(order);
                        item.setProduct(product);
                        item.setSnapshotProductName(product.getName());
                        item.setQuantity(quantity);
                        item.setSnapshotUnitPrice(BigDecimal.valueOf(price));
                        item.setSubTotal(BigDecimal.valueOf(price * quantity));
                        total += price * quantity;
                        details.add(item);
                }

                order.setTotalAmount(total);
                order.setGrandTotal(BigDecimal.valueOf(total));
                order.setTrackingCode(String.format("ORD-%06d", ++orderCounter));
                com.coffeeshop.entity.Order saved = orderRepository.save(order);

                for (com.coffeeshop.entity.OrderItem item : details) {
                        item.setOrder(saved);
                        orderItemRepository.save(item);
                }
        }

        private void seedExpense(java.time.YearMonth period, String category, String description, double amount, int day) {
                com.coffeeshop.entity.Expense expense = new com.coffeeshop.entity.Expense();
                expense.setDescription(description);
                expense.setAmount(amount);
                expense.setCategory(category);
                expense.setExpenseDate(period.atDay(Math.min(day, period.lengthOfMonth())));
                expenseRepository.save(expense);
        }

        private void seedActiveData() {
                java.time.LocalDateTime now = java.time.LocalDateTime.now();
                java.util.List<com.coffeeshop.entity.Product> products = productRepository.findAll();
                java.util.Random rand = new java.util.Random(20260501L + 1);
                com.coffeeshop.entity.User adminUser = userRepository.findByUsername("admin").orElse(null);

                for (int i = 0; i < 5; i++) {
                        com.coffeeshop.entity.Order order = new com.coffeeshop.entity.Order();
                        order.setUser(adminUser);
                        order.setCustomerName("Active Customer " + (i + 1));
                        order.setOrderType("In-Store Order");

                        com.coffeeshop.entity.OrderStatus status = (i % 2 == 0)
                                        ? com.coffeeshop.entity.OrderStatus.PENDING
                                        : com.coffeeshop.entity.OrderStatus.CONFIRMED;
                        order.setStatus(status);
                        order.setOrderStatus(status.name());
                        order.setCreatedAt(now.minusMinutes(10 + (i * 15)));

                        double total = 0;
                        int itemsCount = 1 + rand.nextInt(2);
                        java.util.List<com.coffeeshop.entity.OrderItem> details = new java.util.ArrayList<>();
                        for (int k = 0; k < itemsCount; k++) {
                                com.coffeeshop.entity.Product product = products.get(rand.nextInt(products.size()));
                                com.coffeeshop.entity.OrderItem item = new com.coffeeshop.entity.OrderItem();
                                item.setOrder(order);
                                item.setProduct(product);
                                item.setSnapshotProductName(product.getName());
                                item.setQuantity(1);
                                item.setSnapshotUnitPrice(BigDecimal.valueOf(45_000.0));
                                item.setSubTotal(BigDecimal.valueOf(45_000.0));
                                total += 45_000.0;
                                details.add(item);
                        }

                        order.setTotalAmount(total);
                        order.setGrandTotal(BigDecimal.valueOf(total));
                        order.setTrackingCode(String.format("ORD-%06d", ++orderCounter));
                        com.coffeeshop.entity.Order saved = orderRepository.save(order);
                        for (com.coffeeshop.entity.OrderItem item : details) {
                                item.setOrder(saved);
                                orderItemRepository.save(item);
                        }
                }

                log.info("Active orders seeded.");
        }

        private void seedJobs() {
                if (jobPostingRepository.count() != 0) {
                        return;
                }

                JobPosting job1 = new JobPosting();
                job1.setTitle("Lead Barista");
                job1.setLocation("District 1, HCMC");
                job1.setType(com.coffeeshop.entity.JobType.FULL_TIME);
                job1.setDescription(
                                "Seeking a passionate coffee expert to lead our morning shift. You will be responsible for quality control and training junior staff.");
                job1.setRequirements("3+ years experience in specialty coffee, Latte Art mastery, leadership skills.");
                job1.setActive(true);

                JobPosting job2 = new JobPosting();
                job2.setTitle("Store Supervisor");
                job2.setLocation("District 3, HCMC");
                job2.setType(com.coffeeshop.entity.JobType.FULL_TIME);
                job2.setDescription(
                                "Oversee daily operations, manage inventory, and ensure the highest level of customer satisfaction.");
                job2.setRequirements("Management experience in F&B, strong communication, problem-solving skills.");
                job2.setActive(true);

                JobPosting job3 = new JobPosting();
                job3.setTitle("Customer Experience Specialist");
                job3.setLocation("All Branches");
                job3.setType(com.coffeeshop.entity.JobType.PART_TIME);
                job3.setDescription(
                                "Create a welcoming atmosphere for our guests. Responsible for greeting, serving, and handling customer feedback.");
                job3.setRequirements("Friendly personality, energetic, good English communication is a plus.");
                job3.setActive(true);

                jobPostingRepository.save(job1);
                jobPostingRepository.save(job2);
                jobPostingRepository.save(job3);
                log.info("Premium job postings seeded successfully.");
        }

        private void seedProducts() {
                com.coffeeshop.entity.Category coffee = createDetailsCategory("Coffee", "Premium beans from highlands");
                com.coffeeshop.entity.Category tea = createDetailsCategory("Tea", "Organic tea leaves");
                com.coffeeshop.entity.Category smoothie = createDetailsCategory("Smoothie", "Milk blended drinks");
                com.coffeeshop.entity.Category juice = createDetailsCategory("Juice", "Fresh pressed fruits");

                createProduct("Cafe Latte", "Creamy espresso with steamed milk.",
                                "milk,espresso,coffee,latte,sweet,hot", "/images/products/CaffeLatte.png", coffee,
                                55000.0);
                createProduct("Espresso", "Intense double-shot espresso.",
                                "espresso,coffee,strong,bitter,black,bold,shot,hot", "/images/products/Espresso.png",
                                coffee, 45000.0);
                createProduct("Peach Tea", "Refreshing peach tea with fresh peach slices.",
                                "tea,peach,fruit,refreshing,iced,cold,sweet", "/images/products/PeachTea.png", tea,
                                55000.0);
                createProduct("Sakura Blossom Tea", "Delicate cherry blossom infused tea.",
                                "tea,sakura,cherry blossom,floral,light,elegant,hot",
                                "/images/products/SakuraBlossomTea.png", tea, 58000.0);
                createProduct("Strawberry Smoothie", "Thick strawberry smoothie blended with fresh milk.",
                                "smoothie,strawberry,milk,cream,sweet,cold,ice,fruit",
                                "/images/products/StrawberrySmoothie.png", smoothie, 60000.0);
                createProduct("Coconut Juice", "Fresh young coconut water with coconut jelly.",
                                "coconut,juice,fresh,natural,cold,ice,healthy", "/images/products/CoconutJuice.png",
                                juice, 50000.0);
                log.info("Products seeded.");
        }

        private com.coffeeshop.entity.Category createDetailsCategory(String name, String description) {
                com.coffeeshop.entity.Category category = categoryRepository.findByName(name)
                                .orElse(new com.coffeeshop.entity.Category());
                category.setName(name);
                category.setDescription(description);
                category.setCategoryCode(String.format("CAT-%05d", ++categoryCounter));
                return categoryRepository.save(category);
        }

        private void createProduct(String name, String description,
                        String tags, String imageUrl, com.coffeeshop.entity.Category category, Double basePrice) {
                com.coffeeshop.entity.Product product = productRepository.findAll().stream()
                                .filter(existingProduct -> existingProduct.getName().equalsIgnoreCase(name))
                                .findFirst()
                                .orElse(new com.coffeeshop.entity.Product());
                product.setName(name);
                product.setDescription(description);
                product.setTags(tags);
                product.setImage(imageUrl);
                product.setCategory(category);
                product.setProductCode(String.format("PRD-%05d", ++productCounter));
                product.setAvailable(true);
                product.setBasePrice(BigDecimal.valueOf(basePrice));
                com.coffeeshop.entity.Product savedProduct = productRepository.save(product);

                if (productSizeRepository.findByProductId(savedProduct.getId()).isEmpty()) {
                        com.coffeeshop.entity.ProductSize size = new com.coffeeshop.entity.ProductSize(
                                        "Standard", basePrice, savedProduct);
                        productSizeRepository.save(size);
                }
        }
}
