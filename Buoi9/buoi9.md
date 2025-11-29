# [BUỔI 5] Tối ưu JPA, Validation và Exception handling
## I. Các phương pháp tối ưu JPA
### 1. Lazy / Eager initialization
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
- Pagination chia dữ liệu thành các trang nhỏ để tránh tải toàn bộ dữ liệu cùng một lúc, cải thiện hiệu suất và trải nghiệm người dùng. 
- Ngoài ra, chúng ta thường cần sắp xếp dữ liệu đó theo một số tiêu chí trong khi phân trang.

#### Các Class/Interface quan trọng trong Spring Data JPA:
  * **`Pageable`:** Đây là giao diện trung tâm để xử lý phân trang và sắp xếp. Nó chứa thông tin về:
      * **`pageNumber` (Số trang):** Bắt đầu từ 0 (zero-based index).
      * **`pageSize` (Kích thước trang):** Số lượng bản ghi trên mỗi trang.
      * **`Sort` (Thông tin sắp xếp):** Hướng và trường sắp xếp 
  * **`Page`:** Đây là đối tượng được trả về sau khi thực hiện truy vấn phân trang. Nó không chỉ chứa danh sách các bản ghi (content) của trang hiện tại mà còn chứa các siêu dữ liệu (metadata) quan trọng:
      * `content`: Danh sách các đối tượng thực thể (entity) của trang hiện tại.
      * `totalPages`: Tổng số trang có thể có.
      * `totalElements`: Tổng số bản ghi (entity) trong toàn bộ tập dữ liệu.
      * `number`: Số trang hiện tại.
      * `size`: Kích thước trang.
  * **`PageRequest`**: Một lớp triển khai của `Pageable`, dùng để tạo đối tượng phân trang với các tham số cụ thể.
  * **`Sort`:** Được sử dụng để chỉ định trường và hướng sắp xếp (tăng dần - ASC hay giảm dần - DESC).
      * Có thể được truyền vào `Pageable` hoặc sử dụng độc lập trong các phương thức của Repository.

-----

#### Các Bước Triển Khai Phân trang và Sắp xếp

Việc triển khai được thực hiện qua 3 tầng chính: **Repository**, **Service**, và **Controller**.

##### Bước 1: Repository (Tầng Truy cập Dữ liệu)

Để hỗ trợ phân trang và sắp xếp, Repository của bạn chỉ cần **kế thừa** từ giao diện **`PagingAndSortingRepository`** hoặc **`JpaRepository`** (vì `JpaRepository` đã kế thừa `PagingAndSortingRepository`).

```java
// Giả sử bạn có Entity là User
public interface UserRepository extends JpaRepository<User, Long> {

    // Phương thức FindAll của JpaRepository đã hỗ trợ Pageable
    // Page<User> findAll(Pageable pageable);

    // Bạn cũng có thể tạo các phương thức FindBy tùy chỉnh với Pageable
    Page<User> findByEmailContaining(String email, Pageable pageable);
}
```

##### Bước 2: Service (Tầng Logic Nghiệp vụ)

Tầng Service sẽ nhận các tham số phân trang, tạo đối tượng `Pageable` và gọi Repository.

```java
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Page<User> getAllUsers(int page, int size, String sortBy, String sortDirection) {
        
        // 1. Tạo đối tượng Sort
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? 
                    Sort.by(sortBy).ascending() : 
                    Sort.by(sortBy).descending();

        // 2. Tạo đối tượng Pageable
        // PageRequest là một triển khai concrete của giao diện Pageable
        Pageable pageable = PageRequest.of(page, size, sort);

        // 3. Gọi Repository và trả về đối tượng Page
        return userRepository.findAll(pageable);
    }
}
```

##### Bước 3: Controller (Tầng Giao diện/API)

Tầng Controller sẽ nhận yêu cầu từ người dùng (thường qua `@RequestParam`) và chuyển các tham số này xuống tầng Service.

###### Cách 1: Tự nhận tham số và truyền xuống Service

