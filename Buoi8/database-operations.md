# KHÓA SPRING BOOT CƠ BẢN - BUỔI 4: THAO TÁC VỚI DATABASE

## Mục Lục
1. Các mối quan hệ (1..N, N..N) trong JPA
2. Derived Query Methods
3. JPQL với @Query
4. Native Query (SQL thuần)
5. Truy vấn sử dụng EntityManager

---

## 1. Các Mối Quan Hệ Trong JPA

### 1.1 @ManyToOne và @OneToMany (Quan hệ 1-N)

**Khái niệm:** 
- Một **Author** (Tác giả) có thể viết nhiều **Book** (Sách)
- Một **Book** chỉ thuộc về một **Author**
- Đây là quan hệ 1-N (One-to-Many)

**Cách triển khai:**

#### Bước 1: Tạo Entity Author (phía "1")
```java
@Entity
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String email;
    
    // @OneToMany: Một Author có nhiều Books
    // mappedBy = "author" -> chỉ rõ bên Book có field tên là "author"
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Book> books = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Book> getBooks() { return books; }
    public void setBooks(List<Book> books) { this.books = books; }
}
```

**Giải thích:**
- `@OneToMany(mappedBy = "author")` - "author" là tên field trong class Book
- `cascade = CascadeType.ALL` - Khi Author bị xóa, tất cả Books của nó cũng bị xóa
- `orphanRemoval = true` - Xóa Book khi không còn Author

#### Bước 2: Tạo Entity Book (phía "N")
```java
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    // @ManyToOne: Nhiều Books thuộc về một Author
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public Author getAuthor() { return author; }
    public void setAuthor(Author author) { this.author = author; }
}
```

**Giải thích:**
- `@ManyToOne` - Nhiều Books có thể thuộc về một Author
- `@JoinColumn(name = "author_id")` - Tạo cột "author_id" trong bảng "books" để lưu ID của Author
- `fetch = FetchType.LAZY` - Không tải Author cho tới khi cần thiết (tối ưu hiệu suất)

#### Cấu trúc Database sẽ tạo:
```sql
-- Bảng tác giả
CREATE TABLE authors (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL
);

-- Bảng sách (có khóa ngoại đến authors)
CREATE TABLE books (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    author_id BIGINT NOT NULL,
    FOREIGN KEY (author_id) REFERENCES authors(id)
);
```

#### Cách sử dụng:
```java
@Service
public class BookService {
    @Autowired
    private AuthorRepository authorRepository;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Transactional
    public void addBookToAuthor() {
        // Tìm tác giả
        Author author = authorRepository.findById(1L).orElse(null);
        
        // Tạo sách mới
        Book book = new Book();
        book.setTitle("Java Programming");
        book.setAuthor(author);
        
        // Lưu vào database
        bookRepository.save(book);
        
        // Hoặc: thêm vào danh sách books của author
        author.getBooks().add(book);
        authorRepository.save(author);
    }
}
```

---

### 1.2 @ManyToMany (Quan hệ N-N)

**Khái niệm:**
- Một **Student** (Sinh viên) học nhiều **Course** (Khóa học)
- Một **Course** có nhiều **Student**
- Đây là quan hệ N-N (Many-to-Many)
- Spring Boot tự động tạo **bảng trung gian** để quản lý quan hệ này

**Cách triển khai:**

#### Bước 1: Tạo Entity Student
```java
@Entity
@Table(name = "students")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    private String email;
    
    // @ManyToMany: Một Student học nhiều Courses
    @ManyToMany
    @JoinTable(
        name = "student_course",  // Bảng trung gian
        joinColumns = @JoinColumn(name = "student_id"),  // Khóa ngoại tới Student
        inverseJoinColumns = @JoinColumn(name = "course_id")  // Khóa ngoại tới Course
    )
    private List<Course> courses = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getName() { return name; }
    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
```

