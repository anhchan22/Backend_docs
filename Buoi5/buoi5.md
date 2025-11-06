Xin chào mọi người, mình là thành viên của team Web thuộc Câu lạc bộ Lập trình PTIT (ProPTIT). Đây là bài viết mở đầu cho chuỗi bài học về Spring Boot, bắt đầu với những kiến thức nền tảng nhất. Hi vọng tài liệu này sẽ giúp ích cho các bạn trong quá trình học tập.

***Lưu ý:** Đây là tài liệu do các bạn sinh viên trong CLB biên soạn trong quá trình học tập, vì vậy không thể tránh khỏi những sai sót. Nếu bạn có bất kỳ góp ý nào về nội dung, vui lòng gửi tin nhắn cho fanpage của CLB để chúng mình có thể cải thiện nhé. Cảm ơn các bạn!*

---

# [BUỔI 5] CÁC KIẾN THỨC CƠ BẢN


## I. HTTP là gì ?
 - **HTTP (HyperText Transfer Protocol)** là một giao thức truyền tải siêu văn bản được thiết kế để cho phép giao tiếp giữa các máy khách (client) và máy chủ web (server). HTTP hoạt động theo mô hình yêu cầu - phản hồi (request-response protocol): máy khách gửi một yêu cầu HTTP đến máy chủ, sau đó máy chủ xử lý yêu cầu đó và gửi lại một phản hồi HTTP cho máy khách.
 - Các đặc điểm quan trọng của HTTP:
     - **Stateless (Không có trạng thái)**: Mỗi yêu cầu từ máy khách đến máy chủ phải chứa tất cả thông tin cần thiết để máy chủ xử lý, máy chủ không lưu trữ thông tin về các yêu cầu trước đó.
     - **Lightweight (Nhẹ nhàng)**: HTTP là một giao thức ứng dụng lớp (application layer protocol) nhẹ, được xây dựng trên TCP/IP, giúp tối ưu hóa truyền dữ liệu trên mạng.
     - **Textual Protocol**: Các thông điệp HTTP dễ đọc và dễ hiểu vì chúng được biểu diễn dưới dạng văn bản.
### 1. Các method trong HTTP
HTTP cung cấp nhiều phương thức (methods) khác nhau để thực hiện các thao tác với tài nguyên trên máy chủ. Dưới đây là những phương thức phổ biến nhất:
 | Method | Chức năng chính | Đặc điểm | Ví dụ |
| :--- | :--- | :--- | :--- |
| **GET** | Lấy dữ liệu từ một tài nguyên. | - **An toàn** (không thay đổi dữ liệu).<br>- Có thể được **cache**.<br>- Tham số gửi qua **URL**.<br>- Có giới hạn độ dài. | `GET /api/users/123` |
| **POST** | Tạo mới hoặc cập nhật tài nguyên. | - **Không an toàn** (thay đổi dữ liệu).<br>- **Không idempotent** (gọi nhiều lần có thể tạo nhiều tài nguyên).<br>- Dữ liệu gửi trong **body**.<br>- Không giới hạn độ dài. | `POST /api/users` |
| **PUT** | Cập nhật/thay thế toàn bộ tài nguyên. | - **Idempotent** (gọi nhiều lần cho kết quả như nhau).<br>- Dữ liệu gửi trong **body**. | `PUT /api/users/123` |
| **DELETE** | Xóa một tài nguyên. | - **Idempotent** (gọi nhiều lần cho kết quả như nhau). | `DELETE /api/users/123` |
| **PATCH** | Cập nhật một phần của tài nguyên. | - Cập nhật **một phần**, không cần gửi toàn bộ.<br>- Dữ liệu gửi trong **body**. | `PATCH /api/users/123` |
| **HEAD** | Lấy metadata (thông tin tiêu đề) của tài nguyên. | - Tương tự GET nhưng chỉ trả về **headers**, không có body. | `HEAD /api/users/123` |
### 2. Response là gì, Request là gì ?
#### HTTP Request (Yêu cầu HTTP)
HTTP Request là một thông điệp mà máy khách gửi đến máy chủ để yêu cầu thực hiện một hành động (lấy dữ liệu, tạo mới, cập nhật, xóa,...). HTTP Request bao gồm các thành phần sau:​
- **Request Line** (Dòng yêu cầu): Gồm 3 thành phần:
    - Method: phương thức HTTP (GET, POST, v.v.)
    - Request-URI: Đường dẫn tài nguyên
    - HTTP version
    - ví dụ: 

    ```java
    GET /users/123 HTTP/1.1
    ```