Cách này rõ ràng nhưng hơi cồng kềnh.

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") String sortDirection) {

        Page<User> usersPage = userService.getAllUsers(page, size, sortBy, sortDirection);
        return ResponseEntity.ok(usersPage);
    }
}
```

###### Cách 2: Sử dụng `@PageableDefault` (Cách được khuyên dùng)

Spring Data cung cấp một đối tượng `HandlerMethodArgumentResolver` cho phép bạn **tiêm trực tiếp đối tượng `Pageable`** vào phương thức Controller.

**Ưu điểm:** Cú pháp sạch hơn, Spring tự động chuyển đổi các request param (như `page`, `size`, `sort`) thành đối tượng `Pageable`.

```java
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<Page<User>> getUsers(
            // Spring tự động tạo Pageable từ các tham số HTTP: 
            // ?page=1&size=5&sort=name,desc
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) 
            Pageable pageable) { 

        // Tầng Service chỉ cần nhận và chuyển Pageable xuống Repository
        Page<User> usersPage = userService.getAllUsers(pageable);
        return ResponseEntity.ok(usersPage);
    }
}
```

> **Lưu ý:** Nếu sử dụng cách 2, bạn cần điều chỉnh phương thức Service chỉ nhận đối tượng `Pageable` duy nhất.

```java
// Phương thức Service đã được tối ưu
public Page<User> getAllUsers(Pageable pageable) {
    return userRepository.findAll(pageable);
}
```

-----

#### Tối ưu Hóa Phản hồi API

Việc trả về đối tượng `Page` là cách tốt nhất, vì nó cung cấp toàn bộ siêu dữ liệu cần thiết cho giao diện người dùng (UI) để xây dựng bộ điều khiển phân trang.

**Phản hồi JSON mẫu (ví dụ về đối tượng `Page`):**

```json
{
  "content": [
    { "id": 1, "name": "User A" },
    { "id": 2, "name": "User B" }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 2,
    "sort": { "sorted": true, "unsorted": false, "empty": false },
    // ... thông tin Pageable khác
  },
  "totalPages": 5,
  "totalElements": 10,
  "last": false,
  "size": 2,
  "number": 0,
  "numberOfElements": 2,
  "first": true,
  "empty": false
}
```
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

### 4. Manual / Native query
Native query là các câu lệnh SQL thuần được viết trực tiếp, cho phép sử dụng toàn bộ các tính năng của database cụ thể.

**Khi nào dùng Native Query:**
- JPQL không đáp ứng yêu cầu
- Cần sử dụng các tính năng database cụ thể (FULL TEXT SEARCH, ...)
- Tối ưu hóa hiệu suất cho các query phức tạp

Để định nghĩa một native query, chúng ta chú thích phương thức Repository với **`@Query`**, thiết lập thuộc tính **`nativeQuery=true`**, và cung cấp câu lệnh SQL gốc làm giá trị:

Để thực hiện thao tác ghi (update/delete), chúng ta cần:
1.  Sử dụng annotation **`@Query`** với **`nativeQuery=true`**.
2.  Bổ sung annotation **`@Modifying`** để báo cho Spring Data biết đây là một thao tác sửa đổi dữ liệu.
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

#### Hạn chế của Native Queries

Mặc dù mạnh mẽ, việc sử dụng Native Queries trong Spring Data JPA có ba hạn chế đáng chú ý:

1.  **Không Tự Điều Chỉnh Ngôn Ngữ SQL:**

      * Spring Data JPA và Persistence Provider (Hibernate) **không tự động điều chỉnh** truy vấn theo cú pháp SQL của một cơ sở dữ liệu cụ thể (ví dụ: cú pháp Oracle khác với MySQL).
      * **Yêu cầu:** Chúng ta phải tự đảm bảo câu lệnh SQL được cung cấp tương thích với **tất cả** các RDBMS mà ứng dụng hỗ trợ.

2.  **Yêu Cầu `countQuery` khi Phân Trang (Pagination):**

      * Đối với truy vấn **JPQL** thông thường, Spring Data JPA tự động tạo câu lệnh `COUNT` để xác định tổng số bản ghi khi sử dụng tham số `Pageable`.
      * Đối với **Native Query**, Spring Data JPA không thể tự động tạo câu lệnh `COUNT`. Chúng ta phải cung cấp câu lệnh `count query` để trả về tổng số bản ghi bằng cách sử dụng thuộc tính **`countQuery`** trong annotation `@Query`.

    <!-- end list -->

    ```java
    @Repository
    public interface AuthorRepository extends CrudRepository<Author, Long>, PagingAndSortingRepository<Author, Long> {
         
        @Query(value="select * from author a where a.last_name= ?1", 
                countQuery = "select count(id) from author a where a.last_name= ?1", // Bắt buộc phải cung cấp
                nativeQuery = true)
        Page<Author> getAuthorsByLastName(String lastname, Pageable page);
    }
    ```

      * **Trường hợp Named Native Query:** Nếu tham chiếu đến `named native query`, cần bổ sung một `named native query` riêng để đếm tổng số bản ghi và thêm hậu tố **`.count`** vào tên của nó.

    <!-- end list -->

    ```java
    @NamedNativeQuery(name = "Author.getAuthorsByLastName", 
                        query = "select * from author a where a.last_name= ?1", 
                        resultClass = Author.class)
    @NamedNativeQuery(name = "Author.getAuthorsByLastName.count", 
                        query = "select count(id) from author a where a.last_name= ?1")
    @Entity
    public class Author { ... }
    ```

3.  **Không Hỗ Trợ Sắp Xếp Động (Dynamic Sorting):**

      * Với JPQL, chúng ta có thể thêm tham số kiểu **`Sort`** hoặc **`Pageable`** vào phương thức Repository, và Spring Data sẽ tự động tạo mệnh đề `ORDER BY` dựa trên tham số đó.
      * **Native Query không hỗ trợ** tính năng sắp xếp động này. Lý do là việc phân tích cú pháp câu lệnh SQL gốc và tạo mệnh đề `ORDER BY` theo cách an toàn kiểu (type-safe) là một hoạt động phức tạp, chưa được Spring Data JPA hỗ trợ.
      * **Giải pháp:** Nếu cần sắp xếp, bạn phải tự bổ sung mệnh đề `ORDER BY` vào câu lệnh SQL gốc. Điều này làm giảm tính linh động của truy vấn.

-----

#### Kết Luận Chung

Native Queries là một công cụ mạnh mẽ, mang lại sự linh hoạt tối đa khi cần tận dụng các tính năng của cơ sở dữ liệu gốc. Tuy nhiên, việc sử dụng chúng đòi hỏi nhiều công sức hơn so với **Derived Query** hoặc **Custom JPQL Query** và đi kèm với các đánh đổi quan trọng:

| Tính năng | JPQL Query | Native Query |
| :--- | :--- | :--- |
| **Linh hoạt** | Giới hạn bởi chuẩn JPA | Tối đa, sử dụng toàn bộ tính năng DB |
| **Phân trang** | Tự động tạo `COUNT` | **Cần cung cấp `countQuery` thủ công** |
| **Sắp xếp động** | Hỗ trợ tự động (`Sort`/`Pageable`) | **Không hỗ trợ** (phải tự viết `ORDER BY`) |
| **Khả năng tương thích DB** | Tương thích cao giữa các DB | **Không tương thích**, yêu cầu kiểm tra thủ công |



### 5. Dynamic query

### 6. Phòng tránh N + 1 query
#### Vấn Đề N+1 Select: Tại Sao Nó Xảy Ra?

Chúng ta bắt đầu với vai trò của **ORM (Object-Relational Mapping)** là cầu nối giữa các **Entity** (đối tượng trong mã nguồn) và các đối tượng **Schema** (bảng) trong cơ sở dữ liệu quan hệ (RDBMS).

Trong RDBMS, chúng ta mô hình các mối quan hệ giữa các bảng thông qua **Foreign Key** (Khóa ngoại). Ví dụ, bảng `equipment` có tham chiếu khóa ngoại đến bảng `manufacturer` và bảng `equipment_items`.

##### 1\. Mô hình Quan hệ trong Code và DB

Quan hệ trong cơ sở dữ liệu được ánh xạ sang các mối quan hệ logic trong code (ví dụ: JPA/Hibernate):

  * **`equipment` (N) $\rightarrow$ `manufacturer` (1):** Mối quan hệ **N:1** (Many-to-One).
  * **`equipment` (1) $\rightarrow$ `equipment_items` (N):** Mối quan hệ **1:N** (One-to-Many).

##### 2\. Sự Cố Phát Sinh từ Tải Dữ liệu Lười (Lazy Loading)

Khi chúng ta truy vấn một thực thể (entity) cơ bản, ORM thường chỉ lấy dữ liệu của thực thể đó.

**Truy vấn ban đầu (1 SELECT):**

```sql
SELECT e.* FROM equipment e WHERE e.id = ?
```

Sau truy vấn này, ta chỉ có thông tin của `equipment`. Thông tin từ các bảng liên quan (`manufacturer`, `equipment_items`) chưa được tải vào ứng dụng.

Để tối ưu hiệu suất, nhiều ORM sử dụng tính năng **Tải Lười (Lazy Loading)**. Tính năng này hoãn việc tải dữ liệu của các mối quan hệ cho đến khi chúng thực sự được truy cập trong mã nguồn.

  * Nếu cần thông tin `manufacturer`, ORM sẽ tự động gửi thêm một truy vấn:
    ```sql
    SELECT m.* FROM manufacturer m INNER JOIN equipment e ON e.manufacturer_id = m.id WHERE e.id = ?
    ```
  * Nếu cần danh sách `equipment_items`, ORM sẽ gửi thêm một truy vấn khác:
    ```sql
    SELECT ei.* FROM equipment_item ei WHERE ei.equipment_id = ?
    ```

##### 3\. Tái Hiện "Vấn Đề N+1 Select"

Trong ví dụ trên, để tải hoàn chỉnh thực thể `equipment` và **tất cả** các mối quan hệ của nó, chúng ta đã phải thực hiện:

$$1 (\text{truy vấn chính}) + 2 (\text{truy vấn cho các mối quan hệ}) = 3 \text{ truy vấn}$$

  * **Vấn đề N+1 Select** mô tả tình huống khi chúng ta tải một thực thể bằng **1** truy vấn `SELECT`, và sau đó là **N** truy vấn `SELECT` bổ sung để tải các mối quan hệ (dependencies) của nó.

Vấn đề này dẫn đến **hiệu suất ứng dụng kém** vì mỗi truy vấn phải chịu **chi phí không thể tránh khỏi** của chuỗi trừu tượng sau:

  * Mã ứng dụng (Application code)
  * JDBC driver
  * Mạng (Network latency)
  * Phân tích cú pháp, lập lịch và thực thi truy vấn cơ sở dữ liệu

Thay vì 3 truy vấn riêng lẻ, chúng ta **có thể** đạt được kết quả tương đương chỉ trong **1 truy vấn duy nhất** bằng cách sử dụng `JOIN`:

```sql
SELECT * FROM equipment e
LEFT JOIN manufacturer m ON e.manufacturer_id = m.id
LEFT JOIN equipment_item ei ON ei.equipment_id = e.id
WHERE e.id = ?
```

Việc thực hiện một truy vấn duy nhất với các `JOIN` **nhanh hơn đáng kể** so với việc phát hành nhiều truy vấn riêng lẻ.

-----

#### Giải Pháp Cho Vấn Đề N+1

Cần hiểu rõ rằng vấn đề không phải do bản thân **Tải Lười (Lazy Loading)**. Lazy Loading rất hữu ích khi chúng ta **chỉ cần** thực thể `equipment` mà không cần các mối quan hệ của nó (khi đó 1 truy vấn `SELECT` đơn giản là đủ).

Vấn đề chỉ xảy ra khi chúng ta **cần** thực thể cùng với **một số hoặc tất cả** các mối quan hệ của nó.

Về mặt lý thuyết, giải pháp rất đơn giản: **phát hành các câu lệnh SQL khác nhau cho từng kịch bản cụ thể.**

  * Nếu chỉ cần `equipment`: Query bảng `equipment`.
  * Nếu cần `equipment` với `manufacturer`: Query `equipment` với `JOIN` đến `manufacturer`.

Bằng cách này, chúng ta vẫn hưởng lợi từ Lazy Loading trong trường hợp đơn giản và không bị ảnh hưởng bởi N+1 khi cần tải đầy đủ.

#### Cơ chế Hỗ trợ trong ORM

May mắn thay, các framework ORM lớn đều hỗ trợ cơ chế này:

  * **JPA/Hibernate (từ JPA 2.1):** Chúng ta có thể sử dụng **EntityGraph** để chỉ định rõ những mối quan hệ nào cần được tải **ngay lập tức (eagerly)** trong một truy vấn cụ thể.
  * **C\# Entity Framework:** Sử dụng phương thức **`Include`** để tải các mối quan hệ lười (lazy associations) một cách **háo hức (eagerly)** khi cần.

-----

#### Kết Luận

Vấn đề **N+1 Select** là một cạm bẫy hiệu suất phổ biến trong ORM, phát sinh khi các mối quan hệ lười (Lazy) được truy cập và kích hoạt nhiều truy vấn bổ sung (N truy vấn) sau truy vấn ban đầu (1 truy vấn). Giải pháp là sử dụng các tính năng của ORM (như **EntityGraph** hoặc **Include**) để chuyển từ tải lười (Lazy Loading) sang **tải háo hức (Eager Loading)** **theo yêu cầu** bằng cách sử dụng các lệnh `JOIN` hiệu quả.
## II. Tìm hiểu về Validation
Validation là quá trình kiểm tra xem dữ liệu đầu vào có hợp lệ trước khi xử lý. Thay vì viết logic xác thực thủ công trong code, bạn chỉ cần sử dụng các annotations để khai báo các ràng buộc (constraints) trên các thuộc tính của lớp (fields), phương thức (methods) hoặc lớp đó.
- Thêm dependency 
```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-validation</artifactId> 
    <version>3.5.7</version>
