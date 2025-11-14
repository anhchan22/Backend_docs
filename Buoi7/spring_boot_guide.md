# Hướng dẫn Spring Boot Chi Tiết - Dành cho Người Mới Bắt Đầu

## I. Triển khai RESTful API trong Spring Boot

### Khái niệm cơ bản

**REST (Representational State Transfer)** là một kiến trúc phần mềm dùng để thiết kế các dịch vụ web. **RESTful API** trong Spring Boot giúp bạn xây dựng các endpoint để nhận yêu cầu từ client và trả về dữ liệu (thường là JSON).

### Các bước triển khai

#### Bước 1: Tạo Spring Boot Project
- Sử dụng Spring Initializr
- Chọn dependencies: **Spring Web** và **Spring Data JPA**
- Language: **Java**, Packaging: **Jar**, Java version: **17+**

#### Bước 2: Tạo Entity (lớp đại diện cho bảng cơ sở dữ liệu)
```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
}
```

#### Bước 3: Tạo Repository (giao tiếp với database)
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
```

#### Bước 4: Tạo Service (xử lý business logic)
```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
```

#### Bước 5: Tạo RestController (tiếp nhận request từ client)
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;
    
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }
    
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userRepository.findById(id);
    }
    
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        return userService.saveUser(user);
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
```

---

## II. JPA là gì? Hibernate là gì?

### JPA (Java Persistence API)

**JPA là một đặc tả (specification)** - tức là một bộ quy tắc, giao diện, và hướng dẫn để các nhà phát triển làm theo. JPA không phải là một công cụ thực tế mà là một tiêu chuẩn.

**Vai trò của JPA:**
- Cung cấp các giao diện (interfaces) và chú thích (annotations)
- Cho phép lập trình viên tương tác với cơ sở dữ liệu mà không cần viết SQL
- Ánh xạ các đối tượng Java (Objects) thành các bảng trong cơ sở dữ liệu

### Hibernate

**Hibernate là một công cụ ORM (Object-Relational Mapping)** - tức là một phần mềm thực tế thực hiện JPA specification.

**Tính năng của Hibernate:**
- Triển khai tất cả các quy tắc của JPA
- Cung cấp các tính năng bổ sung ngoài JPA (caching, custom SQL, v.v.)
- Hỗ trợ nhiều loại cơ sở dữ liệu
- Quản lý các mối quan hệ phức tạp giữa các bảng

### Mối quan hệ giữa JPA và Hibernate

```
JPA (Specification - Quy tắc)
    ↓
Hibernate (Implementation - Công cụ thực thi)
```

**Tương tự như:**
- JPA ≈ JavaScript specification (ES6)
- Hibernate ≈ JavaScript engine (V8, SpiderMonkey)

### So sánh chi tiết

| Tiêu chí | JPA | Hibernate |
|----------|-----|-----------|
| **Loại** | Specification (quy tắc) | Implementation (công cụ) |
| **Gói** | javax.persistence | org.hibernate |
| **Tính năng** | Cơ bản | Nhiều, nâng cao |
| **Query Language** | JPQL | HQL (Hibernate Query Language) |
| **Chuyển đổi** | Dễ chuyển sang ORM khác | Khó (vendor lock-in) |

---

## III. Kiến trúc 4 lớp Spring Boot

Spring Boot chia ứng dụng thành 4 lớp chính, mỗi lớp có nhiệm vụ riêng biệt:

### 1. Presentation Layer (Lớp Trình Bày)
**Vị trí:** Đầu tiên, tiếp nhận request từ client

**Nhiệm vụ:**
- Nhận HTTP request từ client (GET, POST, PUT, DELETE)
- Chuyển đổi dữ liệu JSON thành Java objects
- Xác thực người dùng
- Gọi Service Layer để xử lý logic
- Trả về HTTP response dưới dạng JSON

**Công cụ:**
- `@RestController` - chứa các endpoint API
- `@RequestMapping`, `@GetMapping`, `@PostMapping`, v.v. - định nghĩa route
- `@RequestBody` - nhận dữ liệu từ request