- **Headers** (Các tiêu đề): Cung cấp thông tin bổ sung về yêu cầu, chẳng hạn như Host, Content-Type, Authorization, v.v.
  - Ví dụ:
  ``` java 
    Host: api.example.com
    Content-Type: application/json
    Authorization: Bearer abc123
  ```
- **Message Body** (Thân thông điệp): Chứa dữ liệu gửi đến máy chủ (thường được sử dụng với POST, PUT, PATCH). Thân của GET request thường trống.​
    - Ví dụ:
  ``` java
    Khi tạo user mới cần truyền vào body như sau:

    {
        "name": "Nguyễn Văn A",
        "age": 22
    }

  ```
- **Ví dụ đầy đủ về HTTP Request:**
    ``` java
    POST /api/users HTTP/1.1
    Host: api.example.com
    Content-Type: application/json
    Authorization: Bearer abc123

    {
        "name": "Nguyễn Văn A",
        "age": 22
    }

    ```

#### HTTP Response (Phản hồi HTTP)
HTTP Response là thông điệp mà máy chủ gửi lại cho máy khách sau khi xử lý request. HTTP Response bao gồm:​

- **Status Line** (Dòng trạng thái): Gồm 3 thành phần:
    - HTTP Version
    - Status Code Mã trạng thái
    - Reason Phrase: Mô tả trạng thái
    - Ví dụ:
    ```java
    HTTP/1.1 201 Created
    ```
- **Response Headers**: Cung cấp thông tin về phản hồi, chẳng hạn như Content-Type, Content-Length, Cache-Control, v.v..​
    - Ví dụ:
    ``` java
    Content-Type: application/json
    Location: /api/users/123
    ```
- **Message Body**: Chứa dữ liệu thực tế mà máy chủ trả về, chẳng hạn như trang HTML, dữ liệu JSON, hình ảnh, v.v...​ Tùy vào request và kết quả xử lí
    - Ví dụ:
    ``` java
    Trả về thông tin user vừa tạo:

    {
        "id": 13,
        "name": "Nguyễn Văn A",
        "age": 22
    }
    ```

- **Ví dụ đầy đủ về HTTP Response:**
    ``` java
    HTTP/1.1 201 Created
    Content-Type: application/json
    Location: /api/users/123

    {
        "id": 13,
        "name": "Nguyễn Văn A",
        "age": 22
    }

    ```
##### HTTP Status Codes (Mã trạng thái HTTP)
Mã trạng thái là số có 3 chữ số cho biết kết quả của yêu cầu:

| Mã  | Ý nghĩa                         | Ví dụ                             |
|-----|----------------------------------|------------------------------------|
| 1xx | Informational (Thông tin)        | 100 Continue                       |
| 2xx | Success (Thành công)             | 200 OK, 201 Created                |
| 3xx | Redirection (Chuyển hướng)       | 301 Moved Permanently, 302 Found   |
| 4xx | Client Error (Lỗi phía khách)    | 404 Not Found, 400 Bad Request     |
| 5xx | Server Error (Lỗi phía máy chủ)  | 500 Internal Server Error          |

## IV. API là gì, RestAPI là gì ?
### API (Application Programming Interface)
API là một giao diện lập trình ứng dụng - một tập hợp các quy tắc và định nghĩa cho phép các ứng dụng khác nhau giao tiếp và trao đổi dữ liệu với nhau. Nói cách khác, API là một hợp đồng giữa nhà cung cấp thông tin (máy chủ) và người sử dụng thông tin (máy khách), xác định dữ liệu nào sẽ được yêu cầu và dữ liệu nào sẽ được cung cấp.​

API hoạt động như một trung gian giữa các ứng dụng và cho phép các nhà phát triển:
- Truy cập các tài nguyên từ một ứng dụng hoặc dịch vụ khác
- Chia sẻ dữ liệu một cách an toàn
- Duy trì kiểm soát quyền truy cập

