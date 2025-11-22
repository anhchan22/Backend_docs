# BUỔI 4: THAO TÁC VỚI DATABASE

## I. Các mối quan hệ (1..N, N..N) trong JPA
### One-to-Many / Many-to-One
- Đây là mối quan hệ phổ biến nhất. Một thực thể A có thể liên kết với nhiều thực thể B, nhưng mỗi thực thể B chỉ liên kết với duy nhất một thực thể A.
- Trong JPA, chúng ta sử dụng `@OneToMany` trên bên “một” và `@ManyToOne` trên bên “nhiều”. Theo quy ước và thực tế, bên “nhiều” (sở hữu khóa ngoại trong CSDL) thường là bên sở hữu (owning side) trong JPA. Vì vậy, `@ManyToOne` thường là bên sở hữu.

Ví dụ: Tác giả và Sách
```java
@Entity
public class TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenTacGia;

    // Inverse side: One-to-Many relationship. Mapped by the 'tacGia' field in the Sach entity.
    @OneToMany(mappedBy = "tacGia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sach> danhSachSach;

    // Constructor to initialize the list
    public TacGia() {
        this.danhSachSach = new ArrayList<>();
    }

    // Helper method to add a book and maintain bidirectional link
    public void addSach(Sach sach) {
        danhSachSach.add(sach);
        sach.setTacGia(this); // Set the owning side
    }

    // Helper method to remove a book
    public void removeSach(Sach sach) {
        danhSachSach.remove(sach);
        sach.setTacGia(null); // Break the link
    }

    // Getters and Setters...
}

@Entity
public class Sach {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tieuDe;

    // Owning side: Many-to-One relationship. Sach holds the foreign key to TacGia.
    @ManyToOne(fetch = FetchType.LAZY) // Lazy loading is often better for performance
    @JoinColumn(name = "tac_gia_id") // Specifies the FK column in the 'sach' table
    private TacGia tacGia;

    // Getters and Setters...
}
```
Trong ví dụ này:
- `Sach` là bên sở hữu (owning side) với `@ManyToOne`. Annotation `@JoinColumn(name = "tac_gia_id")` chỉ ra rằng bảng `sach` có cột `tac_gia_id` là khóa ngoại tham chiếu đến bảng `tac_gia`. Đây là nơi JPA sẽ quản lý việc thiết lập và cập nhật khóa ngoại.
- `TacGia` là bên đảo ngược (inverse side) với `@OneToMany`. `mappedBy = "tacGia"` chỉ ra rằng mối quan hệ này được quản lý bởi trường `tacGia` trong lớp `Sach`.
- Chúng ta thường sử dụng các Collection như `List` hoặc `Set` để biểu diễn bên “nhiều” (`danhSachSach` trong `TacGia`).
- `fetch = FetchType.LAZY` trên `@ManyToOne` (và thường là default cho `@OneToMany`) là một tối ưu hiệu suất. Nó chỉ tải dữ liệu của `TacGia` khi bạn truy cập trường `tacGia` của một đối tượng `Sach`, thay vì tải ngay lập tức khi load đối tượng `Sach` (EAGER).
- `cascade = CascadeType.ALL` và `orphanRemoval = true` trên `@OneToMany` giúp quản lý danh sách sách của tác giả. `orphanRemoval = true` đặc biệt hữu ích: nếu một cuốn sách bị xóa khỏi danh sách `danhSachSach` của một tác giả và cuốn sách đó không còn được liên kết với tác giả nào khác, JPA sẽ tự động xóa cuốn sách đó khỏi CSDL.
- Các phương thức helper `addSach` và `removeSach` trong `TacGia` là cách tốt để đảm bảo tính nhất quán của mối quan hệ hai chiều (bidirectional relationship) bằng cách thiết lập liên kết ở cả hai phía.
### Many-to-Many
- Mối quan hệ Many-to-Many xảy ra khi một thực thể A có thể liên kết với nhiều thực thể B, và một thực thể B cũng có thể liên kết với nhiều thực thể A.
- Trong JPA, chúng ta sử dụng `@ManyToMany` trên cả hai thực thể. Một trong hai bên sẽ định nghĩa bảng trung gian bằng `@JoinTable`.
Tôi hiểu rồi. Yêu cầu của bạn là chuyển toàn bộ nội dung đã cung cấp (bao gồm ví dụ code và phân tích) sang định dạng Markdown chuẩn, không cần tóm tắt lại.

Dưới đây là nội dung đã được chuyển đổi sang Markdown:

Ví dụ: Sinh Viên và Khóa Học

Chúng ta có hai thực thể: **SinhVien** (Student) và **KhoaHoc** (Course). Một sinh viên có thể học nhiều khóa học, và một khóa học có nhiều sinh viên.



