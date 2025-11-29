# [BUỔI 5] Tối ưu JPA, Validation và Exception Handling

## I. Các phương pháp tối ưu JPA

### 1. Lazy / Eager Initialization

**Lazy Initialization (FetchType.LAZY)** - Tải dữ liệu khi cần

Lazy initialization có nghĩa là các entity liên quan sẽ chỉ được tải từ database khi bạn truy cập chúng. Đây là chiến lược mặc định cho `@OneToMany` và `@ManyToMany`.

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Chỉ tải khi truy cập
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
}
```

**Lợi ích:**
- Giảm lượng dữ liệu được tải từ database
- Tăng hiệu suất khi không cần tất cả dữ liệu liên quan
- Tiết kiệm bộ nhớ với dữ liệu lớn

**Nhược điểm:**
- Có thể gây ra `LazyInitializationException` nếu truy cập dữ liệu bên ngoài transaction
- Cần phải xử lý cẩn thận để tránh N+1 query problem

---

**Eager Initialization (FetchType.EAGER)** - Tải dữ liệu cùng một lúc

Eager initialization có nghĩa là các entity liên quan sẽ được tải cùng với entity chính ngay khi lấy từ database. Đây là chiến lược mặc định cho `@OneToOne` và `@ManyToOne`.

```java
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    
    // Tải cùng với Employee
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "department_id")
    private Department department;
    
    // Tải cùng với Employee
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "office_id")
    private Office office;
}
```

**Lợi ích:**
- Tải tất cả dữ liệu cần thiết ngay từ đầu
- Tránh `LazyInitializationException`
- Đơn giản để sử dụng

**Nhược điểm:**
- Tải quá nhiều dữ liệu không cần thiết
- Giảm hiệu suất, đặc biệt với các bảng lớn
- Nó là **anti-pattern** trong hầu hết các trường hợp

---

**Best Practice:**
- **Sử dụng LAZY cho tất cả `@OneToMany` và `@ManyToMany`** (luôn luôn)
- **Sử dụng LAZY cho `@ManyToOne` và `@OneToOne`** (khi có thể)
- **EAGER chỉ khi 100% chắc chắn cần dữ liệu** (hiếm khi)

---

### 2. Pagination

Pagination chia dữ liệu thành các trang nhỏ để tránh tải toàn bộ dữ liệu cùng một lúc, cải thiện hiệu suất và trải nghiệm người dùng.

**Sử dụng Pagination với Spring Data JPA:**

```java
// Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    // Hỗ trợ pagination
    Page<Product> findAll(Pageable pageable);
    
    Page<Product> findByPrice(Double price, Pageable pageable);
}

// Controller
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductRepository productRepository;
    
    @GetMapping
    public ResponseEntity<Page<Product>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Tạo PageRequest (trang bắt đầu từ 0)
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productRepository.findAll(pageable);
        
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/sorted")
    public ResponseEntity<Page<Product>> getSortedProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        // Sắp xếp theo nhiều tiêu chí
        Sort sort = Sort.by("price").descending().and(Sort.by("name"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<Product> products = productRepository.findAll(pageable);
        
        return ResponseEntity.ok(products);
    }
}
```

**Các thông tin trả về từ Page Object:**
- `getContent()`: Danh sách dữ liệu hiện tại
- `getTotalElements()`: Tổng số phần tử
- `getTotalPages()`: Tổng số trang
- `getNumber()`: Trang hiện tại
- `getSize()`: Kích thước trang
- `hasNext()`: Có trang tiếp theo không
- `hasPrevious()`: Có trang trước đó không

---

### 3. Caching

Caching lưu trữ dữ liệu thường xuyên được truy cập để tránh truy cập database nhiều lần, cải thiện hiệu suất đáng kể.

**Second-Level Cache (L2 Cache) trong Hibernate:**

```java
// 1. Cấu hình trong application.properties hoặc application.yml
spring.jpa.properties.hibernate.cache.use_second_level_cache=true
spring.jpa.properties.hibernate.cache.use_query_cache=true
spring.jpa.properties.hibernate.cache.region.factory_class=org.hibernate.cache.jcache.JCacheRegionFactory

// 2. Entity được cache
@Entity
@Cacheable
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Category {
    @Id
    private Long id;
    
    private String name;
}