</dependency>
```
### Các Annotation Validation Cơ bản và Chi tiết Cách dùng
Các annotations được sử dụng để áp dụng các ràng buộc cho các thuộc tính (field) của một lớp đối tượng (thường là Form Object hoặc DTO - Data Transfer Object).


| Annotation | Áp dụng cho | Mô tả Ràng buộc | Ví dụ Sử dụng |
| :--- | :--- | :--- | :--- |
| **`@NotNull`** | Mọi loại dữ liệu | Giá trị của thuộc tính **không được là `null`**. Cho phép chuỗi rỗng (`""`) hoặc collection rỗng. | `@NotNull private String username;` |
| **`@NotEmpty`** | String, Collection, Map, Array | Giá trị **không được là `null`** và **không được rỗng** (phải có ít nhất 1 phần tử hoặc 1 ký tự). | `@NotEmpty private List<String> items;` |
| **`@NotBlank`** | String | Giá trị **không được là `null`**, **không được rỗng**, và **không được chỉ chứa ký tự khoảng trắng** (space). | `@NotBlank private String name;` |
| **`@Size(min=x, max=y)`** | String, Collection, Map, Array | Kích thước (số lượng ký tự hoặc số phần tử) của thuộc tính phải nằm trong khoảng **`[min, max]`**. | `@Size(min = 2, max = 30) private String name;` |
| **`@Min(value)`** | Số (numeric) | Giá trị phải **lớn hơn hoặc bằng `value`**. | `@Min(18) private int age;` |
| **`@Max(value)`** | Số (numeric) | Giá trị phải **nhỏ hơn hoặc bằng `value`**. | `@Max(100) private int age;` |
| **`@Email`** | String | Giá trị phải là một **địa chỉ email hợp lệ** theo định dạng cơ bản. | `@Email private String email;` |
| **`@Pattern(regexp)`** | String | Giá trị phải **khớp** với biểu thức chính quy (**Regular Expression**) đã cho. | `@Pattern(regexp = "\\d{10}") private String phoneNumber;` |
| **`@Past`** | Date/Time | Giá trị phải là một thời điểm trong **quá khứ**. | `@Past private Date birthday;` |
| **`@Future`** | Date/Time | Giá trị phải là một thời điểm trong **tương lai**. | `@Future private Date eventDate;` |
| **`@AssertTrue`** | Boolean | Giá trị phải là **`true`**. | `@AssertTrue private boolean isAdult;` |
| **`@Valid`** | Object, Collection, Map | Kích hoạt **xác thực cascade** (xác thực các thuộc tính của đối tượng lồng nhau). | `@Valid private Address address;` |

- Ví dụ:
```java
import jakarta.validation.constraints.*; 