#### Bước 2: Tạo Entity Course
```java
@Entity
@Table(name = "courses")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    // @ManyToMany: Một Course có nhiều Students
    // mappedBy = "courses" -> chỉ rõ bên Student có field tên là "courses"
    @ManyToMany(mappedBy = "courses")
    private List<Student> students = new ArrayList<>();
    
    // Getters and Setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}
```

#### Cấu trúc Database sẽ tạo:
```sql
-- Bảng sinh viên
CREATE TABLE students (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255)
);

-- Bảng khóa học
CREATE TABLE courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT
);

-- Bảng trung gian (tự động tạo)
CREATE TABLE student_course (
    student_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    PRIMARY KEY (student_id, course_id),
    FOREIGN KEY (student_id) REFERENCES students(id),
    FOREIGN KEY (course_id) REFERENCES courses(id)
);
```

#### Cách sử dụng:
```java
@Service
public class CourseService {
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private CourseRepository courseRepository;
    
    @Transactional
    public void enrollStudentInCourse() {
        // Tìm sinh viên
        Student student = studentRepository.findById(1L).orElse(null);
        
        // Tìm khóa học
        Course course = courseRepository.findById(1L).orElse(null);
        
        // Thêm sinh viên vào khóa học
        student.getCourses().add(course);
        studentRepository.save(student);
    }
}
```

---

### 1.3 Bảng So Sánh Các Quan Hệ

| Loại Quan Hệ | Ký Hiệu | Ví Dụ | Annotation |
|---|---|---|---|
| One-to-One | 1:1 | Một người có một chứng minh thư | @OneToOne |
| One-to-Many | 1:N | Một tác giả viết nhiều sách | @OneToMany + @ManyToOne |
| Many-to-One | N:1 | Nhiều sách của một tác giả | @ManyToOne |
| Many-to-Many | N:N | Nhiều sinh viên học nhiều khóa học | @ManyToMany |

---

## 2. Derived Query Methods (Truy Vấn Theo Tên Hàm)

**Khái niệm:** 
- Spring Data JPA có thể **tự động tạo truy vấn** dựa trên **tên phương thức** bạn định nghĩa
- Bạn không cần viết SQL hay JPQL
- Rất tiện khi truy vấn đơn giản

### 2.1 Cú Pháp Cơ Bản

```
find/read/query/get/count + By + [Điều kiện]
```

**Ví dụ:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Tìm user theo email
    User findByEmail(String email);
    
    // Tìm tất cả users theo status
    List<User> findByStatus(String status);
    
    // Tìm users có tuổi lớn hơn 18
    List<User> findByAgeGreaterThan(int age);
    
    // Đếm số users có email chứa "gmail"
    long countByEmailContaining(String email);
}
```

### 2.2 Các Từ Khóa Điều Kiện

#### Điều Kiện Bằng (Equality)
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Tìm theo tên (chính xác)
    Product findByName(String name);
    
    // Tương tự
    Product findByNameIs(String name);
    
    // Kiểm tra NULL
    List<Product> findByDescriptionIsNull();
    
    // Kiểm tra NOT NULL
    List<Product> findByDescriptionIsNotNull();
}
```

