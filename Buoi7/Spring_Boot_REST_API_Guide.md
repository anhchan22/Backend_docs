# Hướng Dẫn Toàn Diện: Triển Khai RESTful API với Spring Boot

## I. Cấu Trúc Dự Án Spring Boot

### 1.1 Giới Thiệu Cấu Trúc 4 Lớp

Một ứng dụng Spring Boot được chia thành **4 lớp chính** để tổ chức code một cách khoa học và dễ bảo trì:

```
┌─────────────────────────────────────┐
│   Presentation Layer (Controller)   │  <- Tiếp nhận request từ client
├─────────────────────────────────────┤
│   Business Logic Layer (Service)    │  <- Xử lý logic nghiệp vụ
├─────────────────────────────────────┤
│   Data Access Layer (Repository)    │  <- Truy cập database
├─────────────────────────────────────┤
│   Database Layer                    │  <- Lưu trữ dữ liệu
└─────────────────────────────────────┘
```

### 1.2 Chi Tiết Từng Lớp

**Lớp Model/Entity (Tầng Dữ Liệu)**
- Định nghĩa cấu trúc dữ liệu của đối tượng
- Ánh xạ trực tiếp tới bảng trong database
- Sử dụng annotation @Entity và @Table

**Lớp Controller (Tầng Presentation)**
- Tiếp nhận request HTTP từ client (GET, POST, PUT, DELETE)
- Gọi Service để xử lý logic
- Trả về response cho client (thường là JSON)
- Sử dụng @RestController

**Lớp Service (Tầng Business Logic)**
- Chứa toàn bộ logic nghiệp vụ của ứng dụng
- Kết nối giữa Controller và Repository
- Xử lý validation, kiểm tra, tính toán
- Sử dụng @Service

**Lớp Repository (Tầng Data Access)**
- Giao tiếp trực tiếp với cơ sở dữ liệu
- Cung cấp các phương thức CRUD (Create, Read, Update, Delete)
- Sử dụng Spring Data JPA
- Sử dụng @Repository

---

## II. JPA và Hibernate

### 2.1 JPA (Java Persistence API) Là Gì?

**JPA** (Java Persistence API) là một **tiêu chuẩn/specification** định nghĩa cách ánh xạ các object Java thành cơ sở dữ liệu quan hệ. 

JPA giải quyết vấn đề "Object-Relational Impedance" - sự khác biệt giữa thế giới hướng đối tượng và thế giới cơ sở dữ liệu quan hệ.

**Ví dụ:** Trong Java có Class `Student` với các thuộc tính `id`, `name`, `age`. JPA định nghĩa cách để tự động ánh xạ class này thành bảng `STUDENT` trong database với các cột tương ứng.

### 2.2 Hibernate Là Gì?

**Hibernate** là một **implementation/triển khai** của JPA. Nó là cách "thực hiện" những gì JPA định nghĩa.

**Sự khác biệt:**
- **JPA** = Bản thiết kế xây dựng cây cầu
- **Hibernate** = Công ty xây dựng thực tế theo bản thiết kế

**Tính năng chính của Hibernate:**
- Tự động sinh câu lệnh SQL từ code Java
- Tự động ánh xạ dữ liệu database thành Java object (ORM)
- Quản lý session và transaction
- Hỗ trợ cache

### 2.3 Spring Data JPA

**Spring Data JPA** là một tầng trừu tượng cao hơn, giúp đơn giản hóa việc sử dụng JPA và Hibernate trong Spring Boot.

```
┌──────────────────────┐
│  Spring Data JPA     │  <- Cung cấp Repository interface
├──────────────────────┤
│     Hibernate        │  <- Triển khai JPA, sinh SQL
├──────────────────────┤
│   JPA Specification  │  <- Định nghĩa tiêu chuẩn
└──────────────────────┘
```

---

## III. Kiến Trúc 4 Lớp Spring Boot Chi Tiết

### 3.1 Sơ Đồ Request/Response Flow