```java
@Entity
public class SinhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenSinhVien;

    // Owning side: Defines the join table
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }) // Configure cascade types carefully
    @JoinTable(
        name = "sinh_vien_khoa_hoc", // Name of the linking table
        joinColumns = @JoinColumn(name = "sinh_vien_id"), // FK column in linking table referencing SinhVien
        inverseJoinColumns = @JoinColumn(name = "khoa_hoc_id") // FK column in linking table referencing KhoaHoc
    )
    private Set<KhoaHoc> danhSachKhoaHoc = new HashSet<>();

    // Helper methods to add/remove courses
    public void addKhoaHoc(KhoaHoc khoaHoc) {
        this.danhSachKhoaHoc.add(khoaHoc);
        khoaHoc.getDanhSachSinhVien().add(this); // Maintain bidirectional link
    }

    public void removeKhoaHoc(KhoaHoc khoaHoc) {
        this.danhSachKhoaHoc.remove(khoaHoc);
        khoaHoc.getDanhSachSinhVien().remove(this); // Maintain bidirectional link
    }

    // Getters and Setters...
}



@Entity
public class KhoaHoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenKhoaHoc;

    // Inverse side: Mapped by the field in the owning side
    @ManyToMany(mappedBy = "danhSachKhoaHoc")
    private Set<SinhVien> danhSachSinhVien = new HashSet<>();

     // Helper methods to add/remove students (often mirror those in the owning side)
    public void addSinhVien(SinhVien sinhVien) {
        this.danhSachSinhVien.add(sinhVien);
        sinhVien.getDanhSachKhoaHoc().add(this); // Maintain bidirectional link
    }

    public void removeSinhVien(SinhVien sinhVien) {
        this.danhSachSinhVien.remove(sinhVien);
        sinhVien.getDanhSachKhoaHoc().remove(this); // Maintain bidirectional link
    }


    // Getters and Setters...
}
```

Trong ví dụ này:

  * Chúng ta sử dụng `Set` cho các collection ở cả hai phía để tránh các bản ghi trùng lặp trong bảng trung gian.
  * **SinhVien** được chọn làm **bên sở hữu (owning side)**. Nó định nghĩa bảng trung gian `sinh_vien_khoa_hoc` bằng `@JoinTable`.
      * `name = "sinh_vien_khoa_hoc"`: Tên của bảng trung gian.
      * `joinColumns = @JoinColumn(name = "sinh_vien_id")`: Cột khóa ngoại trong bảng trung gian tham chiếu đến `SinhVien`.
      * `inverseJoinColumns = @JoinColumn(name = "khoa_hoc_id")`: Cột khóa ngoại trong bảng trung gian tham chiếu đến `KhoaHoc`.
  * **KhoaHoc** là **bên đảo ngược (inverse side)**. `mappedBy = "danhSachKhoaHoc"` chỉ ra rằng mối quan hệ được quản lý bởi trường `danhSachKhoaHoc` trong lớp `SinhVien`.
  * `cascade` trên mối quan hệ `@ManyToMany` cần được cân nhắc kỹ lưỡng. `PERSIST` và `MERGE` là phổ biến, nhưng `REMOVE` thường không mong muốn (ví dụ: xóa một `SinhVien` không nên xóa tất cả `KhoaHoc` mà họ đã đăng ký).

**Lưu ý quan trọng:** Khi mối quan hệ Many-to-Many cần chứa thêm dữ liệu (ví dụ: ngày đăng ký khóa học, điểm số), mô hình bảng trung gian đơn thuần sẽ không đủ. Cách tốt hơn là tạo một thực thể riêng cho bảng trung gian (ví dụ: `Enrollment`) và mô hình hóa mối quan hệ Many-to-Many thành hai mối quan hệ One-to-Many: `SinhVien` One-to-Many `Enrollment` và `KhoaHoc` One-to-Many `Enrollment`.

## Các Khía Cạnh Cần Cân Nhắc Khi Dùng JPA

### 1\. Owning Side và Inverse Side

  * Hiểu rõ bên nào là **bên sở hữu** là rất quan trọng. Bên sở hữu là bên quản lý (thêm/xóa/cập nhật) khóa ngoại trong CSDL.
  * Trong mối quan hệ hai chiều, chỉ thao tác trên bên sở hữu mới thực sự ảnh hưởng đến dữ liệu trong CSDL. Bên đảo ngược chỉ là một cách thuận tiện để điều hướng.

### 2\. mappedBy

  * Thuộc tính này chỉ được sử dụng trên **bên đảo ngược** của mối quan hệ hai chiều.
  * Nó trỏ đến tên trường ở bên sở hữu định nghĩa cùng một mối quan hệ.