**Ví dụ:**
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        // Request từ client vào đây
        return service.save(product);
    }
}
```

### 2. Business Layer / Service Layer (Lớp Xử Lý Logic)
**Vị trí:** Ở giữa, nhận dữ liệu từ Controller

**Nhiệm vụ:**
- Kiểm tra dữ liệu (validation)
- Thực hiện xử lý kinh doanh (business logic)
- Kiểm tra quyền hạn (authorization)
- Quản lý transactions (giao dịch) - @Transactional
- Gọi Repository Layer để lấy/lưu dữ liệu

**Công cụ:**
- `@Service` - đánh dấu lớp này là Service
- `@Transactional` - quản lý giao dịch cơ sở dữ liệu
- `@Autowired` - inject dependency

**Ví dụ:**
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    
    @Transactional
    public Product createProduct(Product product) {
        // Validate dữ liệu
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Giá không hợp lệ");
        }
        // Lưu vào database
        return repository.save(product);
    }
}
```

### 3. Persistence Layer / Data Access Layer (Lớp Truy Cập Dữ Liệu)
**Vị trị:** Tiếp theo, nhận yêu cầu từ Service

**Nhiệm vụ:**
- Tương tác với cơ sở dữ liệu
- Thực hiện các phép toán CRUD (Create, Read, Update, Delete)
- Ánh xạ Java objects thành database records (qua JPA/Hibernate)
- Thực hiện các custom queries

**Công cụ:**
- `@Repository` - đánh dấu lớp này là Repository
- `JpaRepository<Entity, ID>` - interface cung cấp các phương thức sẵn có
- `@Query` - định nghĩa custom queries

**Ví dụ:**
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category = ?1")
    List<Product> findByCategory(String category);
}
```

### 4. Database Layer (Lớp Cơ Sở Dữ Liệu)
**Vị trí:** Cuối cùng, lưu trữ dữ liệu thực tế

**Nhiệm vụ:**
- Lưu trữ dữ liệu vật lý (MySQL, PostgreSQL, MongoDB, v.v.)
- Đảm bảo tính toàn vẹn dữ liệu
- Thực hiện indexing, query optimization

**Ví dụ:**
```sql
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2)
);
```

### Dòng chảy dữ liệu qua 4 lớp

```
Client Request
    ↓
Presentation Layer (Controller) - Nhận request, convert JSON
    ↓
Business Layer (Service) - Xử lý logic, kiểm tra
    ↓
Persistence Layer (Repository) - Gọi database query
    ↓
Database Layer - Lấy/lưu dữ liệu
    ↓
Persistence Layer - Trả kết quả
    ↓
Business Layer - Xử lý kết quả
    ↓
Presentation Layer - Convert thành JSON
    ↓
Client Response
```

---

## IV. Request Flow (Dòng Chảy Xử Lý Request)

Khi một client gửi request đến Spring Boot API, có một quá trình dài diễn ra phía sau. Dưới đây là từng bước chi tiết:

### Bước 1: Client Gửi Request
```
GET http://localhost:8080/api/users/1
```

### Bước 2: Embedded Server Nhận Request
- Spring Boot chạy với embedded Tomcat server (mặc định)
- Tomcat lắng nghe trên port 8080
- Tomcat nhận HTTP request và chuyển cho DispatcherServlet

### Bước 3: DispatcherServlet (Front Controller)
- **DispatcherServlet** là "điểm đầu vào" của tất cả requests
- Nó là một servlet đặc biệt được Spring Boot tự động cấu hình
- Nó phân loại request và gửi tới Handler thích hợp

**Log sẽ hiển thị:**
```
Mapping servlet: 'dispatcherServlet' to [/]
```

### Bước 4: Filter Chain
- Request đi qua một loạt filters (DelegatingFilterProxy)
- Ví dụ: HTTP Firewall kiểm tra bảo mật
- Các filters này có thể chặn hoặc chỉnh sửa request

### Bước 5: Handler Mapping
- DispatcherServlet tìm kiếm handler (controller method) phù hợp
- Dựa vào URL pattern và HTTP method
- Ví dụ: `@GetMapping("/users/{id}")` khớp với `GET /api/users/1`

### Bước 6: Handler Interceptors (Trước khi xử lý)
- Thực thi các interceptor `preHandle()` method
- Có thể kiểm tra authentication, logging
- Có thể chặn request nếu cần

### Bước 7: Vào Controller
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        // Request đến đây
        return service.getUserById(id);
    }
}
```

