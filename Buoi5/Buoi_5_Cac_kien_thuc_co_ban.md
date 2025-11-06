# BACKEND JAVA SPRING BOOT - PROPTIT
# Buổi 5: Các kiến thức cơ bản
Người thực hiện: Trần Tiểu Long

- [BACKEND JAVA SPRING BOOT - PROPTIT](#backend-java-spring-boot---proptit)
- [Buổi 5: Các kiến thức cơ bản](#buổi-5-các-kiến-thức-cơ-bản)
    - [1. HTTP](#1-http)
      - [1.1. Định nghĩa](#11-định-nghĩa)
      - [1.2. Method](#12-method)
      - [1.3. Request](#13-request)
      - [1.4. Response](#14-response)
      - [1.5. Một số status code](#15-một-số-status-code)
    - [2. API \& RestAPI](#2-api--restapi)
      - [2.1. API (Application Programming Interface)](#21-api-application-programming-interface)
      - [2.2. RestAPI (Representational State Transfer)](#22-restapi-representational-state-transfer)
      - [2.3. Luồng hoạt động](#23-luồng-hoạt-động)
      - [2.4. Quy tắc viết tên API](#24-quy-tắc-viết-tên-api)
    - [3. IoC (Inversion of Control) \& DI (Dependency Injection)](#3-ioc-inversion-of-control--di-dependency-injection)
      - [3.1. IoC (Inversion of Control) - Đảo ngược điều khiển](#31-ioc-inversion-of-control---đảo-ngược-điều-khiển)
      - [3.2. DI (Dependency Injection) - Tiêm phụ thuộc](#32-di-dependency-injection---tiêm-phụ-thuộc)
      - [3.3. Ví dụ minh hoạ](#33-ví-dụ-minh-hoạ)

### 1. HTTP 
#### 1.1. Định nghĩa
- **HTTP (HyperText Transfer Protocol)** là giao thức truyền tải siêu văn bản dùng để trao đổi dữ liệu giữa máy khách (client) và máy chủ (server) qua Internet. Nó hoạt động dựa trên mô hình client-server và là nền tảng của Web hiện đại
#### 1.2. Method
| Method | Mục đích                        | Ví dụ                                            |
| ------ | ------------------------------- | ------------------------------------------------ |
| GET    | Lấy tài nguyên (không thay đổi) | `GET /products`                                  |
| POST   | Tạo mới tài nguyên              | `POST /products` với body `{ "name": "Áo", ...}` |
| PUT    | Cập nhật toàn bộ tài nguyên     | `PUT /products/5` với body `{ "name": "Quần"}`   |
| PATCH  | Cập nhật một phần tài nguyên    | `PATCH /products/5` với body `{ "price": 100 }`  |
| DELETE | Xóa tài nguyên                  | `DELETE /products/5`                             |

#### 1.3. Request
- HTTP Request là thông điệp do client (trình duyệt, ứng dụng) gửi đến server để yêu cầu thực hiện một hành động (lấy dữ liệu, tạo mới, cập nhật, xóa…).
- **Cấu trúc:**
    + ***Request Line*** ==> Gồm 3 thành phần
        * Method: Phương thức HTTP
        * Request-URI: Đường dẫn tài nguyên
        * HTTP Version
        ```
        Ví dụ:
        GET /api/users/123 HTTP/1.1
        ```
    + ***Headers*** 
        + Cặp khóa–giá trị truyền metadata, như chứng thực, kiểu dữ liệu, cache…
        + Ví dụ:
        ``` java
        Host: api.example.com
        Content-Type: application/json
        Authorization: Bearer abc123
        ```
    + ***Body*** ==> Tuỳ phương thức: có hoặc không
        * Chứa dữ liệu gửi kèm (thường dùng với POST, PUT, PATCH).
        * Ví dụ:
        ``` java
        Khi tạo user mới cần truyền vào body như sau:

        {
        "name": "Long",
        "age": 30
        }

        ```
- **Ví dụ đầy đủ về HTTP Request:**
    ``` java
    POST /api/users HTTP/1.1
    Host: api.example.com
    Content-Type: application/json
    Authorization: Bearer abc123

    {
    "name": "Long",
    "age": 30
    }

    ```
#### 1.4. Response
- HTTP Response là thông điệp do server gửi về client sau khi xử lý request, bao gồm mã trạng thái và (nếu có) dữ liệu trả về.
- **Cấu trúc:**
    + ***Status Line*** ==> Gồm 3 thành phần
        * HTTP Version
        * Status Code Mã trạng thái
        * Reason Phrase: Mô tả trạng thái
        ```
        Ví dụ:

        HTTP/1.1 200 OK
        ```
    + ***Headers***
        + Cặp khóa–giá trị metadata cho response, như kiểu dữ liệu, cache, length…
        + Ví dụ:
        ``` java
        Content-Type: application/json
        Cache-Control: no-cache
        ```
    + ***Body***
        * Dữ liệu trả về (JSON, HTML, hình ảnh…), tuỳ vào request và kết quả xử lý.
        * Ví dụ:
        ``` java
        Trả về thông tin user vừa tạo:

        {
        "id": 123,
        "name": "Long",
        "age": 30
        }
        ```
- **Ví dụ đầy đủ về HTTP Response:**
    ``` java
    HTTP/1.1 201 Created
    Content-Type: application/json
    Location: /api/users/123

    {
    "id": 123,
    "name": "Long",
    "age": 30
    }

    ```
#### 1.5. Một số status code
- **1xx - Thông tin (Informational)**
    | Mã trạng thái | Tiếng Anh           | Tiếng Việt            |
    | ------------- | ------------------- | --------------------- |
    | 100           | Continue            | Tiếp tục yêu cầu      |
    | 101           | Switching Protocols | Chuyển giao giao thức |
    | 102           | Processing (WebDAV) | Đang xử lý            |
    | 103           | Early Hints         | Gợi ý sớm             |


- **2xx - Thành công (Success)**
    | Mã trạng thái | Tiếng Anh                     | Tiếng Việt                 |
    | ------------- | ----------------------------- | -------------------------- |
    | 200           | OK                            | Thành công                 |
    | 201           | Created                       | Đã tạo thành công          |
    | 202           | Accepted                      | Đã chấp nhận xử lý         |
    | 203           | Non-Authoritative Information | Thông tin không chính thức |
    | 204           | No Content                    | Không có nội dung          |
    | 205           | Reset Content                 | Yêu cầu đặt lại nội dung   |
    | 206           | Partial Content               | Nội dung một phần          |


- **3xx - Chuyển hướng (Redirection)**
    | Mã trạng thái | Tiếng Anh          | Tiếng Việt             |
    | ------------- | ------------------ | ---------------------- |
    | 300           | Multiple Choices   | Nhiều lựa chọn         |
    | 301           | Moved Permanently  | Đã chuyển vĩnh viễn    |
    | 302           | Found              | Tìm thấy (tạm thời)    |
    | 303           | See Other          | Xem tại nơi khác       |
    | 304           | Not Modified       | Không thay đổi         |
    | 307           | Temporary Redirect | Chuyển hướng tạm thời  |
    | 308           | Permanent Redirect | Chuyển hướng vĩnh viễn |

- **4xx - Lỗi từ phía client (Client Error)**
    | Mã trạng thái | Tiếng Anh          | Tiếng Việt                  |
    | ------------- | ------------------ | --------------------------- |
    | 400           | Bad Request        | Yêu cầu không hợp lệ        |
    | 401           | Unauthorized       | Không được ủy quyền         |
    | 403           | Forbidden          | Bị từ chối truy cập         |
    | 404           | Not Found          | Không tìm thấy              |
    | 405           | Method Not Allowed | Phương thức không được phép |
    | 408           | Request Timeout    | Hết thời gian yêu cầu       |
    | 409           | Conflict           | Xung đột                    |
    | 429           | Too Many Requests  | Quá nhiều yêu cầu           |


- **5xx - Lỗi từ phía server (Server Error)**
    | Mã trạng thái | Tiếng Anh                  | Tiếng Việt                       |
    | ------------- | -------------------------- | -------------------------------- |
    | 500           | Internal Server Error      | Lỗi máy chủ nội bộ               |
    | 501           | Not Implemented            | Chưa được hỗ trợ                 |
    | 502           | Bad Gateway                | Cổng không hợp lệ                |
    | 503           | Service Unavailable        | Dịch vụ không khả dụng           |
    | 504           | Gateway Timeout            | Hết thời gian phản hồi từ cổng   |
    | 505           | HTTP Version Not Supported | Phiên bản HTTP không được hỗ trợ |

  
### 2. API & RestAPI
#### 2.1. API (Application Programming Interface)
- **Định nghĩa:** Giao diện cho phép hai hệ thống phần mềm giao tiếp, trao đổi dữ liệu và chức năng với nhau.
- **Ví dụ 1: API như Nhà Hàng**
    - Hãy tưởng tượng vào một nhà hàng:
      - **Bạn (Client)**: Muốn gọi món.
      - **Bồi bàn (API)**: Tiếp nhận yêu cầu của bạn, chuyển tới bếp.
      - **Bếp (Server)**: Chế biến món ăn.
      - **Bồi bàn (API)**: Mang món ăn (dữ liệu) từ bếp trả lại bạn.
    - ***Kết luận:*** *API là "bồi bàn" trung gian, giúp bạn (ứng dụng) giao tiếp với bếp (máy chủ) mà không cần biết bếp hoạt động thế nào.*
- **Ví dụ 2: API thời tiết**
    - Giả sử dùng ứng dụng thời tiết
        - **Ứng dụng** gọi API để hỏi: "Hà Nội hôm nay thế nào?"
        - **API** gửi câu hỏi tới máy chủ thời tiết.
        - **Máy chủ** trả lời: { "city": "Hà Nội", "temp": 28, "unit": "°C", "description": "Trời nắng" }.
        - **Ứng dụng** hiển thị dữ liệu này cho bạn.
    - Code minh hoạ:
    ``` javascript
    // Gọi API thời tiết
    fetch('https://api.thoitiet.vn/hanoi') // Địa chỉ API
    .then(response => response.json())   // Chuyển đổi dữ liệu
    .then(data => {
        console.log(`Nhiệt độ Hà Nội: ${data.temp}${data.unit}`); 
        console.log(`Mô tả: ${data.description}`);
    });

    // Kết quả:
    // Nhiệt độ Hà Nội: 28°C
    // Mô tả: Trời nắng
    ```
#### 2.2. RestAPI (Representational State Transfer)
- **Định nghĩa**: Kiến trúc web API tuân theo các nguyên tắc REST, trong đó mỗi tài nguyên được biểu diễn qua URI, và các hành động CRUD được ánh xạ vào HTTP methods.
- Các nguyên tắc này bao gồm:
    + **Stateless (Không trạng thái):** Mỗi yêu cầu từ client đến server phải chứa tất cả thông tin cần thiết để server hiểu và xử lý yêu cầu đó. Server không lưu trữ bất kỳ thông tin nào về phiên làm việc của client giữa các yêu cầu.

    + **Client-Server:** Phân tách rõ ràng giữa giao diện người dùng (client) và phần xử lý dữ liệu (server).

    + **Cacheable (Có thể lưu trữ):** Dữ liệu trả về có thể được cache (lưu trữ tạm thời) để cải thiện hiệu suất.

    + **Uniform Interface (Giao diện đồng nhất):** Áp dụng một tập hợp các quy tắc chuẩn cho cách client tương tác với server, giúp hệ thống dễ hiểu và dễ mở rộng.

    + **Layered System (Hệ thống phân lớp):** Server có thể được cấu trúc thành nhiều lớp (ví dụ: lớp bảo mật, lớp cân bằng tải) mà client không cần biết.

    + **Code-On-Demand (Tùy chọn):** Khả năng gửi mã thực thi từ server đến client (ít phổ biến hơn).
- **Ví dụ:**
    ```
    GET /products: Lấy tất cả sản phẩm.

    GET /products/{id}: Lấy thông tin một sản phẩm cụ thể bằng ID.

    POST /products: Thêm một sản phẩm mới.

    PUT /products/{id}: Cập nhật thông tin một sản phẩm cụ thể.

    DELETE /products/{id}: Xóa một sản phẩm cụ thể.
    ```

#### 2.3. Luồng hoạt động
- Khi một client (ví dụ: trình duyệt web, ứng dụng di động) tương tác với một RESTful API, luồng hoạt động cơ bản diễn ra như sau:

    + **Client gửi yêu cầu HTTP:** Client tạo một yêu cầu HTTP (GET, POST, PUT, DELETE, v.v.) đến một URL cụ thể (endpoint) của API. Yêu cầu này có thể bao gồm dữ liệu (ví dụ: JSON trong yêu cầu POST/PUT) và các header (ví dụ: Authorization token).

        ``` Ví dụ: GET https://api.example.com/products/123```
    
    + **Server nhận và xử lý yêu cầu:** Máy chủ chứa API nhận yêu cầu. Dựa vào URL và phương thức HTTP, server sẽ định tuyến yêu cầu đến bộ điều khiển (controller) hoặc hàm xử lý phù hợp.

    + **Xử lý logic nghiệp vụ:** Hàm xử lý thực hiện các thao tác cần thiết (ví dụ: truy vấn cơ sở dữ liệu để lấy sản phẩm có ID 123, thêm sản phẩm mới vào DB, v.v.).

    + **Server gửi phản hồi HTTP:** Sau khi xử lý, server tạo một phản hồi HTTP. Phản hồi này bao gồm:

        * ***Mã trạng thái HTTP:*** Cho biết kết quả của yêu cầu (ví dụ: 200 OK cho thành công, 404 Not Found nếu không tìm thấy, 500 Internal Server Error nếu có lỗi server).

        * ***Header:*** Các thông tin bổ sung về phản hồi (ví dụ: Content-Type: application/json).

        * ***Body:*** Dữ liệu trả về (thường ở định dạng JSON hoặc XML).
        ```
        Ví dụ: Phản hồi thành công cho GET /products/123

        HTTP/1.1 200 OK
        Content-Type: application/json

        {
            "id": 123,
            "name": "Laptop XYZ",
            "price": 1200
        }
        ```
    + **Client nhận và xử lý phản hồi:** Client nhận phản hồi, kiểm tra mã trạng thái và xử lý dữ liệu trong body để hiển thị cho người dùng hoặc thực hiện các hành động tiếp theo.

#### 2.4. Quy tắc viết tên API
- **1. Dùng danh từ, dạng số nhiều**
    - Đặt tên theo **tài nguyên (resource)** và dùng **danh từ số nhiều**:

    | Mô tả             | Method & URL             | Ghi chú                       |
    |-------------------|--------------------------|-------------------------------|
    | Lấy tất cả user   | `GET /users`             | Dùng GET cho danh sách        |
    | Lấy 1 user cụ thể | `GET /users/{id}`        | Ví dụ: `/users/123`           |
    | Tạo user mới      | `POST /users`            | Body chứa thông tin user      |
    | Cập nhật user     | `PUT /users/{id}`        | PUT cập nhật toàn bộ          |
    | Xóa user          | `DELETE /users/{id}`     | Xoá theo ID                   |

- **2. Không dùng động từ trong URL**
  - Sai: `/getUsers`, `/createUser`
  - Đúng: `/users` (kết hợp với method GET/POST/PUT/DELETE)
  
- **3. Dùng quy tắc lồng tài nguyên (Nested Resources)**
    - Áp dụng cho mối quan hệ cha – con:

    | Mô tả                     | Method & URL                             |
    |---------------------------|------------------------------------------|
    | Lấy tất cả comment của post | `GET /posts/{postId}/comments`         |
    | Lấy comment cụ thể         | `GET /posts/{postId}/comments/{id}`     |

- **4. Dùng lowercase, ngăn cách bằng dấu `-` hoặc không dấu**

    | Đúng       | Sai         |
    |--------------|----------------|
    | `/user-info` | `/user_info`   |
    | `/order-items` | `/orderItems` |

- **5. Lọc, tìm kiếm, phân trang bằng query params**

    | Tác vụ                     | Ví dụ URL                                          |
    |----------------------------|----------------------------------------------------|
    | Lọc theo tên               | `/users?name=long`                                 |
    | Phân trang                 | `/products?page=2&limit=10`                        |
    | Sắp xếp theo ngày          | `/posts?sort=createdAt&order=desc`                 |
    | Tìm kiếm                   | `/search?q=điện thoại`                             |

- **6. Tránh viết tắt mơ hồ**

    | Sai    | Đúng     |
    |----------|-------------|
    | `/usr`   | `/users`    |
    | `/prds`  | `/products` |

- **7. Phiên bản API**
    - Đặt version ở phần đầu URL:
    
    ```java
    GET /api/v1/users
    POST /api/v2/products
    ```

### 3. IoC (Inversion of Control) & DI (Dependency Injection)
#### 3.1. IoC (Inversion of Control) - Đảo ngược điều khiển
- **Khái niệm:**
    + IoC là nguyên lý thiết kế phần mềm nơi quyền kiểm soát luồng thực thi bị đảo ngược:
        * Truyền thống: Code của bạn gọi thư viện/framework
        * Với IoC: Framework gọi code của bạn
    ```
    Tại sao gọi là "đảo ngược"?

    - Thay vì bạn chủ động tạo và quản lý đối tượng → Container làm việc này
    - Bạn chỉ định nghĩa "cái gì cần" thay vì "cách tạo ra nó"
    ```
- **Lợi ích**:
    + Giảm coupling (liên kết lỏng lẻo)
    + Dễ dàng mở rộng và bảo trì
    + Tập trung vào nghiệp vụ chính

#### 3.2. DI (Dependency Injection) - Tiêm phụ thuộc
- **Khái niệm:**
    + DI là cách triển khai cụ thể nhất của IoC, tập trung vào việc:
        * Cung cấp các phụ thuộc (dependency) từ bên ngoài
        * Thay vì để lớp tự tạo phụ thuộc
- **Cách triển khai:**
    + Constructor Injection (tốt nhất)
    + Setter Injection
    + Interface Injection

#### 3.3. Ví dụ minh hoạ
- **Tạo interface và implementation**
    ``` java
    // Interface cho dịch vụ cơ sở dữ liệu
    interface DatabaseService {
        void saveData(String data);
    }

    // Implementation 1: MySQL
    class MySQLService implements DatabaseService {
        @Override
        public void saveData(String data) {
            System.out.println("Lưu '" + data + "' vào MySQL");
        }
    }

    // Implementation 2: MongoDB
    class MongoDBService implements DatabaseService {
        @Override
        public void saveData(String data) {
            System.out.println("Lưu '" + data + "' vào MongoDB");
        }
    }
    ```
- **Lớp cần sử dụng dịch vụ (không DI)**
    ``` java
    class DataProcessor {
        // Tự tạo dependency → Ràng buộc cứng với MySQLService
        private DatabaseService dbService = new MySQLService();
        
        public void process(String data) {
            System.out.println("Xử lý dữ liệu...");
            dbService.saveData(data);
        }
    }

    --> Vấn đề: Muốn đổi sang MongoDB phải sửa code trực tiếp trong lớp
    ```
- **Áp dụng DI qua Constructor**
    ``` java
    class DataProcessor {
        private DatabaseService dbService;
        
        // Dependency được TIÊM vào qua constructor
        public DataProcessor(DatabaseService dbService) {
            this.dbService = dbService;
        }
        
        public void process(String data) {
            System.out.println("Xử lý dữ liệu...");
            dbService.saveData(data);
        }
    }
    ```

- **IoC Container đơn giản**
    ``` java
    class AppContainer {
        // Container quyết định implementation nào được sử dụng
        public DatabaseService getDatabaseService() {
            // Có thể đọc cấu hình từ file/environment
            String dbType = System.getenv("DB_TYPE");
            
            if ("mongodb".equalsIgnoreCase(dbType)) {
                return new MongoDBService();
            }
            // Mặc định dùng MySQL
            return new MySQLService();
        }
        
        public DataProcessor getDataProcessor() {
            // Container "lắp ráp" các dependency
            return new DataProcessor(getDatabaseService());
        }
    }
    ```
- **Sử dụng trong ứng dụng**
    ``` java
    public class MainApp {
        public static void main(String[] args) {
            // Khởi tạo container
            AppContainer container = new AppContainer();
            
            // Lấy đối tượng đã được inject dependency
            DataProcessor processor = container.getDataProcessor();
            
            // Sử dụng
            processor.process("Dữ liệu quan trọng");
        }
    }

    --> Kết quả khi chạy:
    # Nếu set DB_TYPE=mongodb
    Xử lý dữ liệu...
    Lưu 'Dữ liệu quan trọng' vào MongoDB

    # Không set DB_TYPE
    Xử lý dữ liệu...
    Lưu 'Dữ liệu quan trọng' vào MySQL
    ```

- **Tóm tắt:**
    ```
    TRUYỀN THỐNG:
    [DataProcessor] ---new---> [MySQLService]

    IoC/DI:
    [DataProcessor] <---[DatabaseService]--- [Container]
                            ^
                            |
            [MySQLService] [MongoDBService] [CloudService]
    ```