***Ví dụ:***
```Hãy tưởng tượng bạn đang ngồi tại một bàn trong nhà hàng với thực đơn đặt hàng. Nhà bếp là hệ thống chính, nhưng để bạn có thể liên hệ với nhà bếp và nhận được món ăn, bạn cần một người phục vụ. Người phục vụ này chính là một lớp API—là trung gian giữa bạn (client) và nhà bếp (server). Bạn gửi yêu cầu đến người phục vụ, người phục vụ mang đơn đến nhà bếp, và sau khi hoàn thành, người phục vụ mang món ăn lại cho bạn.```

### REST API (RESTful API)
REST API (hay RESTful API hoặc RESTful Web Service) là kiểu kiến trúc của API sử dụng HTTP Request để truy cập và sử dụng dữ liệu. Các dịch vụ Web sử dụng kiến trúc REST được gọi là REST API hoặc RESTful Web Service.

Sự khác biệt chính giữa API thông thường và REST API là REST API tuân theo các nguyên tắc và chuẩn mực cụ thể để đảm bảo tính nhất quán, hiệu quả và khả năng mở rộng.

**REST API được xây dựng dựa trên các nguyên tắc kiến trúc sau:**

1.  **Client-Server (Máy Khách - Máy Chủ)**
    Mô hình này phân chia rõ ràng giữa client (người gửi yêu cầu) và server (người xử lý yêu cầu). Client chịu trách nhiệm về giao diện người dùng, trong khi server quản lý dữ liệu và logic nghiệp vụ.

2.  **Stateless (Không Trạng Thái)**
    Mỗi yêu cầu từ client phải chứa đầy đủ thông tin để server xử lý. Server không lưu trữ trạng thái của client giữa các yêu cầu, giúp hệ thống dễ mở rộng.

3.  **Cacheable (Có Thể Lưu Bộ Nhớ Đệm)**
    Phản hồi từ server phải cho biết liệu nó có thể được cache hay không. Điều này giúp giảm tải cho server và tăng tốc độ cho client.

4.  **Uniform Interface (Giao Diện Đồng Nhất)**
    Tài nguyên được truy cập qua URL duy nhất và biểu diễn bằng định dạng chuẩn (JSON, XML), giúp đơn giản hóa kiến trúc.

5.  **Layered System (Hệ Thống Phân Lớp)**
    Kiến trúc có thể gồm nhiều lớp trung gian (bảo mật, cân bằng tải) giữa client và server, giúp tăng cường bảo mật và khả năng mở rộng.

6.  **Code on Demand (Mã Theo Yêu Cầu - Tùy chọn)**
    Server có thể cung cấp mã thực thi (ví dụ: JavaScript) cho client để tối ưu hóa hiệu suất.

**Cách hoạt động của REST API**

Luồng hoạt động của một REST API diễn ra theo các bước sau:

1.  **Client Gửi Yêu Cầu (Request):** Client tạo một yêu cầu HTTP đến một `endpoint` (URL) cụ thể trên server. Yêu cầu này bao gồm:
    *   **Phương thức HTTP:** `GET`, `POST`, `PUT`, `DELETE`,... để xác định hành động.
    *   **Headers:** Chứa thông tin bổ sung như token xác thực.
    *   **Body (tùy chọn):** Dữ liệu gửi đi, thường dùng với `POST` hoặc `PUT`.

    ***Ví dụ: Client gửi yêu cầu tạo người dùng mới***
    ```http
    POST /api/users HTTP/1.1
    Host: api.example.com
    Content-Type: application/json

    {
      "name": "Nguyễn Văn B",
      "email": "b.nguyen@example.com"
    }
    ```

2.  **Server Xử Lý:** Server nhận yêu cầu, xác thực (nếu cần), và thực hiện logic nghiệp vụ tương ứng (ví dụ: lưu người dùng mới vào cơ sở dữ liệu).

3.  **Server Gửi Phản Hồi (Response):** Sau khi xử lý, server gửi lại một phản hồi HTTP cho client, bao gồm:
    *   **Mã trạng thái HTTP:** Cho biết kết quả (ví dụ: `201 Created`).
    *   **Body:** Dữ liệu trả về, thường ở định dạng JSON.

    ***Ví dụ: Server phản hồi sau khi tạo người dùng thành công***
    ```http
    HTTP/1.1 201 Created
    Content-Type: application/json
    Location: /api/users/124

    {
      "id": 124,
      "name": "Nguyễn Văn B",
      "email": "b.nguyen@example.com"
    }
    ```