// 3. Sử dụng
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // Kết quả của query này sẽ được cache
    @Transactional(readOnly = true)
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
}
```

**Các Caching Strategy:**

1. **READ_ONLY**: Dữ liệu không bao giờ thay đổi, hiệu suất cao nhất
```java
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class Country {
    // ...
}
```

2. **READ_WRITE**: Dữ liệu có thể thay đổi, phù hợp nhất (mặc định)
```java
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User {
    // ...
}
```

3. **NONSTRICT_READ_WRITE**: Cho phép dữ liệu hơi lỗi thời, hiệu suất tốt
```java
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Log {
    // ...
}
```

4. **TRANSACTIONAL**: Yêu cầu cache hỗ trợ transaction
```java
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class CriticalData {
    // ...
}
```

**Caching Associations:**
```java
@Entity
public class Order {
    @Id
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
```

---

### 4. Manual / Native Query

Native query là các câu lệnh SQL thuần được viết trực tiếp, cho phép sử dụng toàn bộ các tính năng của database cụ thể.

**Khi nào dùng Native Query:**
- JPQL không đáp ứng yêu cầu
- Cần sử dụng các tính năng database cụ thể (FULL TEXT SEARCH, ...)
- Tối ưu hóa hiệu suất cho các query phức tạp

**Ví dụ Native Query:**

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    // Native SELECT query
    @Query(value = "SELECT * FROM products WHERE price < :maxPrice", nativeQuery = true)
    List<Product> getProductsWithMaxPrice(@Param("maxPrice") Double maxPrice);
    
    // Native query với LIKE
    @Query(value = "SELECT * FROM products WHERE name LIKE %:keyword%", nativeQuery = true)
    List<Product> searchByName(@Param("keyword") String keyword);
    
    // Full text search (MySQL specific)
    @Query(value = "SELECT * FROM products WHERE MATCH(name, description) AGAINST(?1 IN BOOLEAN MODE)", 
           nativeQuery = true)
    List<Product> fullTextSearch(String keyword);
    
    // Native UPDATE query
    @Query(value = "UPDATE products SET price = price + :amount", nativeQuery = true)
    @Modifying
    @Transactional
    void updatePrice(@Param("amount") Double amount);
    
    // Native DELETE query
    @Query(value = "DELETE FROM products WHERE price < :minPrice", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteExpired(@Param("minPrice") Double minPrice);
}
```

**Lưu ý khi dùng Native Query:**
- Phải dùng **tên table và column thực tế** (không phải entity name)
- Không thể sử dụng entity names trong câu lệnh SQL
- `@Modifying` bắt buộc cho UPDATE/DELETE
- `@Transactional` bắt buộc cho write operations

---

### 5. Dynamic Query

Dynamic query cho phép xây dựng câu lệnh truy vấn lập trình trong runtime, phù hợp cho các tìm kiếm với nhiều điều kiện tùy ý.

**Sử dụng Criteria API:**

```java
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    public List<Product> searchProducts(String name, Double minPrice, Double maxPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (name != null && !name.isEmpty()) {
            predicates.add(cb.like(root.get("name"), "%" + name + "%"));
        }
        
        if (minPrice != null) {
            predicates.add(cb.ge(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            predicates.add(cb.le(root.get("price"), maxPrice));
        }
        
        // AND tất cả các điều kiện
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(query).getResultList();
    }
    
    public List<Product> searchWithOr(String keyword, Boolean inStock) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = cb.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (keyword != null) {
            predicates.add(cb.like(root.get("name"), "%" + keyword + "%"));
        }
        
        if (inStock != null) {
            predicates.add(cb.equal(root.get("inStock"), inStock));
        }
        
        // OR tất cả các điều kiện
        query.where(cb.or(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(query).getResultList();
    }
}

// Repository
@Repository
public interface ProductRepository extends JpaRepository<Product, Integer>, 
                                         JpaSpecificationExecutor<Product> {
}

// Sử dụng Specification
@Service
public class ProductSearchService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public List<Product> search(ProductSearchCriteria criteria) {
        Specification<Product> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            if (criteria.getName() != null) {
                predicates.add(cb.like(root.get("name"), "%" + criteria.getName() + "%"));
            }
            
            if (criteria.getMinPrice() != null) {
                predicates.add(cb.ge(root.get("price"), criteria.getMinPrice()));
            }
            
            if (criteria.getMaxPrice() != null) {
                predicates.add(cb.le(root.get("price"), criteria.getMaxPrice()));
            }
            
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        
        return productRepository.findAll(spec);
    }
}
```

---

### 6. Phòng tránh N + 1 Query

**N+1 Query Problem** xảy ra khi:
- 1 query tải danh sách records
- N queries tải dữ liệu liên quan cho mỗi record

**Ví dụ vấn đề:**

```java
// BÀI TOÁN: Lấy tất cả người dùng và đơn hàng của họ

// CÁCH SAI (Gây N+1):
List<User> users = userRepository.findAll(); // 1 query
for (User user : users) {
    List<Order> orders = orderRepository.findByUserId(user.getId()); // N queries
}
// Tổng: 1 + N queries (nếu 100 users → 101 queries!)

// CÁCH ĐÚNG 1: Sử dụng JOIN FETCH
@Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders")
List<User> findAllWithOrders();
// Kết quả: 1 query tối ưu

// CÁCH ĐÚNG 2: Sử dụng Entity Graph
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "orders")
    List<User> findAll();
    
    @EntityGraph(attributePaths = {"orders", "profile"})
    Optional<User> findById(Long id);
}

// CÁCH ĐÚNG 3: Sử dụng Criteria với Fetch
CriteriaBuilder cb = entityManager.getCriteriaBuilder();
CriteriaQuery<User> query = cb.createQuery(User.class);
Root<User> root = query.from(User.class);
root.fetch("orders", JoinType.LEFT);
List<User> users = entityManager.createQuery(query).getResultList();
```

**So sánh các giải pháp:**

| Giải pháp | Ưu điểm | Nhược điểm |
|-----------|---------|-----------|
| **JOIN FETCH** | Tối ưu, rõ ràng, linh hoạt | Phải viết query tùy chỉnh |
| **Entity Graph** | Tái sử dụng, khai báo | Có thể gây DISTINCT issue |
| **Criteria API** | Động, logic phức tạp | Phức tạp để viết |
| **Lazy + Explicit** | Kiểm soát chính xác | Dễ quên, dễ gây N+1 |

---

## II. Tìm hiểu về Validation

Validation là quá trình kiểm tra xem dữ liệu đầu vào có hợp lệ trước khi xử lý. Spring Boot hỗ trợ Hibernate Validator (JSR 303/380).

### Các Annotation Validation Thường Dùng

**Validation Cơ Bản:**

```java
@Entity
public class User {
    
    @Id
    @GeneratedValue
    private Long id;
    
    // Không null
    @NotNull(message = "Tên không được null")
    private String name;
    
    // Không null và không rỗng
    @NotEmpty(message = "Email không được rỗng")
    private String email;
    
    // Không null và không blank (chỉ space)
    @NotBlank(message = "Địa chỉ không được để trống")
    private String address;
    
    // Độ dài string
    @Size(min = 6, max = 20, message = "Mật khẩu phải từ 6-20 ký tự")
    private String password;
    
    // Email hợp lệ
    @Email(message = "Email không hợp lệ")
    private String emailAddress;
    
    // Number trong range
    @Min(value = 18, message = "Tuổi tối thiểu 18")
    @Max(value = 100, message = "Tuổi tối đa 100")
    private Integer age;
    
    // Pattern regex
    @Pattern(regexp = "^[0-9]{10}$", message = "Số điện thoại phải 10 chữ số")
    private String phone;
    
    // Số dương
    @Positive(message = "Giá phải lớn hơn 0")
    private Double price;
    
    // Số không âm
    @PositiveOrZero(message = "Số lượng không được âm")
    private Integer quantity;
}
```

**Validation Nested Objects:**

```java
public class OrderDTO {
    
    @NotNull(message = "Order ID không được null")
    private Long orderId;
    
    // Validate nested object
    @Valid
    @NotNull(message = "Customer không được null")
    private CustomerDTO customer;
    
    @Valid
    @NotEmpty(message = "Phải có ít nhất 1 item")
    private List<OrderItemDTO> items;
}

public class CustomerDTO {
    
    @NotBlank(message = "Tên khách hàng không được trống")
    private String name;
    
    @Email(message = "Email không hợp lệ")
    private String email;
}
```

### Sử dụng Validation trong Controller

```java
@RestController
@RequestMapping("/api/users")
@Validated  // Bắt buộc có annotation này
public class UserController {
    
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO, 
                                       BindingResult bindingResult) {
        // Kiểm tra lỗi validation
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error -> 
                errors.put(error.getField(), error.getDefaultMessage())
            );
            return ResponseEntity.badRequest().body(errors);
        }
        
        // Xử lý dữ liệu hợp lệ
        return ResponseEntity.ok("User created successfully");
    }
    
    // Validation cho path variable
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable 
                                     @Min(1) Long userId) {
        return ResponseEntity.ok("User: " + userId);
    }
    
    // Validation cho request parameter
    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam 
                                    @NotBlank String keyword,
                                    @RequestParam 
                                    @Min(0) int page) {
        return ResponseEntity.ok("Search: " + keyword + ", page: " + page);
    }
}
```

### Global Exception Handler cho Validation

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // Xử lý validation error từ @RequestBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        
        return ResponseEntity.badRequest().body(errors);
    }
    
    // Xử lý validation error từ path variable/request param
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolation(
            ConstraintViolationException ex) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation ->
            errors.put(violation.getPropertyPath().toString(), 
                      violation.getMessage())
        );
        
        return ResponseEntity.badRequest().body(errors);
    }
}
```

### Custom Validation Annotation

```java
// Định nghĩa custom validation
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidAgeValidator.class)
public @interface ValidAge {
    String message() default "Tuổi không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// Validator class
public class ValidAgeValidator implements ConstraintValidator<ValidAge, Integer> {
    