### Bước 8: Controller Gọi Service
```java
@Service
public class UserService {
    @Transactional
    public User getUserById(Long id) {
        // @Transactional tự động mở transaction
        return repository.findById(id).orElseThrow(...);
    }
}
```

### Bước 9: Service Gọi Repository
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Repository tự động thực hiện query
}
```

### Bước 10: Hibernate/JPA Tương Tác Database
- Hibernate nhận command từ Repository
- Chuyển đổi thành SQL native
- Gửi query tới database (MySQL, PostgreSQL, v.v.)

```sql
SELECT * FROM users WHERE id = 1;
```

### Bước 11: Database Trả Kết Quả
- Database thực hiện query
- Trả về kết quả cho Hibernate

### Bước 12: Mapping Kết Quả
- Hibernate chuyển kết quả (rows) thành Java objects
- Ví dụ: Row từ table `users` → `User` object

### Bước 13: Quay Lại Service
```java
// Service nhận User object từ Repository
User user = repository.findById(id).orElseThrow();
// @Transactional tự động commit transaction
return user;
```

### Bước 14: Quay Lại Controller
```java
// Controller nhận User object từ Service
return user; // Tự động convert thành JSON
```

### Bước 15: Handler Interceptors (Sau khi xử lý)
- Thực thi `postHandle()` method
- Có thể thêm attributes, logging

### Bước 16: View/Response Generation
- Vì dùng `@RestController`, Spring tự động convert object thành JSON
- Sử dụng Jackson library

```json
{
  "id": 1,
  "name": "John",
  "email": "john@example.com"
}
```

### Bước 17: Response Trở Lại Client
- HTTP response với status 200 OK
- Header: `Content-Type: application/json`
- Body: JSON data

### Bước 18: Filter Chain (Ngược chiều)
- Response đi ngược lại qua filter chain
- Filters có thể chỉnh sửa response

### Tổng Quát Dòng Chảy

```
REQUEST:
Client → Tomcat → DispatcherServlet → Filters → HandlerMapping 
→ Interceptors (pre) → Controller → Service → Repository 
→ Hibernate → Database

RESPONSE:
Database → Hibernate → Repository → Service → Controller 
→ Interceptors (post) → View Generation → Filters → Client
```

### Flow Diagram (Text)

```
┌─────────────┐
│   Client    │
└──────┬──────┘
       │ HTTP Request
       ▼
┌──────────────────┐
│  Embedded Server │
│   (Tomcat)       │
└──────┬───────────┘
       │
       ▼
┌──────────────────────┐
│ DispatcherServlet    │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│   Filter Chain       │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Handler Mapping      │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│ Handler Interceptors │
│ (preHandle)          │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│    Controller        │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│    Service           │
│ (@Transactional)     │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│   Repository/DAO     │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│  Hibernate/JPA       │
└──────┬───────────────┘
       │
       ▼
┌──────────────────────┐
│    Database          │
└──────┬───────────────┘
       │ (Return Data)
       ▼ [Ngược chiều trở lại...]
       
┌──────────────────────┐
│     Client           │ ◄── JSON Response
└──────────────────────┘
```

---

## V. Cấu Hình DataSource

**DataSource** là kết nối tới cơ sở dữ liệu. Nó chứa thông tin như URL, username, password.

### Cách 1: Cấu Hình Thông Qua application.properties

Tạo file `application.properties` trong thư mục `src/main/resources/`:

```properties
# DataSource Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/springboot_db
spring.datasource.username=root
spring.datasource.password=password123
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true
```

**Giải thích:**
- `spring.datasource.url` - Địa chỉ cơ sở dữ liệu (host, port, database name)
- `spring.datasource.username` - Tên người dùng MySQL
- `spring.datasource.password` - Mật khẩu MySQL
- `spring.datasource.driver-class-name` - Driver JDBC cho MySQL
- `spring.jpa.hibernate.ddl-auto=update` - Tự động cập nhật schema
  - `create` - Xóa table cũ, tạo mới mỗi lần chạy
  - `update` - Thêm/sửa columns nếu cần
  - `validate` - Chỉ kiểm tra
  - `none` - Không làm gì
- `spring.jpa.show-sql=true` - In ra SQL queries (dùng để debug)

### Cách 2: Cấu Hình Thông Qua application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/springboot_db
    username: root
    password: password123
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
        format_sql: true
```