4.  **Client Nhận và Xử Lý:** Client nhận phản hồi và sử dụng dữ liệu trả về cho các mục đích của ứng dụng (ví dụ: hiển thị thông báo "Tạo thành công" và cập nhật danh sách người dùng).

#### Quy tắc viết tên API
**1. Dùng danh từ, dạng số nhiều**
    - Đặt tên theo **tài nguyên (resource)** và dùng **danh từ số nhiều**:

| Mô tả             | Method & URL             | Ghi chú                       |
|-------------------|--------------------------|-------------------------------|
| Lấy tất cả user   | `GET /users`             | Dùng GET cho danh sách        |
| Lấy 1 user cụ thể | `GET /users/{id}`        | Ví dụ: `/users/123`           |
| Tạo user mới      | `POST /users`            | Body chứa thông tin user      |
| Cập nhật user     | `PUT /users/{id}`        | PUT cập nhật toàn bộ          |
| Xóa user          | `DELETE /users/{id}`     | Xoá theo ID                   |

**2. Không dùng động từ trong URL**
  - Sai: `/getUsers`, `/createUser`
  - Đúng: `/users` (kết hợp với method GET/POST/PUT/DELETE)
  
**3. Dùng quy tắc lồng tài nguyên (Nested Resources)**
    - Áp dụng cho mối quan hệ cha – con:

| Mô tả                     | Method & URL                             |
|---------------------------|------------------------------------------|
| Lấy tất cả comment của post | `GET /posts/{postId}/comments`         |
| Lấy comment cụ thể         | `GET /posts/{postId}/comments/{id}`     |

**4. Dùng lowercase, ngăn cách bằng dấu `-` hoặc không dấu**

| Đúng       | Sai         |
|--------------|----------------|
| `/user-info` | `/user_info`   |
| `/order-items` | `/orderItems` |

**5. Lọc, tìm kiếm, phân trang bằng query params**

| Tác vụ                     | Ví dụ URL                                          |
|----------------------------|----------------------------------------------------|
| Lọc theo tên               | `/users?name=long`                                 |
| Phân trang                 | `/products?page=2&limit=10`                        |
| Sắp xếp theo ngày          | `/posts?sort=createdAt&order=desc`                 |
| Tìm kiếm                   | `/search?q=điện thoại`                             |

**6. Tránh viết tắt mơ hồ**

| Sai    | Đúng     |
|----------|-------------|
| `/usr`   | `/users`    |
| `/prds`  | `/products` |

**7. Phiên bản API**
    - Đặt version ở phần đầu URL:
    
```java
GET /api/v1/users
POST /api/v2/products
```

**Ưu điểm của REST API**
- Đơn giản và dễ hiểu: REST sử dụng các HTTP methods tiêu chuẩn​
- Dễ scale: Có thể dễ dàng thêm hoặc bớt tài nguyên máy chủ​
- Linh hoạt: Hỗ trợ nhiều định dạng dữ liệu khác nhau (JSON, XML, v.v.)​
- Không yêu cầu công nghệ cụ thể: Có thể xây dựng bằng bất kỳ ngôn ngữ lập trình nào
## V. Design pattern : DI, IOC
### Dependency Injection (DI) - Mô hình tiêm phụ thuộc
**DI là gì?**
Dependency Injection (DI) là một design pattern (mô hình thiết kế) cho phép một lớp nhận các phụ thuộc từ bên ngoài thay vì tự tạo ra chúng. Điều này giúp tách rời các thành phần của ứng dụng, làm cho code trở nên dễ kiểm tra (testable), dễ bảo trì và dễ mở rộng.
- Ví dụ không sử dụng DI
```java
// Lớp Engine
class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

// Lớp Car tự tạo phụ thuộc Engine
class Car {
    private Engine engine;
    
    public Car() {
        // Car tạo Engine - TẦM LIÊN KẾT CAO
        this.engine = new Engine();
    }
    
    public void drive() {
        engine.start();
        System.out.println("Car is driving");
    }
}
```
=> Vấn đề: Lớp Car phụ thuộc trực tiếp vào lớp Engine. Nếu muốn thay đổi Engine thành ElectricEngine, phải sửa code trong Car.