    @Override
    public void initialize(ValidAge annotation) {
    }
    
    @Override
    public boolean isValid(Integer age, ConstraintValidatorContext context) {
        if (age == null) {
            return true; // Null được xử lý bởi @NotNull
        }
        return age >= 18 && age <= 100;
    }
}

// Sử dụng
@Entity
public class User {
    @ValidAge
    private Integer age;
}
```

---

## III. Tìm hiểu về Exception Handling

Exception Handling là cơ chế xử lý các lỗi/ngoại lệ trong ứng dụng một cách có kiểm soát.

### Custom Exception Classes

```java
// Base custom exception
public class CustomException extends RuntimeException {
    private int statusCode;
    
    public CustomException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }
    
    public int getStatusCode() {
        return statusCode;
    }
}

// Specific exceptions
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends CustomException {
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue),
              HttpStatus.NOT_FOUND.value());
    }
}

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends CustomException {
    public ValidationException(String message) {
        super(message, HttpStatus.BAD_REQUEST.value());
    }
}

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateResourceException extends CustomException {
    public DuplicateResourceException(String resource, String field) {
        super(String.format("%s with this %s already exists", resource, field),
              HttpStatus.CONFLICT.value());
    }
}
```

### Exception Handler với @ControllerAdvice

```java
// Error response class
public class ErrorResponse {
    private int statusCode;
    private String message;
    private LocalDateTime timestamp;
    private String path;
    