### Thêm Dependencies trong pom.xml

```xml
<!-- Spring Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- MySQL Driver -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>
```

### Tạo Database Manually (Tuỳ chọn)

Nếu không muốn Hibernate tự động tạo table, bạn có thể tạo database trước:

```sql
CREATE DATABASE springboot_db;
USE springboot_db;

CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## VI. Các Annotation Quan Trọng

### 1. @RestController vs @Controller

#### @RestController
- Dùng cho **REST APIs** (trả JSON/XML)
- Kết hợp `@Controller + @ResponseBody`
- Tự động convert object thành JSON

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    @GetMapping("/{id}")
    public User getUser(@PathVariable Long id) {
        return new User(id, "John", "john@example.com");
        // Tự động convert thành JSON
    }
}
```

**Response:**
```json
{
  "id": 1,
  "name": "John",
  "email": "john@example.com"
}
```

#### @Controller
- Dùng cho **MVC apps** (trả HTML views)
- Cần thêm `@ResponseBody` để trả JSON

```java
@Controller
public class HomeController {
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("message", "Welcome");
        return "home"; // Trả về file home.html/home.jsp
    }
}
```

### 2. @Service
- Đánh dấu một lớp là **Service** (xử lý business logic)
- Spring tự động tạo bean cho class này
- Cho phép sử dụng `@Transactional`

```java
@Service
public class UserService {
    @Autowired
    private UserRepository repository;
    
    public User createUser(User user) {
        // Xử lý logic ở đây
        return repository.save(user);
    }
}
```

**Lợi ích:**
- Tách biệt business logic khỏi controller
- Dễ test (có thể mock service)
- Dễ tái sử dụng (service có thể dùng từ nhiều controller)

### 3. @Repository
- Đánh dấu một interface/class là **Data Access Object (DAO)**
- Cung cấp các phương thức để tương tác database
- Tự động dịch các database exceptions

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
```

**JpaRepository cung cấp:**
- `save(entity)` - Lưu/cập nhật
- `findById(id)` - Tìm theo ID
- `findAll()` - Lấy tất cả
- `delete(entity)` - Xóa
- `deleteById(id)` - Xóa theo ID

### 4. @Entity
- Đánh dấu một class là **Entity** (đại diện cho bảng database)
- Mỗi attribute là một column trong bảng

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private Long id;
    
    private String name;
    private String email;
}
```

### 5. @Table
- Định nghĩa tên bảng database
- Nếu không dùng `@Table`, tên bảng = tên class

```java
@Entity
@Table(name = "users") // Bảng tên là "users" chứ không phải "User"
public class User {
    // ...
}
```

**Với constraints:**
```java
@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
public class User {
    // ...
}
```

### 6. @Id
- Đánh dấu **primary key** của entity
- Mỗi entity phải có một `@Id`

```java
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // ...
}
```

**GenerationType:**
- `IDENTITY` - Auto increment (MySQL)
- `SEQUENCE` - Dùng sequence (PostgreSQL, Oracle)
- `TABLE` - Dùng một bảng để quản lý
- `AUTO` - Spring tự chọn

### 7. @Column
- Định nghĩa chi tiết của column (tên, độ dài, nullable, v.v.)

```java
@Entity
public class User {
    @Id
    private Long id;
    
    @Column(name = "full_name", length = 100, nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(columnDefinition = "VARCHAR(500)")
    private String bio;
}
```

**Các tham số:**
- `name` - Tên column trong database
- `length` - Độ dài tối đa (varchar)
- `nullable` - Có cho phép NULL không
- `unique` - Giá trị phải duy nhất
- `columnDefinition` - Định nghĩa SQL tùy chỉnh

### 8. @Transactional
- Quản lý **transactions** (giao dịch) cơ sở dữ liệu
- Tự động commit nếu thành công, rollback nếu lỗi
- Theo nguyên tắc ACID (Atomicity, Consistency, Isolation, Durability)