- Ví dụ sử dụng DI - Constructor Injection
```java
// Lớp Engine
class Engine {
    public void start() {
        System.out.println("Engine started");
    }
}

// Lớp Car nhận Engine từ bên ngoài
class Car {
    private Engine engine;
    
    // Constructor Injection: Phụ thuộc được truyền vào
    public Car(Engine engine) {
        this.engine = engine;
    }
    
    public void drive() {
        engine.start();
        System.out.println("Car is driving");
    }
}

public class Main {
    public static void main(String[] args) {
        Engine engine = new Engine();
        // Tiêm phụ thuộc Engine vào Car
        Car car = new Car(engine);
        car.drive();
    }
}
```
**Các loại Dependency Injection**

Spring cung cấp ba loại DI chính (dưới đây có ví dụ minh họa bằng Java):

**1. Constructor Injection (Tiêm qua Constructor)**
Phụ thuộc được cung cấp thông qua constructor của lớp. Đây là cách tốt nhất cho các phụ thuộc bắt buộc.

```java
public class MyService {
    private final MyRepository myRepository;

    public MyService(MyRepository myRepository) {
        this.myRepository = myRepository;
    }
}
```

**2. Setter Injection (Tiêm qua Setter)**
Phụ thuộc được thiết lập thông qua các phương thức setter. Cách này hữu ích cho các phụ thuộc tùy chọn.

```java
public class MyService {
    private MyRepository myRepository;

    public void setMyRepository(MyRepository myRepository) {
        this.myRepository = myRepository;
    }
}
```

**3. Interface Injection**
Với phương pháp này, một lớp sẽ implement một interface. Interface này chứa một phương thức để "tiêm" phụ thuộc vào. IoC container sẽ gọi phương thức này để cung cấp đối tượng phụ thuộc.


- **Ví dụ:**
  ```java
  // 1. Định nghĩa interface để tiêm phụ thuộc
  public interface InjectableRepository {
      void injectRepository(MyRepository repository);
  }

  // 2. Lớp dịch vụ implement interface trên
  public class MyService implements InjectableRepository {
      private MyRepository myRepository;

      // Container sẽ gọi phương thức này để tiêm dependency
      @Override
      public void injectRepository(MyRepository repository) {
          this.myRepository = repository;
      }
  }
  ```

Ghi chú: Ngoài các kiểu trên, trong tài liệu hoặc các tình huống đặc biệt bạn cũng có thể gặp các dạng khác như Method Parameter Injection; tuy nhiên trong Spring thực tế, Constructor và Setter là phổ biến nhất.

**Lợi ích của Dependency Injection**
- Tách rời các thành phần (Loose Coupling): Các lớp không phụ thuộc trực tiếp vào nhau, mà phụ thuộc vào các interface.​
- Dễ kiểm tra (Testability): Có thể dễ dàng tạo các đối tượng giả (mock objects) để kiểm tra.​
- Dễ bảo trì và mở rộng (Maintainability): Có thể thay đổi các triển khai mà không cần sửa code sử dụng chúng.​
- Tái sử dụng code (Reusability): Các lớp có thể được sử dụng lại trong các ngữ cảnh khác nhau.

### Inversion of Control (IoC) - Đảo ngược quyền kiểm soát
**IoC là gì?**
Inversion of Control (IoC) là một nguyên tắc thiết kế (design principle) trong đó quyền kiểm soát luồng thực thi của chương trình được chuyển giao cho một framework hoặc container bên ngoài, thay vì do code của nhà phát triển quản lý.​

Nói cách khác, thay vì nhà phát triển gọi các thư viện, framework gọi code của nhà phát triển ("Don't call us, we'll call you").​

**Sự khác biệt giữa lập trình truyền thống và IoC**
- Lập trình truyền thống: Đoạn code của bạn gọi các thư viện để hoàn thành các tác vụ.
- Với IoC: Framework gọi đoạn code của bạn tại thời điểm thích hợp. Framework quản lý luồng thực thi.

Ví dụ: Trong một ứng dụng web truyền thống, code của bạn phải tự gọi bộ xử lý sự kiện. Với IoC framework (như Spring hoặc Flask), framework tự gọi handler của bạn khi có sự kiện xảy ra.

**Mối quan hệ giữa IoC và DI**
- IoC là một nguyên tắc tổng quát, còn DI là một kỹ thuật cụ thể để thực hiện IoC. DI là cách phổ biến nhất để đạt được IoC.​