#### Điều Kiện So Sánh (Comparison)
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Lớn hơn
    List<Order> findByPriceGreaterThan(Double price);
    
    // Lớn hơn hoặc bằng
    List<Order> findByPriceGreaterThanEqual(Double price);
    
    // Nhỏ hơn
    List<Order> findByPriceLessThan(Double price);
    
    // Nhỏ hơn hoặc bằng
    List<Order> findByPriceLessThanEqual(Double price);
    
    // Giữa hai giá trị
    List<Order> findByPriceBetween(Double min, Double max);
}
```

#### Điều Kiện Chứa (Like)
```java
@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
    // Chứa (% ở cả hai phía)
    List<Article> findByTitleContaining(String keyword);
    
    // Bắt đầu bằng
    List<Article> findByTitleStartingWith(String prefix);
    
    // Kết thúc bằng
    List<Article> findByTitleEndingWith(String suffix);
    
    // Không nhạy cảm HOAS/thường
    List<Article> findByTitleContainingIgnoreCase(String keyword);
}
```

#### Điều Kiện In (Trong danh sách)
```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Trong danh sách
    List<Comment> findByStatusIn(List<String> statuses);
    
    // Không trong danh sách
    List<Comment> findByStatusNotIn(List<String> statuses);
}
```

#### Kết Hợp Điều Kiện (And/Or)
```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // AND: Tìm sách của tác giả X và có giá > 100
    List<Book> findByAuthorNameAndPriceGreaterThan(String authorName, Double price);
    
    // OR: Tìm sách có tiêu đề chứa "Java" HOẶC "Python"
    List<Book> findByTitleContainingOrTitleContaining(String title1, String title2);
}
```

### 2.3 Sắp Xếp (Ordering)
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Sắp xếp tăng dần (mặc định)
    List<Student> findByStatusOrderByNameAsc(String status);
    
    // Sắp xếp giảm dần
    List<Student> findByStatusOrderByAgeDesc(String status);
    
    // Sắp xếp theo nhiều cột
    List<Student> findByStatusOrderByNameAscAgeDesc(String status);
}
```

### 2.4 Phân Trang (Pagination)
```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Trả về Page thay vì List
    Page<Product> findByCategory(String category, Pageable pageable);
}

// Sử dụng:
@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;
    
    public Page<Product> getProducts(int page, int size) {
        // Trang 0 (trang đầu tiên), mỗi trang 10 sản phẩm, sắp xếp theo name tăng dần
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return repository.findByCategory("Electronics", pageable);
    }
}
```

---

## 3. JPQL Với @Query

**JPQL (Java Persistence Query Language)**
- Là ngôn ngữ truy vấn hướng **đối tượng** (Object-Oriented)
- Dùng **tên class và field**, không phải bảng và cột
- Dùng khi truy vấn phức tạp không thể viết bằng Derived Query

### 3.1 JPQL Cơ Bản

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Truy vấn đơn giản
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    User findByEmailAddress(String email);
}
```

**Giải thích:**
- `SELECT u` - Chọn đối tượng User
- `FROM User u` - Từ bảng User, gọi tắt là "u"
- `WHERE u.email = ?1` - Điều kiện: email bằng tham số thứ 1

### 3.2 Sử Dụng Named Parameters (Tham Số Có Tên)

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // Named parameters dễ đọc hơn positional parameters
    @Query("SELECT s FROM Student s WHERE s.email = :email AND s.status = :status")
    List<Student> findByEmailAndStatus(
        @Param("email") String email,
        @Param("status") String status
    );
}

// Sử dụng:
List<Student> students = repository.findByEmailAndStatus("john@example.com", "ACTIVE");
```

### 3.3 JPQL Phức Tạp Với JOIN

```java
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    // INNER JOIN: Tìm tất cả sách của tác giả có tên "John"
    @Query("SELECT b FROM Book b " +
           "INNER JOIN b.author a " +
           "WHERE a.name = :authorName")
    List<Book> findBooksByAuthorName(@Param("authorName") String authorName);
    
    // LEFT JOIN: Tìm tất cả tác giả và số sách (kể cả tác giả chưa có sách)
    @Query("SELECT DISTINCT a FROM Author a " +
           "LEFT JOIN a.books b " +
           "WHERE b IS NOT NULL")
    List<Author> findAuthorsWithBooks();
}
```