    public ErrorResponse(int statusCode, String message, String path) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
    
    // Getters and setters
}

// Global exception handler
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
        
        logger.error("Resource not found: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {
        
        logger.warn("Validation error: {}", ex.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.append(error.getField()).append(": ")
                  .append(error.getDefaultMessage()).append("; ")
        );
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            errors.toString(),
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    
    // Catch-all for any exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {
        
        logger.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );
        
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
```

### Exception Handler trong Controller

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<?> createUser(@Valid @RequestBody UserDTO userDTO) {
        try {
            User user = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (DuplicateResourceException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("User with email already exists");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("User not found");
        }
    }
    
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<?> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}
```

### Best Practices

1. **Tạo custom exceptions cụ thể** cho từng loại lỗi
2. **Sử dụng @ControllerAdvice** để xử lý exceptions toàn cục
3. **Log errors** để debug và monitoring
4. **Trả về response có cấu trúc** với status code phù hợp
5. **Validate input** tại tầng controller để tránh lỗi không cần thiết

---

## IV. Override Default Fetch Plan với Join Fetch / Entity Graph

### 1. JOIN FETCH

JOIN FETCH là cách tối ưu nhất để tránh N+1 query problem. Nó cho phép tải dữ liệu liên quan trong cùng một query.

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Tải User cùng với Orders
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.orders")
    List<User> findAllWithOrders();
    
    // Tải User cùng với Orders và cả PostComments
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.orders o " +
           "LEFT JOIN FETCH o.comments")
    List<User> findAllWithOrdersAndComments();
    
    // Tải User cùng với Orders, phân trang
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.orders ORDER BY u.id")
    List<User> findAllWithOrdersOrderById();
    
    // Có điều kiện WHERE
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.orders " +
           "WHERE u.status = :status")
    List<User> findActiveUsersWithOrders(@Param("status") String status);
}