```java
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Transactional
    public void transferMoney(User from, User to, double amount) {
        // Nếu có lỗi ở bất kỳ dòng nào, cả 2 operation sẽ rollback
        from.setBalance(from.getBalance() - amount);
        userRepository.save(from);
        
        to.setBalance(to.getBalance() + amount);
        userRepository.save(to);
        
        // Log transaction
        accountRepository.saveLog(from.getId(), to.getId(), amount);
    }
}
```

**Ví dụ về Rollback:**
```java
@Transactional
public void transfer() {
    accountA.debit(100);     // Success
    save(accountA);
    
    // Nếu exception ở đây:
    accountB.credit(100);    // Fail - sẽ throw exception
    save(accountB);
    
    // Cả 2 lệnh trên sẽ ROLLBACK (hoàn tác)
}
```

**Tham số thường dùng:**
```java
@Transactional(readOnly = true) // Chỉ đọc, không sửa
@Transactional(propagation = Propagation.REQUIRED) // Yêu cầu transaction
@Transactional(isolation = Isolation.READ_COMMITTED) // Isolation level
@Transactional(timeout = 30) // Timeout 30 giây
@Transactional(rollbackFor = {Exception.class}) // Rollback khi exception
```

### 9. @Query
- Viết **custom query** (query tùy chỉnh) thay vì dùng các phương thức sẵn có
- Hỗ trợ JPQL (Java Persistence Query Language) hoặc SQL native

#### JPQL Query
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // JPQL - dùng tên class, không phải tên bảng
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmail(String email);
    
    @Query("SELECT u FROM User u WHERE u.name LIKE %?1% AND u.status = ?2")
    List<User> searchUsers(String keyword, String status);
}
```

#### Native SQL Query
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Native SQL - dùng tên bảng thực tế
    @Query(value = "SELECT * FROM users WHERE email = ?1", nativeQuery = true)
    User findByEmail(String email);
}
```

#### Với Named Parameters
```java
@Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
List<User> findByEmailAndStatus(@Param("email") String email, 
                                 @Param("status") String status);

// Sử dụng:
repository.findByEmailAndStatus("john@example.com", "ACTIVE");
```

#### UPDATE/DELETE Query
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Query("UPDATE User u SET u.status = ?2 WHERE u.id = ?1")
    void updateStatus(Long id, String status);
    
    @Modifying
    @Query("DELETE FROM User u WHERE u.email = ?1")
    void deleteByEmail(String email);
}
```

**Lưu ý:** Cần thêm `@Modifying` cho UPDATE/DELETE queries

---

## Tổng Kết

| Annotation | Mục đích | Nơi dùng |
|------------|---------|----------|
| @RestController | REST API controller | Class controller |
| @Controller | MVC controller | Class controller |
| @Service | Business logic | Class service |
| @Repository | Data access | Interface repository |
| @Entity | Database entity | Class entity |
| @Table | Tên bảng database | Class entity |
| @Id | Primary key | Field entity |
| @Column | Chi tiết column | Field entity |
| @Transactional | Quản lý transaction | Method service |
| @Query | Custom query | Method repository |

---

## Ví Dụ Thực Tế Hoàn Chỉnh

### Entity
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private Double price;
    
    private String category;
    
    @Column(columnDefinition = "LONGTEXT")
    private String description;
}
```

### Repository
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("SELECT p FROM Product p WHERE p.category = ?1")
    List<Product> findByCategory(String category);
    
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN ?1 AND ?2")
    List<Product> findByPriceRange(Double minPrice, Double maxPrice);
}
```

### Service
```java
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Transactional
    public Product createProduct(Product product) {
        // Validate
        if (product.getPrice() < 0) {
            throw new IllegalArgumentException("Giá không hợp lệ");
        }
        return productRepository.save(product);
    }
    
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
}
```

### Controller
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }
    
    @GetMapping("/category/{category}")
    public List<Product> getByCategory(@PathVariable String category) {
        return productService.getProductsByCategory(category);
    }
}
```

---

**Tài liệu này cung cấp kiến thức cơ bản và dễ hiểu về Spring Boot cho người mới bắt đầu.**