### 3\. @JoinColumn và @JoinTable

  * **`@JoinColumn`**: Được sử dụng trên bên sở hữu của mối quan hệ One-to-One và Many-to-One để chỉ định tên cột khóa ngoại trong bảng của bên sở hữu.
  * **`@JoinTable`**: Được sử dụng trên bên sở hữu của mối quan hệ Many-to-Many để định nghĩa bảng trung gian và các cột khóa ngoại của nó.

### 4\. Fetch Types (FetchType.LAZY vs FetchType.EAGER)

| Loại Fetch | Mặc Định Cho | Mô Tả | Ghi Chú |
| :--- | :--- | :--- | :--- |
| **`EAGER`** | `@OneToOne`, `@ManyToOne` | Tải dữ liệu của các thực thể liên quan ngay lập tức khi thực thể chính được tải. | Có thể gây lãng phí tài nguyên. |
| **`LAZY`** | `@OneToMany`, `@ManyToMany` | Chỉ tải dữ liệu của các thực thể liên quan khi bạn truy cập trường tương ứng lần đầu tiên. | Tối ưu hiệu suất, nhưng dễ dẫn đến vấn đề N+1 Select. |

**Lời khuyên:** Nên ưu tiên sử dụng `LAZY` và sử dụng các kỹ thuật như *fetch joins* trong JPQL/HQL để tải trước dữ liệu khi cần.

### 5\. Cascade Types (CascadeType)

| Loại Cascade | Thao Tác |
| :--- | :--- |
| **`PERSIST`** | Thao tác persist (lưu) thực thể chính sẽ cascade đến thực thể liên quan. |
| **`MERGE`** | Thao tác merge (cập nhật) thực thể chính sẽ cascade đến thực thể liên quan. |
| **`REMOVE`** | Thao tác remove (xóa) thực thể chính sẽ cascade đến thực thể liên quan. |
| **`ALL`** | Cascade tất cả các thao tác (PERSIST, MERGE, REMOVE, REFRESH, DETACH). |
| **`orphanRemoval = true`** | (Chỉ trên `@OneToMany`, `@OneToOne`) Nếu thực thể con bị gỡ khỏi collection của cha, nó sẽ bị xóa khỏi CSDL. |

Việc cấu hình cascade đúng cách giúp quản lý vòng đời của các thực thể liên quan một cách tự động, nhưng cấu hình sai có thể dẫn đến mất dữ liệu không mong muốn.

### 6\. Bidirectional vs Unidirectional

  * **Unidirectional (Một Chiều)**: Đơn giản hơn để code, nhưng bạn không thể điều hướng ngược lại.
  * **Bidirectional (Hai Chiều)**: Cho phép điều hướng linh hoạt hơn, nhưng đòi hỏi code phức tạp hơn một chút để quản lý tính nhất quán ở cả hai phía (như các helper methods `addKhoaHoc/removeKhoaHoc`).

## Tóm Tắt Các Loại Mối Quan Hệ

| Mối Quan Hệ | Annotation (Bên Sở hữu) | Annotation (Bên Đảo ngược, nếu hai chiều) | Cấu Trúc Bảng CSDL | Vai Trò Khóa Ngoại/Bảng Trung Gian | Ví Dụ Phổ Biến |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **One-to-One (1:1)** | `@OneToOne` (với `@JoinColumn`) | `@OneToOne` (với `mappedBy`) | Một FK trong bảng của bên sở hữu trỏ đến PK của bên kia. | Bên sở hữu chứa FK. | Người $\leftrightarrow$ Thông tin chi tiết cá nhân |
| **One-to-Many (1:N) / Many-to-One (N:1)** | `@ManyToOne` (với `@JoinColumn`) | `@OneToMany` (với `mappedBy`) | Một FK trong bảng “nhiều” (Many side) trỏ đến PK của bảng “một” (One side). | Bên “nhiều” (`@ManyToOne`) chứa FK. | Tác giả $\leftrightarrow$ Sách, Danh mục $\leftrightarrow$ Sản phẩm |
| **Many-to-Many (N:M)** | `@ManyToMany` (với `@JoinTable`) | `@ManyToMany` (với `mappedBy`) | Một bảng trung gian chứa hai FK, mỗi FK trỏ đến PK của một trong hai bảng gốc. | Bên sở hữu định nghĩa bảng trung gian. | Sinh viên $\leftrightarrow$ Khóa học, Sản phẩm $\leftrightarrow$ Thẻ (Tag) |

## II. Derived Query Methods (Truy vấn bằng tên phương thức)
- Spring Data JPA có thể **tự động tạo truy vấn** dựa trên **tên phương thức** bạn định nghĩa
- Bạn không cần viết SQL hay JPQL
- Rất tiện khi truy vấn đơn giản