// Service
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public List<UserDTO> getAllUsersWithOrders() {
        // 1 query duy nhất với JOIN, tránh N+1
        List<User> users = userRepository.findAllWithOrders();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }
}
```

**Lưu ý:**
- Cần `DISTINCT` khi JOIN với collection để tránh duplicate
- `LEFT JOIN FETCH` để không loại bỏ những record không có child
- Không thể phân trang trực tiếp khi dùng JOIN FETCH collection

---

### 2. Entity Graph

Entity Graph cung cấp cách khai báo các entity được tải cùng entity chính, tách biệt khỏi query logic.

**Khai báo Entity Graph:**

```java
@Entity
@NamedEntityGraph(name = "User.withOrders",
    attributeNodes = {
        @NamedAttributeNode(value = "orders")
    }
)
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}

// Nested attribute nodes
@Entity
@NamedEntityGraph(name = "User.withOrdersAndComments",
    attributeNodes = {
        @NamedAttributeNode(value = "orders", subgraph = "order-comments")
    },
    subgraphs = {
        @NamedSubgraph(name = "order-comments",
            attributeNodes = @NamedAttributeNode("comments"))
    }
)
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
}
```

**Sử dụng Entity Graph trong Repository:**

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Sử dụng entity graph trong findAll
    @EntityGraph(value = "User.withOrders")
    List<User> findAll();
    
    // Sử dụng entity graph với điều kiện
    @EntityGraph(value = "User.withOrders")
    Optional<User> findById(Long id);
    
    // Custom query với entity graph
    @EntityGraph(value = "User.withOrders")
    @Query("SELECT u FROM User u WHERE u.status = :status")
    List<User> findActiveUsers(@Param("status") String status);
    
    // Sử dụng programmatic entity graph
    @EntityGraph(attributePaths = {"orders", "profile"})
    List<User> findByAgeGreaterThan(Integer age);
}
```

**Sử dụng Programmatic Entity Graph:**

```java
@Service
public class UserService {
    
    @Autowired
    private EntityManager entityManager;
    
    public User getUserWithRelations(Long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get("id"), id));
        
        EntityGraph<?> graph = entityManager.getEntityGraph("User.withOrders");
        
        TypedQuery<User> typedQuery = entityManager.createQuery(query);
        typedQuery.setHint("jakarta.persistence.fetchgraph", graph);
        
        return typedQuery.getSingleResult();
    }
}
```

---

### 3. So sánh JOIN FETCH vs Entity Graph

| Tiêu chí | JOIN FETCH | Entity Graph |
|----------|-----------|--------------|
| **Syntax** | Trong JPQL query | Trong @EntityGraph |
| **Linh hoạt** | Cao (tùy chỉnh mỗi query) | Thấp (cố định) |
| **Tái sử dụng** | Thấp (phải viết lại query) | Cao (dùng lại dễ dàng) |
| **Complexity** | Cao cho nested relations | Cao với subgraph |
| **Performance** | Tối ưu | Tối ưu |
| **Best for** | Ad-hoc queries | Reusable fetching pattern |

---

## Nên dùng FetchType.EAGER hay FetchType.LAZY cho các mối quan hệ?

### Best Practices Chung

```
┌─────────────────────────────────────────┐
│        RECOMMENDATION CHEATSHEET         │
├─────────────────────────────────────────┤
│ @OneToMany   → Luôn dùng LAZY           │
│ @ManyToMany  → Luôn dùng LAZY           │
│ @ManyToOne   → Dùng LAZY nếu có thể     │
│ @OneToOne    → Dùng LAZY nếu có thể     │
└─────────────────────────────────────────┘
```

### Chi tiết từng trường hợp

**1. @OneToMany - Luôn LAZY**

```java
@Entity
public class Department {
    @Id
    private Long id;
    
    private String name;
    
    // LUÔN LAZY vì collection lớn, không cần tất cả lúc nào
    @OneToMany(mappedBy = "department", fetch = FetchType.LAZY)
    private List<Employee> employees;
}
```