```
[Client] --HTTP Request--> [DispatcherServlet]
                                  |
                                  v
                          [Controller]
                                  |
                                  v
                          [Service]
                                  |
                                  v
                          [Repository]
                                  |
                                  v
                          [Database]
                                  |
                                  v
                          [Data] --HTTP Response--> [Client]
```

### 3.2 Ví Dụ Minh Họa Cụ Thể

Giả sử bạn muốn tạo API để quản lý sinh viên.

**Entity/Model:**
```java
@Entity
@Table(name = "student")
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String email;
    private int age;
    
    // Getters, Setters
}
```

**Repository:**
```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    List<Student> findByName(String name);
    
    @Query("SELECT s FROM Student s WHERE s.age > :age")
    List<Student> findStudentsOlderThan(@Param("age") int age);
}
```

**Service:**
```java
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }
    
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }
    
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    public void deleteStudent(Long id) {
        studentRepository.deleteById(id);
    }
}
```

**Controller:**
```java
@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired
    private StudentService studentService;
    
    @GetMapping
    public List<Student> getAllStudents() {
        return studentService.getAllStudents();
    }
    
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }
    
    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.saveStudent(student);
    }
    
    @DeleteMapping("/{id}")
    public void deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
    }
}
```

---

## IV. Luồng Xử Lý Request (Request Flow)

### 4.1 Các Bước Chi Tiết

**Bước 1: Client Gửi Request**
```
GET /api/students/1
```

**Bước 2: DispatcherServlet Tiếp Nhận**
- DispatcherServlet là một servlet trung tâm của Spring MVC
- Tiếp nhận tất cả các request từ client

**Bước 3: Tìm Kiếm Handler (Controller + Method)**
- Spring tìm Controller nào match với URL `/api/students/1`
- Tìm method nào được map bằng @GetMapping("/{id}")

**Bước 4: Controller Xử Lý**
```java
@GetMapping("/{id}")
public Student getStudentById(@PathVariable Long id) {
    // id = 1
    return studentService.getStudentById(id);
}
```

**Bước 5: Gọi Service**
- Controller gọi `studentService.getStudentById(1)`
- Service xử lý logic

**Bước 6: Repository Truy Vấn Database**
```java
public Student getStudentById(Long id) {
    return studentRepository.findById(id).orElse(null);
    // Hibernate tự sinh câu SQL: SELECT * FROM student WHERE id = 1
}
```

**Bước 7: Trả Dữ Liệu Ngược**
- Database trả về dữ liệu
- Repository chuyển đổi thành Student object
- Service trả về cho Controller
- Controller trả về cho client dưới dạng JSON

**Response JSON:**
```json
{
    "id": 1,
    "name": "Nguyễn Văn A",
    "email": "nguyenvana@example.com",
    "age": 20
}
```

### 4.2 Biểu Đồ Luồng Chi Tiết

```
┌────────────┐
│  Client    │
└────────┬───┘
         │ HTTP Request: GET /api/students/1
         v
┌────────────────────┐
│ DispatcherServlet  │
└────────┬───────────┘
         │ Tìm Controller match
         v
┌────────────────────┐
│ StudentController  │
│ .getStudentById(1) │
└────────┬───────────┘
         │ Gọi Service
         v
┌────────────────────┐
│ StudentService     │
│ .getStudentById(1) │
└────────┬───────────┘
         │ Gọi Repository
         v
┌────────────────────┐
│ StudentRepository  │
│ .findById(1)       │
└────────┬───────────┘
         │ Sinh SQL qua Hibernate
         v
┌────────────────────┐
│    Database        │
│ SELECT * FROM ...  │
└────────┬───────────┘
         │ Trả dữ liệu
         v
┌────────────────────┐
│ HTTP Response      │
│ JSON Student       │
└────────┬───────────┘
         │
         v
┌────────────┐
│  Client    │
└────────────┘
```

---

## V. Cấu Hình DataSource

### 5.1 DataSource Là Gì?

**DataSource** là một đối tượng quản lý kết nối với cơ sở dữ liệu. Nó cung cấp một pool (nhóm) các kết nối có sẵn để ứng dụng sử dụng.