Cụ thể:
- IoC Container (Vùng chứa IoC): Là một framework component quản lý việc tạo, cấu hình và quản lý lifecycle của các đối tượng (được gọi là beans).​
- DI Container: Dùng DI để "tiêm" các phụ thuộc vào các đối tượng được quản lý bởi IoC container.​

**Ví dụ đơn giản để hình dung IoC**

Hãy tưởng tượng bạn cần lắp ráp một chiếc ô tô.

**1. Cách làm truyền thống (Không có IoC): Bạn tự làm mọi thứ**

Bạn là người điều khiển toàn bộ quy trình. Bạn phải:
1.  Tự mình đi tìm và chế tạo một cái động cơ (`Engine`).
2.  Tự mình lấy động cơ đó và lắp vào khung xe để tạo ra chiếc ô tô (`Car`).

```java
public class Main {
    public static void main(String[] args) {
        // 1. Bạn tự tạo Engine
        Engine engine = new Engine(); 
        
        // 2. Bạn tự tạo Car và lắp Engine vào
        Car car = new Car(engine); 
        
        car.drive(); // Bạn quyết định khi nào xe chạy
    }
}
```
*=> Quyền kiểm soát (Control) nằm trong tay bạn (lập trình viên).*

**2. Cách làm với IoC (Spring Framework): Bạn chỉ cần ra yêu cầu**

Bây giờ, bạn chỉ cần nói với một "nhà máy" (IoC Container) rằng:
-   "Tôi cần một chiếc `Car`."
-   "Một chiếc `Car` thì cần có một `Engine`."

Bạn định nghĩa các "bản thiết kế" (các class với annotation):

```java
@Component // Đánh dấu Engine là một "linh kiện" để nhà máy tự tạo
class Engine {
    // ...
}

@Component // Đánh dấu Car là một "sản phẩm" để nhà máy tự tạo
class Car {
    private final Engine engine;

    @Autowired // Tự động lắp "linh kiện" Engine vào đây
    public Car(Engine engine) {
        this.engine = engine;
    }
    // ...
}
```
Khi chương trình chạy, "nhà máy" Spring (IoC Container) sẽ tự động:
1.  Quét và thấy các bản thiết kế `Engine` và `Car`.
2.  Tự sản xuất một đối tượng `Engine`.
3.  Tự sản xuất một đối tượng `Car` và "tiêm" đối tượng `Engine` vừa tạo vào đó.

Lúc này, bạn chỉ cần đến nhà máy và lấy chiếc xe đã được lắp sẵn.

*=> Quyền kiểm soát việc tạo và lắp ráp đối tượng đã bị **đảo ngược** (Inversion of Control) từ bạn sang cho Framework.*

**Lợi ích của IoC và DI**
- Giảm sự kết hợp chặt chẽ (Reduced Coupling): Các lớp không trực tiếp phụ thuộc vào nhau.
- Dễ kiểm tra (Testability): Có thể dễ dàng tạo mock objects cho các phụ thuộc.​
- Dễ bảo trì (Maintainability): Thay đổi triển khai không ảnh hưởng đến code sử dụng chúng.​
- Quản lý tập trung (Centralized Management): Framework quản lý tất cả các phụ thuộc ở một nơi.​
- Tính linh hoạt (Flexibility): Dễ dàng thay đổi triển khai hoặc cấu hình mà không cần thay đổi code ứng dụng.​
---
## Kết Luận
Trên đây là những thông tin cơ bản và nền tảng nhất về HTTP, REST API và các Design Pattern quan trọng như Dependency Injection (DI) và Inversion of Control (IoC). Hi vọng qua bài viết này, các bạn đã có một cái nhìn tổng quan và vững chắc để chuẩn bị cho các bài học chuyên sâu hơn về Spring Boot trong thời gian tới.

Theo dõi chúng mình để cập nhật thêm nhiều kiến thức lập trình thú vị và sinh động nhé!

💻 Fanpage: https://www.facebook.com/clubproptit/

🌐 Website: https://proptit.com/

📺 Youtube: https://www.youtube.com/channel/UCdOcZv16XwUi7bhawqPjV9g

💌 Email: clblaptrinhptit@gmail.com

Like và share nếu bạn thấy bài viết hữu ích! Cảm ơn các bạn đã theo dõi!