### 3.4 JPQL Với Aggregation (Hàm Tổng Hợp)

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // COUNT: Đếm số đơn hàng
    @Query("SELECT COUNT(o) FROM Order o WHERE o.status = :status")
    long countByStatus(@Param("status") String status);
    
    // SUM: Tính tổng giá trị đơn hàng
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.customerId = :customerId")
    Double getTotalSpentByCustomer(@Param("customerId") Long customerId);
    
    // AVG: Tính giá trị trung bình
    @Query("SELECT AVG(o.totalPrice) FROM Order o")
    Double getAverageOrderPrice();
    
    // MIN/MAX: Tìm giá trị nhỏ nhất/lớn nhất
    @Query("SELECT MIN(o.totalPrice) FROM Order o")
    Double getMinOrderPrice();
}
```

### 3.5 JPQL Với GROUP BY

```java
@Repository
public interface SalesRepository extends JpaRepository<Sale, Long> {
    // GROUP BY: Tính doanh số theo sản phẩm
    @Query("SELECT p.name, SUM(s.amount) FROM Sale s " +
           "JOIN s.product p " +
           "GROUP BY p.name " +
           "ORDER BY SUM(s.amount) DESC")
    List<Object[]> getSalesReportByProduct();
}

// Sử dụng:
List<Object[]> report = repository.getSalesReportByProduct();
for (Object[] row : report) {
    String productName = (String) row[0];
    Double totalSales = (Double) row[1];
    System.out.println(productName + ": " + totalSales);
}
```

### 3.6 JPQL Với UPDATE

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :newStatus WHERE u.id = :userId")
    void updateUserStatus(
        @Param("userId") Long userId,
        @Param("newStatus") String newStatus
    );
}
```

**Lưu ý:** Cần `@Modifying` và `@Transactional` cho UPDATE/DELETE

### 3.7 JPQL Với DELETE

```java
@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.postId = :postId")
    void deleteCommentsByPostId(@Param("postId") Long postId);
}
```

---

## 4. Native Query (SQL Thuần)

**Khi nào dùng Native Query?**
- JPQL không đủ linh hoạt
- Muốn tận dụng tính năng riêng của cơ sở dữ liệu (MySQL, PostgreSQL, v.v.)
- Truy vấn rất phức tạp

### 4.1 Native Query Cơ Bản

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // nativeQuery = true cho biết đây là SQL thuần
    @Query(value = "SELECT * FROM products WHERE category_id = ?1", 
           nativeQuery = true)
    List<Product> findByCategoryId(Long categoryId);
}
```

**Giải thích:**
- `nativeQuery = true` - Bảo Spring đây là SQL native, không phải JPQL
- Dùng tên bảng `products` và cột `category_id` thực tế trong DB

### 4.2 Native Query Với Named Parameters

```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT * FROM users " +
                   "WHERE email = :email AND status = :status", 
           nativeQuery = true)
    List<User> findByEmailAndStatus(
        @Param("email") String email,
        @Param("status") String status
    );
}
```

### 4.3 Native Query Phức Tạp

```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Truy vấn JOIN với bảng khác
    @Query(value = "SELECT o.*, c.name as customer_name " +
                   "FROM orders o " +
                   "INNER JOIN customers c ON o.customer_id = c.id " +
                   "WHERE o.order_date BETWEEN :startDate AND :endDate", 
           nativeQuery = true)
    List<Order> findOrdersBetweenDates(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
```

### 4.4 Native Query Trả Về Custom Object

```java
// DTO (Data Transfer Object) để ánh xạ kết quả
public class OrderReport {
    private Long orderId;
    private String customerName;
    private Double totalAmount;
    
    public OrderReport(Long orderId, String customerName, Double totalAmount) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.totalAmount = totalAmount;
    }
    
    // Getters
}

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "SELECT o.id, c.name, o.total_amount " +
                   "FROM orders o " +
                   "INNER JOIN customers c ON o.customer_id = c.id", 
           nativeQuery = true)
    List<OrderReport> getOrderReports();
}
```

### 4.5 Native Query Với UPDATE/DELETE

```java
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Update: Giảm giá 10% cho tất cả sản phẩm trong category
    @Modifying
    @Transactional
    @Query(value = "UPDATE products SET price = price * 0.9 WHERE category_id = :categoryId", 
           nativeQuery = true)
    void reducePriceByCategory(@Param("categoryId") Long categoryId);
    
    // Delete: Xóa sản phẩm hết hàng
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM products WHERE stock = 0", 
           nativeQuery = true)
    void deleteOutOfStockProducts();
}
```

---

## 5. Truy Vấn Sử Dụng EntityManager

**EntityManager** là interface cấp thấp để tương tác trực tiếp với JPA. Hữu ích cho:
- Truy vấn động (build query lúc runtime)
- Thực thi thao tác phức tạp
- Quản lý vòng đời entity

### 5.1 EntityManager Cơ Bản

```java
@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    
    // CREATE: Lưu entity
    public User createUser(User user) {
        entityManager.persist(user);
        return user;
    }
    
    // READ: Tìm entity theo ID
    public User getUserById(Long id) {
        return entityManager.find(User.class, id);
    }
    
    // UPDATE: Cập nhật entity
    @Transactional
    public User updateUser(User user) {
        return entityManager.merge(user);
    }
    
    // DELETE: Xóa entity
    @Transactional
    public void deleteUser(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }
}
```

### 5.2 Truy Vấn JPQL Với EntityManager

```java
@Service
public class UserService {
    @PersistenceContext
    private EntityManager entityManager;
    