public class PersonForm {
    // Tên: Không được rỗng, không được chỉ có khoảng trắng, độ dài từ 2 đến 30 ký tự
    @NotBlank(message = "Tên không được để trống.")
    @Size(min = 2, max = 30, message = "Tên phải có từ 2 đến 30 ký tự.")
    private String name;
    // Tuổi: Không được null, phải là số nguyên tối thiểu là 18
    @NotNull(message = "Tuổi không được null.")
    @Min(value = 18, message = "Tuổi phải lớn hơn hoặc bằng 18.")
    private Integer age; 
    // Email: Không được rỗng, phải đúng định dạng email
    @NotBlank(message = "Email không được để trống.")
    @Email(message = "Email không hợp lệ.")
    private String email;
    
    // ... getters và setters
}
```

### Kích hoạt Validation trong Controller
Để kích hoạt validation khi nhận request, bạn cần thêm annotation `@Valid` (hoặc `@Validated` trong một số trường hợp) vào tham số đối tượng trong Controller.
- Các trường hợp sử dụng `@Valid` và `@Validated`:

| Trường hợp | Nên dùng chú thích nào? | Cách dùng (Ví dụ) |
| :--- | :--- | :--- |
| **1. Kiểm tra DTO/Model** (Ví dụ: `@RequestBody`) | `@Valid` hoặc `@Validated` | Đặt trên tham số phương thức: `public void create(@Valid UserDto user)` |
| **2. Kiểm tra tham số URI/Truy vấn** (Tức `@PathVariable` / `@RequestParam`) | **`@Validated`** | Phải đặt `@Validated` trên **Lớp Controller** và thêm ràng buộc kiểm tra trên tham số. |
| **3. Kiểm tra theo nhóm** (Group Validation, ví dụ: Đăng ký vs Cập nhật) | **`@Validated`** | Đặt trên tham số phương thức và chỉ định nhóm: `public void update(@Validated(OnUpdate.class) UserDto user)` |
| **4. Kiểm tra thuộc tính lồng nhau** (Nested Validation) | **`@Valid`** | Đặt `@Valid` trên thuộc tính là đối tượng lồng nhau bên trong DTO/Model. |

**Xử lí lỗi:**
- **@Valid**: Nếu validation thất bại, nó sẽ kích hoạt **`MethodArgumentNotValidException`**. Mặc định, Spring dịch lỗi này thành HTTP status **400 (Bad Request)**.

- **@Validated**: Nếu validation thất bại, nó kích hoạt **`ConstraintViolationException`** (khác với `MethodArgumentNotValidException` của Request Body), gây ra HTTP status **500 (Internal Server Error)**.
- ví dụ:
```java
    @PostMapping
    public ApiResponse<LopHocPhanResponse> createLopHocPhan(@Valid @RequestBody LopHocPhanRequest request) {
        return ApiResponse.<LopHocPhanResponse>builder()
                .code(1000)
                .message("Tạo lớp học phần thành công")
                .result(lopHocPhanService.createLopHocPhan(request))
                .build();
    }