**Cú Pháp Cơ Bản**
Trong Spring JPA, cơ chế xây dựng truy vấn thông qua tên của method được quy định bởi các tiền tố sau:
```
find/read/query/get/count + ... + By + [Điều kiện]
```
phần còn lại sẽ được parse theo tên của thuộc tính (viết hoa chữ cái đầu). Nếu chúng ta có nhiều điều kiện, thì các thuộc tính có thể kết hợp với nhau bằng chữ `And` hoặc `Or`.

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
Các thuộc tính trong tên method phải thuộc về Class đó, nếu không sẽ gây ra lỗi.

| Từ khóa             | Ví dụ                                                      | Đoạn JPQL tương ứng                                             |
| ------------------- | --------------------------------------------------------- | ------------------------------------------------------------------ |
| `And`               | `findByLastnameAndFirstname`                              | `… where x.lastname = ?1 and x.firstname = ?2`                     |
| `Or`                | `findByLastnameOrFirstname`                               | `… where x.lastname = ?1 or x.firstname = ?2`                      |
| `Is,Equals`         | `findByFirstname,findByFirstnameIs,findByFirstnameEquals` | `… where x.firstname = 1?`                                         |
| `Between`           | `findByStartDateBetween`                                  | `… where x.startDate between 1? and ?2`                            |
| `LessThan`          | `findByAgeLessThan`                                       | `… where x.age < ?1`                                               |
| `LessThanEqual`     | `findByAgeLessThanEqual`                                  | `… where x.age <= ?1`                                              |
| `GreaterThan`       | `findByAgeGreaterThan`                                    | `… where x.age > ?1`                                               |
| `GreaterThanEqual`  | `findByAgeGreaterThanEqual`                               | `… where x.age >= ?1`                                              |
| `After`             | `findByStartDateAfter`                                    | `… where x.startDate > ?1`                                         |
| `Before`            | `findByStartDateBefore`                                   | `… where x.startDate < ?1`                                         |
| `IsNull`            | `findByAgeIsNull`                                         | `… where x.age is null`                                            |
| `IsNotNull,NotNull` | `findByAge(Is)NotNull`                                    | `… where x.age not null`                                           |
| `Like`              | `findByFirstnameLike`                                     | `… where x.firstname like ?1`                                      |
| `NotLike`           | `findByFirstnameNotLike`                                  | `… where x.firstname not like ?1`                                  |
| `StartingWith`      | `findByFirstnameStartingWith`                             | `… where x.firstname like ?1` (tham số được bao bọc bởi `%` ở cuối)  |
| `EndingWith`        | `findByFirstnameEndingWith`                               | `… where x.firstname like ?1` (tham số được bao bọc bởi `%` ở đầu) |
| `Containing`        | `findByFirstnameContaining`                               | `… where x.firstname like ?1` (tham số được bao bọc bởi `%` ở cả hai đầu)     |
| `OrderBy`           | `findByAgeOrderByLastnameDesc`                            | `… where x.age = ?1 order by x.lastname desc`                      |
| `Not`               | `findByLastnameNot`                                       | `… where x.lastname <> ?1`                                         |
| `In`                | `findByAgeIn(Collection<Age> ages)`                       | `… where x.age in ?1`                                              |
| `NotIn`             | `findByAgeNotIn(Collection<Age> age)`                     | `… where x.age not in ?1`                                          |
| `True`              | `findByActiveTrue()`                                      | `… where x.active = true`                                          |
| `False`             | `findByActiveFalse()`                                     | `… where x.active = false`                                         |
| `IgnoreCase`        | `findByFirstnameIgnoreCase`                               | `… where UPPER(x.firstame) = UPPER(?1)`                            |

## III. JPQL với @Query
**JPQL (Java Persistence Query Language)**
- Là ngôn ngữ truy vấn hướng **đối tượng** (Object-Oriented)
- Dùng khi truy vấn phức tạp không thể viết bằng Derived Query
- Annotation `@Query` cho phép sử dụng JPQL trên các phương thức.
- Các thao tác được thực hiện đối với tên đối tượng và tên thuộc tính đối tượng, chứ không phải tên bảng và tên trường trong cơ sở dữ liệu.

```java
@Query("select u from User u where u.name=?1 and u.department.id=?2")
public User findUser(String name, Integer departmentId);
```

```java
@Query("from User u where u.name=?1 and u.department.id=?2")
public User findUser(String name, Integer departmentId);
```

Khi truy vấn, bạn có thể sử dụng `Pageable` và `Sort` để hoàn thành việc phân trang và sắp xếp.

```java
@Query("select u from User u where department.id=?1")
public Page<User> QueryUsers(Integer departmentId, Pageable page);
```
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
## IV. Native Query (SQL thuần)
**Khi nào dùng Native Query?**
- Khi JPQL không đủ linh hoạt
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
## V. Truy vấn sử dụng EntityManager
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