### 5.2 Cấu Hình Trong application.properties

```properties
# Cấu hình kết nối MySQL
spring.datasource.url=jdbc:mysql://localhost:3306/my_database
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Cấu hình JPA/Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect

# Cấu hình HikariCP Connection Pool
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=5
```

### 5.3 Giải Thích Từng Cấu Hình

| Cấu Hình | Ý Nghĩa |
|---------|---------|
| `spring.datasource.url` | Địa chỉ database (localhost:3306) và tên database (my_database) |
| `spring.datasource.username` | Tên user để đăng nhập database |
| `spring.datasource.password` | Mật khẩu đăng nhập database |
| `spring.datasource.driver-class-name` | Driver JDBC cho MySQL |
| `spring.jpa.hibernate.ddl-auto` | update: Tự động cập nhật schema; create: tạo mới; validate: kiểm tra |
| `spring.jpa.show-sql` | true: hiển thị SQL trong console log |
| `spring.jpa.properties.hibernate.dialect` | Xác định loại database để Hibernate sinh SQL phù hợp |

### 5.4 Cấu Hình Với H2 Database (In-Memory)

Đây là database nhẹ, thích hợp cho development và testing:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
spring.jpa.hibernate.ddl-auto=create-drop
```

---

## VI. Các Annotation Quan Trọng

### 6.1 Annotation Cho Entity

#### @Entity
Đánh dấu class này là một JPA entity, có thể được lưu trữ trong database.

```java
@Entity
public class Student {
    // ...
}
```

#### @Table
Chỉ định tên bảng trong database. Nếu không dùng, Spring sẽ dùng tên class.

```java
@Entity
@Table(name = "student")
public class Student {
    // ...
}
```

**Ví dụ với constraint:**
```java
@Entity
@Table(
    name = "student",
    uniqueConstraints = @UniqueConstraint(columnNames = "email")
)
public class Student {
    // email phải unique (không được trùng)
}
```

#### @Id
Đánh dấu field này là primary key (khóa chính) của bảng.

```java
@Entity
public class Student {
    @Id
    private Long id;
}
```

#### @GeneratedValue
Chỉ định cách tự động sinh giá trị cho @Id.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Strategy = IDENTITY: MySQL tự động tăng id
}
```

**Các strategy khác:**
- `GenerationType.AUTO`: Spring tự chọn strategy phù hợp
- `GenerationType.SEQUENCE`: Dùng database sequence (PostgreSQL, Oracle)
- `GenerationType.TABLE`: Dùng bảng riêng để quản lý ID

#### @Column
Chỉ định thông tin chi tiết của column trong database.

```java
@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "student_name", nullable = false, length = 100)
    private String name;
    
    @Column(unique = true)
    private String email;
}
```

**Các attribute:**
- `name`: Tên column trong database
- `nullable`: Cho phép null hay không (false = bắt buộc)
- `length`: Độ dài tối đa của string
- `unique`: Giá trị phải unique

### 6.2 Annotation Cho Repository

#### @Repository
Đánh dấu interface/class này là một data repository, là một Spring bean.

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}
```

**Ưu điểm:**
- Được Spring quản lý như một bean
- Hỗ trợ exception translation (chuyển đổi ngoại lệ database)

### 6.3 Annotation Cho Service

#### @Service
Đánh dấu class này là service layer, chứa business logic.

```java
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
}
```

#### @Transactional
Đánh dấu method hoặc class là transactional. Toàn bộ operation trong method phải thành công, nếu lỗi sẽ rollback.

```java
@Service
public class StudentService {
    @Transactional
    public void updateStudentEmail(Long id, String newEmail) {
        Student student = studentRepository.findById(id).orElse(null);
        if (student != null) {
            student.setEmail(newEmail);
            studentRepository.save(student); // Nếu lỗi, rollback
        }
    }
}
```

**Attribute:**
- `readOnly = true`: Chỉ đọc, không modify dữ liệu
- `timeout`: Timeout transaction (tính bằng giây)
- `rollbackFor`: Exception nào thì rollback

**Ví dụ:**
```java
@Transactional(readOnly = true)
public Student getStudentById(Long id) {
    return studentRepository.findById(id).orElse(null);
}
```

#### @Query
Định nghĩa custom query (JPQL hoặc native SQL) trực tiếp trên Repository.

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    // JPQL query
    @Query("SELECT s FROM Student s WHERE s.age > :age")
    List<Student> findStudentsOlderThan(@Param("age") int age);
    
    // Native SQL query
    @Query(value = "SELECT * FROM student WHERE name LIKE %:name%", nativeQuery = true)
    List<Student> searchByName(@Param("name") String name);
}
```