```
## III. Tìm hiểu về Exception 
Xử lý ngoại lệ (Exception handling) là một phần thiết yếu trong phát triển ứng dụng, đặc biệt là khi xây dựng các **Restful API** trong **Spring Boot**. Mục tiêu là thay vì trả về một lỗi hệ thống (như HTTP Status 500) không thân thiện, chúng ta sẽ trả về một phản hồi lỗi có cấu trúc, dễ hiểu cho người dùng/client.
### 1\. Giới Thiệu các Annotation Chính

Spring Framework cung cấp một cơ chế mạnh mẽ để xử lý ngoại lệ trên toàn bộ ứng dụng bằng cách sử dụng các Annotation cấp lớp và phương thức.

| Annotation | Loại | Phạm vi | Mục đích |
| :--- | :--- | :--- | :--- |
| **`@ControllerAdvice`** | Cấp Class | Toàn cục | Định nghĩa một lớp chứa các phương thức xử lý ngoại lệ áp dụng cho **tất cả các `@Controller`** trong ứng dụng. |
| **`@RestControllerAdvice`** | Cấp Class | Toàn cục | Tương tự như `@ControllerAdvice`, nhưng kết hợp sẵn với `@ResponseBody`. Thường dùng cho **RESTful API** để trả về lỗi dưới dạng JSON/XML. |
| **`@ExceptionHandler`** | Cấp Method | Trong lớp `@Controller`/`@ControllerAdvice` | Đánh dấu một phương thức là trình xử lý cho một loại ngoại lệ cụ thể (ví dụ: `IndexOutOfBoundsException.class`). |
| **`@ResponseStatus`** | Cấp Method hoặc Class | Trong trình xử lý | Định nghĩa **HTTP status code** mà bạn muốn trả về cho client. |

- Mục đích của exception handler:
  - Tập trung việc chuyển đổi exception thành HTTP response có cấu trúc.
  - Tránh leak stacktrace/chi tiết nội bộ ra client.
  - Thống nhất định dạng lỗi để client dễ xử lý.
- Thành phần chính:
  - DTO phản hồi lỗi (ví dụ `ApiResponse` / `ErrorResponse`).
  - Enum/registry mã lỗi (optional nhưng khuyến khích).
  - Custom exceptions dùng trong business logic.
  - Global handler (`@ControllerAdvice`/`@RestControllerAdvice`) để map exception -> response.
## Các bước xây dựng hệ thống xử lý lỗi (exception handling) 

### 1. Thiết kế contract phản hồi lỗi
Tạo một DTO chung để trả về cho mọi lỗi. Ví dụ:

```java
public class ErrorResponse<T> {
    private int code;
    private String message;
    private T details; // optional, có thể là list field errors
    // getters/setters/constructors
}
```

- `code`: mã lỗi ứng dụng (không bắt buộc phải trùng HTTP status).
- `message`: thông điệp người dùng / dev.
- `details`: thông tin bổ sung (ví dụ danh sách lỗi field validation).

### 2. Tạo tập mã lỗi (Error codes)
Dùng enum để quản lý mã và thông điệp lỗi:

```java
public enum ErrorCode {
    INVALID_REQUEST(1000, "Invalid request"),
    RESOURCE_NOT_FOUND(1001, "Resource not found"),
    DUPLICATE_ENTITY(1002, "Duplicate entity"),
    INTERNAL_ERROR(2000, "Internal server error");