**Lý do:** Collection có thể chứa hàng nghìn records, tải hết không hiệu quả

---

**2. @ManyToMany - Luôn LAZY**

```java
@Entity
public class Course {
    @Id
    private Long id;
    
    private String name;
    
    // LUÔN LAZY vì collection, không cần tất cả lúc nào
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "course_student",
               joinColumns = @JoinColumn(name = "course_id"),
               inverseJoinColumns = @JoinColumn(name = "student_id"))
    private List<Student> students;
}
```

**Lý do:** M-M relations thường khá lớn, tải hết gây OOM

---

**3. @ManyToOne - Nên LAZY**

```java
@Entity
public class Order {
    @Id
    private Long id;
    
    private String orderNumber;
    
    // LAZY vì không phải lúc nào cũng cần Customer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
}

// Nhưng trong trường hợp này có thể EAGER:
@Entity
public class OrderItem {
    @Id
    private Long id;
    
    // EAGER OK vì mỗi OrderItem luôn cần Order
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private Order order;
}
```

**Lý do:** Phụ thuộc vào use case. Nếu luôn cần, EAGER OK. Nếu không, LAZY tốt hơn.

---

**4. @OneToOne - Nên LAZY**

```java
@Entity
public class User {
    @Id
    private Long id;
    
    private String name;
    
    // LAZY nếu không phải lúc nào cũng cần profile
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

// Nhưng có thể EAGER nếu luôn cần:
@Entity
public class UserProfile {
    @Id
    private Long id;
    
    // EAGER OK vì profile luôn cần address
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;
}
```

**Lý do:** @OneToOne với child có thể EAGER, nhưng @OneToOne với parent phải LAZY

---

### Chiến lược tối ưu hoàn hảo

```java
// ENTITY LAYER: Tất cả LAZY
@Entity
public class User {
    @Id
    private Long id;
    
    private String name;
    
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private UserProfile profile;
}

// SERVICE LAYER: Fetch ý như cần
@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    // Chỉ lấy User cơ bản
    public User getBasicUser(Long id) {
        return userRepository.findById(id).orElseThrow();
    }
    
    // Lấy User cùng Orders
    public User getUserWithOrders(Long id) {
        return userRepository.findByIdWithOrders(id);
    }
    
    // Lấy User cùng tất cả relations
    public User getFullUser(Long id) {
        return userRepository.findByIdFull(id);
    }
}

// REPOSITORY LAYER: Tối ưu fetch
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    @EntityGraph(attributePaths = "orders")
    Optional<User> findByIdWithOrders(Long id);
    
    @EntityGraph(attributePaths = {"orders", "company", "profile"})
    Optional<User> findByIdFull(Long id);
    
    @Query("SELECT DISTINCT u FROM User u " +
           "LEFT JOIN FETCH u.orders " +
           "WHERE u.id = :id")
    Optional<User> findByIdWithOrdersJoinFetch(@Param("id") Long id);
}
```

### Cảnh báo: Vấn đề N+1 khi dùng LAZY

```java
// NGUY HIỂM: Gây N+1 queries
List<User> users = userRepository.findAll(); // 1 query
for (User user : users) {
    System.out.println(user.getCompany().getName()); // N queries!
}

// AN TOÀN: Dùng Join Fetch
@Query("SELECT u FROM User u LEFT JOIN FETCH u.company")
List<User> findAllWithCompany();

// AN TOÀN: Dùng Entity Graph
@EntityGraph(attributePaths = "company")
List<User> findAll();

// AN TOÀN: Trong transaction
@Transactional(readOnly = true)
public List<User> getUsersWithCompany() {
    return userRepository.findAll(); // Dữ liệu LAZY vẫn load được trong transaction
}
```

---

## Tóm tắt quan trọng

1. **Lazy loading là default**: Giảm lượng dữ liệu tải không cần thiết
2. **Join Fetch tốt nhất**: Giải quyết N+1 một cách hiệu quả
3. **Entity Graph tái sử dụng**: Khi pattern fetch giống nhau
4. **Đừng dùng Eager**: Anti-pattern trong hầu hết trường hợp
5. **Validation đơn giản**: Sử dụng annotations, handle centrally
6. **Exception handling toàn cục**: Dùng @ControllerAdvice
7. **Caching khi cần**: Cho dữ liệu read-only hoặc ít thay đổi