**Giải thích:**
- `FROM Student s` - Student là tên entity class, không phải tên bảng
- `:age` - Named parameter, được gán value từ `@Param("age")`
- `nativeQuery = true` - Dùng native SQL thay vì JPQL

### 6.4 Annotation Cho Controller

#### @RestController
Đánh dấu class này là REST controller. Kết hợp của @Controller + @ResponseBody.

```java
@RestController
public class StudentController {
    // Tất cả return value tự động convert thành JSON
}
```

#### @RequestMapping
Map URL path tới method hoặc class.

```java
@RestController
@RequestMapping("/api/students")
public class StudentController {
    // Tất cả method sẽ có prefix /api/students
    
    @RequestMapping(method = RequestMethod.GET)
    public List<Student> getAllStudents() {}
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Student getStudentById(@PathVariable Long id) {}
}
```

#### @GetMapping
Shortcut cho @RequestMapping(method = RequestMethod.GET). Dùng để xử lý GET request.

```java
@RestController
@RequestMapping("/api/students")
public class StudentController {
    @GetMapping
    public List<Student> getAllStudents() {
        // GET /api/students
    }
    
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        // GET /api/students/1
    }
}
```

#### @PostMapping
Shortcut cho @RequestMapping(method = RequestMethod.POST). Dùng để xử lý POST request.

```java
@PostMapping
public Student createStudent(@RequestBody Student student) {
    // POST /api/students
    // Body: { "name": "...", "email": "..." }
}
```

#### @PutMapping
Shortcut cho @RequestMapping(method = RequestMethod.PUT). Dùng để xử lý PUT request (update).

```java
@PutMapping("/{id}")
public Student updateStudent(
    @PathVariable Long id, 
    @RequestBody Student student
) {
    // PUT /api/students/1
}
```

#### @DeleteMapping
Shortcut cho @RequestMapping(method = RequestMethod.DELETE). Dùng để xử lý DELETE request.

```java
@DeleteMapping("/{id}")
public void deleteStudent(@PathVariable Long id) {
    // DELETE /api/students/1
}
```

#### @PathVariable
Trích xuất giá trị từ URL path.

```java
@GetMapping("/{id}/courses/{courseId}")
public Course getStudentCourse(
    @PathVariable Long id,
    @PathVariable Long courseId
) {
    // GET /api/students/5/courses/10
    // id = 5, courseId = 10
}
```

#### @RequestBody
Bind dữ liệu từ request body (thường là JSON) thành Java object.

```java
@PostMapping
public Student createStudent(@RequestBody Student student) {
    // Nhận JSON từ request body, chuyển thành Student object
    // { "name": "Nguyễn Văn A", "email": "a@gmail.com", "age": 20 }
}
```

#### @RequestParam
Trích xuất query parameter từ URL.

```java
@GetMapping("/search")
public List<Student> searchStudents(
    @RequestParam String name,
    @RequestParam(defaultValue = "0") int age
) {
    // GET /api/students/search?name=Nguyễn&age=20
    // name = "Nguyễn", age = 20
}
```

#### @Autowired
Tự động inject bean (dependency injection). Spring sẽ tìm và gán bean phù hợp.

```java
@Service
public class StudentService {
    @Autowired
    private StudentRepository studentRepository;
    
    // hoặc dùng constructor injection (khuyên dùng)
    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }
}
```