    private final int code;
    private final String message;

    // constructor, getters
}
```
Lợi ích: Đồng bộ thông điệp, dễ map sang HTTP status nếu cần.

### 3. Viết custom exception
Tạo exception tùy chỉnh để ném ở service / domain layer:

```java
public class AppException extends RuntimeException {
    private final ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() { return errorCode; }
}
```

Sử dụng:
```java
if (entity == null) {
    throw new AppException(ErrorCode.RESOURCE_NOT_FOUND);
}
```
### 4. Tạo Global Exception Handler
Sử dụng `@ControllerAdvice` hoặc `@RestControllerAdvice` để xử lý ngoại lệ toàn cục.

Ví dụ:

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AppException.class)
    public ResponseEntity<ErrorResponse<?>> handleAppException(AppException ex) {
        ErrorCode ec = ex.getErrorCode();
        ErrorResponse<Void> body = new ErrorResponse<>(ec.getCode(), ec.getMessage(), null);
        HttpStatus status = mapToHttpStatus(ec); // implement mapping logic
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse<List<FieldErrorDto>>> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldErrorDto> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(fe -> new FieldErrorDto(fe.getField(), fe.getDefaultMessage()))
            .collect(Collectors.toList());
        ErrorResponse<List<FieldErrorDto>> body = new ErrorResponse<>(1000, "Validation failed", errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse<?>> handleGeneric(Exception ex) {
        // log exception
        ErrorResponse<Void> body = new ErrorResponse<>(ErrorCode.INTERNAL_ERROR.getCode(), "Server error", null);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
```

Ghi chú:
- Dùng `@RestControllerAdvice` giúp trả JSON mà không cần `@ResponseBody`.
- Handler cho `Exception`/`RuntimeException` làm fallback cho lỗi không lường trước.

### 5. Xử lý validation errors (Request validation)
Khi dùng `@Valid` trong request DTO, Spring sẽ ném `MethodArgumentNotValidException`.

Cách phổ biến:
- Lấy `BindingResult` → danh sách `FieldError` → map thành `details` trong `ErrorResponse`.
- Tránh đặt `message` constraint là một "enum key" trừ khi bạn muốn map như vậy.

Ví dụ DTO field errors:

```java
public class FieldErrorDto {
    private String field;
    private String message;
    // constructor/getters/setters
}
```
### 6. Mapping sang HTTP Status phù hợp
Không phải tất cả lỗi đều trả `400`. Một số mapping gợi ý:
- Validation / Bad input → 400 Bad Request
- Resource not found → 404 Not Found
- Unauthorized → 401 Unauthorized
- Forbidden → 403 Forbidden
- Business rule violated → 409 Conflict (tuỳ trường hợp)
- Unexpected server error → 500 Internal Server Error

Bạn có thể:
- Thêm trường `HttpStatus` hoặc `int httpStatus` vào `ErrorCode`.
- Hoặc tạo một mapper `ErrorCode -> HttpStatus`.
## IV. Override default fetch plan với join fetch / entity grap
## 1\. Lý Thuyết: Tại Sao Cần Ghi Đè Chiến Lược Fetch?

### 1.1. Chiến Lược Fetch Mặc Định (Lazy vs Eager)

Trong JPA, các mối quan hệ giữa các Entity được định nghĩa với một chiến lược tải (fetch strategy) mặc định:

  * **Tải Lười (**`LAZY`**):** Dữ liệu của mối quan hệ sẽ **chưa** được tải cùng với Entity gốc. Chúng chỉ được tải khi bạn **thực sự truy cập** chúng (ví dụ: gọi phương thức `get` trong mã code). Đây là mặc định cho các mối quan hệ **1:N** (`@OneToMany`) và **N:M** (`@ManyToMany`).
  * **Tải Háo hức (**`EAGER`**):** Dữ liệu của mối quan hệ sẽ **luôn** được tải cùng với Entity gốc trong một truy vấn duy nhất. Đây là mặc định cho các mối quan hệ **N:1** (`@ManyToOne`) và **1:1** (`@OneToOne`).

### 1.2. Vấn Đề N+1 Select

Vấn đề N+1 Select thường xảy ra khi bạn lặp qua một tập hợp các Entity và truy cập một mối quan hệ **`LAZY`** của chúng.

1.  **1** truy vấn được thực hiện để lấy danh sách các Entity gốc (ví dụ: 100 đối tượng `Student`).
2.  Sau đó, một truy vấn **`SELECT`** mới được thực hiện cho mỗi lần bạn truy cập mối quan hệ `LAZY` của mỗi Entity (ví dụ: lấy `School` của từng `Student`).
3.  Tổng cộng: $1 + N$ (100) truy vấn.

Để giải quyết vấn đề này, chúng ta cần **ghi đè** chiến lược `LAZY` mặc định và buộc JPA tải các mối quan hệ cần thiết một cách **háo hức** thông qua một `JOIN` ngay trong truy vấn ban đầu.

-----

## 2\. Phương Pháp 1: Sử Dụng JOIN FETCH (JPQL)

**`JOIN FETCH`** là một cú pháp trong **JPQL (Java Persistence Query Language)** cho phép bạn chỉ định các mối quan hệ nào cần được tải ngay lập tức bằng cách sử dụng `JOIN` trong câu lệnh SQL gốc.

### Lý thuyết:

  * **Mục đích:** Buộc Persistence Provider (Hibernate) sử dụng câu lệnh `JOIN` để kéo dữ liệu của các Entity liên quan vào kết quả trả về của truy vấn chính.
  * **Cú pháp:** `SELECT e FROM Entity e JOIN FETCH e.relationship`.

### Ví dụ Code Chi tiết

Giả sử Entity **`Student`** có mối quan hệ **`@ManyToOne`** lười (lazy) với **`School`**:

```java
// Entity
@Entity
public class Student {
    // ...
    @ManyToOne(fetch = FetchType.LAZY) // Mặc định là LAZY, hoặc bạn cố tình đặt LAZY
    private School school;
    // ...
}

// Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 1. Ghi đè Fetch Plan với JOIN FETCH
    // Buộc tải eager mối quan hệ 'school'
    @Query("SELECT s FROM Student s JOIN FETCH s.school WHERE s.id = :studentId")
    Optional<Student> findByIdWithSchoolJoinFetch(@Param("studentId") Long studentId);

    // 2. Xử lý One-to-Many với DISTINCT (Cần thiết để tránh lặp lại dữ liệu)
    @Query("SELECT DISTINCT s FROM Student s JOIN FETCH s.school")
    List<Student> findAllStudentsWithSchoolEagerly();
}
```

### Ưu điểm:

  * **Kiểm soát trực tiếp:** Bạn có toàn quyền kiểm soát cấu trúc câu lệnh `JOIN` (dùng `INNER JOIN` hay `LEFT JOIN`).
  * **Không cần cấu hình Entity:** Không cần thêm annotation vào Entity.

### Hạn chế:

  * **Không tái sử dụng:** Phải viết lại cú pháp `JOIN FETCH` cho mỗi truy vấn cần ghi đè fetch plan.

-----

## 3\. Phương Pháp 2: Sử Dụng @EntityGraph (JPA Standard)