    // Truy vấn đơn giản
    public User findByEmail(String email) {
        String jpql = "SELECT u FROM User u WHERE u.email = :email";
        return entityManager.createQuery(jpql, User.class)
                            .setParameter("email", email)
                            .getSingleResult();  // Trả về 1 kết quả hoặc throw exception
    }
    
    // Truy vấn danh sách
    public List<User> findByStatus(String status) {
        String jpql = "SELECT u FROM User u WHERE u.status = :status";
        return entityManager.createQuery(jpql, User.class)
                            .setParameter("status", status)
                            .getResultList();  // Trả về List
    }
    
    // Truy vấn với phân trang
    public List<User> findByStatusWithPagination(String status, int page, int size) {
        String jpql = "SELECT u FROM User u WHERE u.status = :status";
        return entityManager.createQuery(jpql, User.class)
                            .setParameter("status", status)
                            .setFirstResult(page * size)  // Offset
                            .setMaxResults(size)          // Limit
                            .getResultList();
    }
}
```

### 5.3 Truy Vấn Native SQL Với EntityManager

```java
@Service
public class OrderService {
    @PersistenceContext
    private EntityManager entityManager;
    
    // Truy vấn native SQL
    public List<Order> findOrdersByCustomerId(Long customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ?1";
        return entityManager.createNativeQuery(sql, Order.class)
                            .setParameter(1, customerId)
                            .getResultList();
    }
    
    // Truy vấn trả về Object array
    public List<Object[]> getSalesReport() {
        String sql = "SELECT p.name, SUM(o.quantity) as total_quantity " +
                     "FROM orders o " +
                     "JOIN products p ON o.product_id = p.id " +
                     "GROUP BY p.name";
        return entityManager.createNativeQuery(sql)
                            .getResultList();
    }
}
```

### 5.4 UPDATE/DELETE Với EntityManager

```java
@Service
@Transactional
public class ProductService {
    @PersistenceContext
    private EntityManager entityManager;
    
    // UPDATE: Giảm giá
    public void reducePriceByCategory(Long categoryId, double discount) {
        String jpql = "UPDATE Product p SET p.price = p.price * :discount " +
                      "WHERE p.category.id = :categoryId";
        entityManager.createQuery(jpql)
                     .setParameter("discount", 1 - discount)  // discount = 0.1 (10%)
                     .setParameter("categoryId", categoryId)
                     .executeUpdate();
    }
    
    // DELETE: Xóa sản phẩm hết hàng
    public int deleteOutOfStockProducts() {
        String jpql = "DELETE FROM Product p WHERE p.stock = 0";
        return entityManager.createQuery(jpql)
                            .executeUpdate();
    }
}
```

### 5.5 Criteria API (Query Builder)

```java
@Service
public class BookService {
    @PersistenceContext
    private EntityManager entityManager;
    
    // Build query động (không viết string)
    public List<Book> findBooks(String authorName, Double minPrice) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> query = cb.createQuery(Book.class);
        Root<Book> book = query.from(Book.class);
        
        List<Predicate> predicates = new ArrayList<>();
        
        if (authorName != null) {
            predicates.add(cb.equal(book.get("author").get("name"), authorName));
        }
        
        if (minPrice != null) {
            predicates.add(cb.ge(book.get("price"), minPrice));
        }
        
        query.where(cb.and(predicates.toArray(new Predicate[0])));
        
        return entityManager.createQuery(query).getResultList();
    }
}
```

---

## Tổng Kết: So Sánh Các Phương Pháp Truy Vấn

| Phương Pháp | Khi Nào Dùng | Ưu Điểm | Nhược Điểm |
|---|---|---|---|
| **Derived Query** | Truy vấn đơn giản (1-2 điều kiện) | Dễ viết, tự động | Không linh hoạt với truy vấn phức tạp |
| **JPQL @Query** | Truy vấn phức tạp, JOIN, GROUP BY | Hướng object, rõ ràng | Cần hiểu JPQL |
| **Native Query** | Cần tính năng riêng của DB | Linh hoạt cao, tận dụng SQL | Không portable giữa các DB |
| **EntityManager** | Truy vấn động, build runtime | Rất linh hoạt | Code dài, khó đọc |
| **Criteria API** | Query builder, không string SQL | Kiểm tra compile-time | Code phức tạp |

---

## Ví Dụ Thực Tế Hoàn Chỉnh

### Bài Toán: Quản Lý Blog (Bài Viết - Bình Luận)

#### Entity
```java
@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "LONGTEXT")
    private String content;
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();
    
    // Getters & Setters
}

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(columnDefinition = "TEXT")
    private String text;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;
    
    // Getters & Setters
}
```

#### Repository
```java
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    // Derived Query
    List<Post> findByTitleContainingIgnoreCase(String keyword);
    
    // JPQL
    @Query("SELECT p FROM Post p WHERE SIZE(p.comments) > :minComments")
    List<Post> findPostsWithManyComments(@Param("minComments") int minComments);
    
    // Native Query
    @Query(value = "SELECT p.* FROM posts p " +
                   "ORDER BY (SELECT COUNT(*) FROM comments c WHERE c.post_id = p.id) DESC",
           nativeQuery = true)
    List<Post> findPostsOrderByCommentCount();
}

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    
    @Modifying
    @Transactional
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void deleteCommentsByPostId(@Param("postId") Long postId);
}
```

#### Service
```java
@Service
@Transactional
public class BlogService {
    @Autowired
    private PostRepository postRepository;
    
    @Autowired
    private CommentRepository commentRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    // Tạo bài viết mới
    public Post createPost(String title, String content) {
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }
    
    // Thêm bình luận vào bài viết
    public Comment addComment(Long postId, String text) {
        Post post = postRepository.findById(postId)
                                  .orElseThrow(() -> new RuntimeException("Post not found"));
        Comment comment = new Comment();
        comment.setText(text);
        comment.setPost(post);
        post.getComments().add(comment);
        return commentRepository.save(comment);
    }
    
    // Tìm bài viết theo từ khóa (Derived Query)
    public List<Post> searchPosts(String keyword) {
        return postRepository.findByTitleContainingIgnoreCase(keyword);
    }
    
    // Tìm bài viết có nhiều bình luận (JPQL)
    public List<Post> getPopularPosts() {
        return postRepository.findPostsWithManyComments(5);
    }
    
    // Lấy bài viết kèm số bình luận (EntityManager)
    public List<Object[]> getPostsWithCommentCount() {
        String jpql = "SELECT p.id, p.title, COUNT(c) as commentCount " +
                      "FROM Post p " +
                      "LEFT JOIN p.comments c " +
                      "GROUP BY p.id, p.title";
        return entityManager.createQuery(jpql)
                            .getResultList();
    }
}
```

---