**`@EntityGraph`** là cơ chế được giới thiệu trong JPA 2.1, cho phép bạn định nghĩa các **"đồ thị thực thể"** (Entity Graphs) – các tập hợp thuộc tính cần được tải **eagerly** – và áp dụng chúng cho các phương thức Repository.

### Lý thuyết:

  * **Mục đích:** Tách biệt định nghĩa truy vấn với định nghĩa fetch plan. Bạn chỉ cần đặt tên cho EntityGraph hoặc mô tả nó, và JPA sẽ xử lý việc tạo câu lệnh `JOIN` (hoặc `SELECT` thứ cấp) phù hợp.
  * **Vị trí:** Có thể được định nghĩa trên Entity (`@NamedEntityGraph`) hoặc trực tiếp trên phương thức Repository (`@EntityGraph(attributePaths = {...})`).

### Ví dụ Code Chi tiết

#### Bước 1: Định nghĩa Graph (Tùy chọn)

Định nghĩa Graph trên Entity `Student` (chỉ rõ mối quan hệ `school` cần tải):

```java
@Entity
@NamedEntityGraph(
    name = "student-with-school", // Tên của Entity Graph
    attributeNodes = {
        @NamedAttributeNode("school") // Tải eagerly mối quan hệ 'school'
    }
)
public class Student {
    // ...
    @ManyToOne(fetch = FetchType.LAZY)
    private School school; 
    // ...
}
```

#### Bước 2: Áp dụng Graph vào Repository

Áp dụng `@EntityGraph` lên phương thức Repository để ghi đè fetch plan:

```java
public interface StudentRepository extends JpaRepository<Student, Long> {

    // 1. Áp dụng EntityGraph đã đặt tên
    @EntityGraph(value = "student-with-school", type = EntityGraph.EntityGraphType.LOAD)
    List<Student> findAll(Sort sort); 
    // Ghi đè: Khi gọi findAll, mối quan hệ 'school' sẽ được tải eagerly.
    
    // 2. Định nghĩa EntityGraph ẩn danh (thường dùng)
    @EntityGraph(attributePaths = {"school"}) 
    Optional<Student> findById(Long id);
    // Ghi đè: Khi gọi findById, mối quan hệ 'school' sẽ được tải eagerly.
    
    // 3. EntityGraph cho mối quan hệ lồng nhau
    @EntityGraph(attributePaths = {"school", "school.address"})
    List<Student> findAllWithSchoolAndAddress();
}
```

### Các Kiểu EntityGraph (`EntityGraphType`)

Kiểu `EntityGraph` ảnh hưởng đến cách JPA xử lý các thuộc tính **không** được chỉ định trong đồ thị:

  * **`EntityGraphType.FETCH` (Mặc định):** Các thuộc tính được chỉ định trong `@EntityGraph` là **EAGER**. Các thuộc tính **Lazy** khác **vẫn là Lazy**. Các thuộc tính **Eager** khác **vẫn là Eager**.
  * **`EntityGraphType.LOAD`:** Các thuộc tính được chỉ định trong `@EntityGraph` là **EAGER**. Các thuộc tính còn lại sẽ sử dụng chiến lược **mặc định** đã được định nghĩa trên Entity (ví dụ: nếu bạn đặt `@OneToMany(fetch = EAGER)`, nó sẽ được tôn trọng).

### Ưu điểm:

  * **Tách biệt (Separation of Concerns):** Tách biệt định nghĩa truy vấn với fetch plan.
  * **Tái sử dụng cao:** Có thể áp dụng Entity Graph cho nhiều truy vấn khác nhau.
  * **Tiêu chuẩn JPA:** Là cơ chế chuẩn, tương thích tốt hơn.

### Hạn chế:

  * **Cần cấu hình Entity:** Nếu sử dụng `@NamedEntityGraph`, cần thêm annotation vào Entity.

-----

## 4\. Tổng Kết và Khuyến Nghị

Để ghi đè fetch plan và tránh Vấn đề N+1 Select, cả hai cơ chế đều hiệu quả.

| Cơ chế | Cú pháp | Khi nào sử dụng |
| :--- | :--- | :--- |
| **`JOIN FETCH`** | `@Query("SELECT s FROM ... JOIN FETCH ...")` | Khi bạn cần **kiểm soát chính xác** câu lệnh JPQL hoặc khi EntityGraph không giải quyết được các truy vấn phức tạp. |
| **`@EntityGraph`** | `@EntityGraph(attributePaths = {...})` | **Là phương pháp ưu tiên.** Khi bạn cần sự rõ ràng, dễ bảo trì và khả năng tái sử dụng cao cho các fetch plan. |

**Khuyến nghị:** Luôn ưu tiên sử dụng **`@EntityGraph`** để ghi đè fetch plan, vì nó giúp Repository của bạn rõ ràng, không bị trộn lẫn giữa logic truy vấn và logic tải dữ liệu.
