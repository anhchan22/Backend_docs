# [BUỔI 5] CÁC KIẾN THỨC CƠ BẢN
## I. HTTP là gì ?
 - **HTTP (HyperText Transfer Protocol)** là một giao thức truyền tải siêu văn bản được thiết kế để cho phép giao tiếp giữa các máy khách (client) và máy chủ web (server). HTTP hoạt động theo mô hình yêu cầu - phản hồi (request-response protocol): máy khách gửi một yêu cầu HTTP đến máy chủ, sau đó máy chủ xử lý yêu cầu đó và gửi lại một phản hồi HTTP cho máy khách.
 - Các đặc điểm quan trọng của HTTP:
     - Stateless (Không có trạng thái): Mỗi yêu cầu từ máy khách đến máy chủ phải chứa tất cả thông tin cần thiết để máy chủ xử lý, máy chủ không lưu trữ thông tin về các yêu cầu trước đó.
     - Lightweight (Nhẹ nhàng): HTTP là một giao thức ứng dụng lớp (application layer protocol) nhẹ, được xây dựng trên TCP/IP, giúp tối ưu hóa truyền dữ liệu trên mạng.
     - Textual Protocol: Các thông điệp HTTP dễ đọc và dễ hiểu vì chúng được biểu diễn dưới dạng văn bản.
### 1. Các method trong HTTP
HTTP cung cấp nhiều phương thức (methods) khác nhau để thực hiện các thao tác với tài nguyên trên máy chủ. Dưới đây là những phương thức phổ biến nhất:
 - **GET** - Lấy dữ liệu từ máy chủ
    - GET được sử dụng để yêu cầu dữ liệu từ một tài nguyên cụ thể trên máy chủ. Khi sử dụng GET, các tham số được gửi dưới dạng query string trong URL (ví dụ: `https://api.example.com/users?id=123`). Một số đặc điểm quan trọng:​
    - Được coi là safe operation (hoạt động an toàn) - không thay đổi trạng thái của tài nguyên trên máy chủ​
    - Các yêu cầu GET có thể được cache (lưu vào bộ nhớ tạm)​
    - Không phù hợp với thông tin nhạy cảm vì các tham số được hiển thị trong URL​
    - Có giới hạn độ dài URL
 - **POST** - Gửi dữ liệu để tạo hoặc cập nhật tài nguyên
    - POST được sử dụng để gửi dữ liệu từ máy khách đến máy chủ nhằm tạo hoặc cập nhật tài nguyên. Dữ liệu được gửi trong message body của yêu cầu:​
    - Không phải là safe operation - có thể cập nhật trạng thái máy chủ​
    - Không phải là idempotent - gọi nhiều lần có thể tạo ra những trạng thái khác nhau​
    - Không bị cache​
    - Không có giới hạn độ dài dữ liệu
 - **PUT** - Cập nhật toàn bộ tài nguyên
    - PUT được sử dụng để thay thế hoàn toàn một tài nguyên hiện có trên máy chủ bằng dữ liệu mới. Nó là một idempotent operation - gọi nhiều lần với cùng dữ liệu sẽ tạo ra kết quả giống nhau.
 - **DELETE** - Xóa tài nguyên
    - DELETE được sử dụng để xóa một tài nguyên cụ thể khỏi máy chủ.
 - **PATCH** - Cập nhật một phần của tài nguyên
    - PATCH được sử dụng để cập nhật một phần của tài nguyên, không cần phải gửi toàn bộ dữ liệu như PUT.
 - **HEAD** - Lấy metadata của tài nguyên
    - HEAD tương tự như GET nhưng chỉ trả về headers mà không trả về message body. Nó thường được dùng để kiểm tra kích thước tài nguyên, ngày sửa đổi cuối cùng, hay kiểm tra xem tài nguyên có tồn tại hay không
### 2. Response là gì, Request là gì ?
#### HTTP Request (Yêu cầu HTTP)
Một HTTP Request là một thông điệp mà máy khách gửi đến máy chủ để yêu cầu thực hiện một hành động. HTTP Request bao gồm các thành phần sau:​
- Request Line (Dòng yêu cầu): Chứa phương thức HTTP (GET, POST, v.v.), đường dẫn URI của tài nguyên, và phiên bản HTTP (ví dụ: GET /users/123 HTTP/1.1).​
- Headers (Các tiêu đề): Cung cấp thông tin bổ sung về yêu cầu, chẳng hạn như Host, Content-Type, Authorization, v.v.
- Message Body (Thân thông điệp): Chứa dữ liệu gửi đến máy chủ (thường được sử dụng với POST, PUT, PATCH). Thân của GET request thường trống.​

#### HTTP Response (Phản hồi HTTP)
Một HTTP Response là thông điệp mà máy chủ gửi lại cho máy khách sau khi xử lý yêu cầu. HTTP Response bao gồm:​

- Status Line (Dòng trạng thái): Chứa phiên bản HTTP, mã trạng thái (status code), và cụm từ lý do (reason phrase). Ví dụ: HTTP/1.1 200 OK.​
- Response Headers: Cung cấp thông tin về phản hồi, chẳng hạn như Content-Type, Content-Length, Cache-Control, v.v..​
- Message Body: Chứa dữ liệu thực tế mà máy chủ trả về, chẳng hạn như trang HTML, dữ liệu JSON, hình ảnh, v.v..​
- HTTP Status Codes (Mã trạng thái HTTP)
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

### REST API (RESTful API)
REST API là một giao diện lập trình ứng dụng tuân theo các nguyên tắc thiết kế của REST (Representational State Transfer) - một phong cách kiến trúc được định nghĩa lần đầu tiên vào năm 2000 bởi tiến sĩ Roy Fielding.​

REST API được xây dựng dựa trên các nguyên tắc kiến trúc sau:​
- Separation (Tách biệt): Máy khách (ứng dụng yêu cầu dữ liệu) và máy chủ API (cung cấp dữ liệu) được tách biệt hoàn toàn.
- Statelessness (Không có trạng thái): Mỗi yêu cầu phải chứa tất cả thông tin cần thiết để máy chủ xử lý, không dựa vào các yêu cầu trước đó.
- Resource-based (Dựa trên tài nguyên): REST API trình bày dữ liệu và chức năng dưới dạng các tài nguyên (resources) có thể truy cập thông qua các URI (Uniform Resource Identifiers).
- Uniform Interface (Giao diện thống nhất): Quy tắc nhất quán về cách máy khách tương tác với tài nguyên máy chủ.
- Cacheable (Có thể cache): Các phản hồi từ máy chủ nên có thể được lưu vào bộ nhớ tạm.
- Layered System (Hệ thống phân lớp): Kiến trúc cho phép có các trung gian (như cache, load balancers) giữa máy khách và máy chủ.

**Cách hoạt động của REST API**
REST API sử dụng các HTTP methods tiêu chuẩn (GET, POST, PUT, DELETE) để thực hiện các thao tác CRUD (Create, Read, Update, Delete) trên tài nguyên. Mỗi tài nguyên có một URI duy nhất để định danh nó.​

Ví dụ:

- `GET /api/users` - Lấy danh sách tất cả người dùng
- `GET /api/users/123` - Lấy thông tin người dùng có ID là 123
- `POST /api/users` - Tạo người dùng mới
- `PUT /api/users/123` - Cập nhật thông tin người dùng 123
- `DELETE /api/users/123` - Xóa người dùng 123

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
        // Car tạo Engine - TÂM LIÊN KẾT CAO
        this.engine = new Engine();
    }
    
    public void drive() {
        engine.start();
        System.out.println("Car is driving");
    }
}
```
Vấn đề: Lớp Car phụ thuộc trực tiếp vào lớp Engine. Nếu muốn thay đổi Engine thành ElectricEngine, phải sửa code trong Car.

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
- **Constructor Injection (Tiêm qua Constructor)**: Phụ thuộc được truyền qua hàm khởi tạo của lớp. Đây là cách phổ biến nhất vì làm cho các phụ thuộc rõ ràng và bắt buộc.​
- **Setter Injection (Tiêm qua Setter)**: Phụ thuộc được thiết lập thông qua các phương thức setter sau khi đối tượng được tạo.
- **Interface Injection (Tiêm qua Interface)**: Lớp triển khai một interface để nhận các phụ thuộc.
- **Method Parameter Injection (Tiêm qua Tham số Phương thức)**: Phụ thuộc được truyền trực tiếp vào các phương thức khi gọi chúng.

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

**IoC Containers trong các Framework phổ biến**
***Spring Framework (Java)***
Spring IoC Container là thành phần cốt lõi của framework, quản lý vòng đời của các beans (đối tượng):​
- **BeanFactory Container**: Container cơ bản, chỉ cung cấp hỗ trợ dependency injection. Beans được tạo khi được yêu cầu (lazy loading).​
- **ApplicationContext Container**: Container nâng cao, xây dựng trên BeanFactory, cung cấp thêm các tính năng như internationalization, event propagation, integration với các module khác của Spring.​

```java
// Ví dụ sử dụng Spring IoC Container
@Component
public class UserService {
    private UserRepository repository;
    
    // Constructor Injection - Spring tự tiêm phụ thuộc
    public UserService(UserRepository repository) {
        this.repository = repository;
    }
}

@Component
public class UserRepository {
    // Spring quản lý lifecycle của đối tượng này
}
```

**Cách hoạt động của Spring IoC Container**
1. Đọc cấu hình (Configuration Metadata): Container đọc cấu hình từ XML, Java annotations hoặc Java-based configuration để hiểu cách tạo và kết nối các beans.

2. Tạo các beans: Container tạo các instance của các lớp được cấu hình.

3. Tiêm phụ thuộc (Dependency Injection): Container sử dụng DI để tiêm các phụ thuộc vào các beans.

4. Quản lý lifecycle: Container quản lý toàn bộ vòng đời của beans, từ khởi tạo đến hủy đối tượng.​

**Lợi ích của IoC và DI**
- Giảm sự kết hợp chặt chẽ (Reduced Coupling): Các lớp không trực tiếp phụ thuộc vào nhau.
- Dễ kiểm tra (Testability): Có thể dễ dàng tạo mock objects cho các phụ thuộc.​
- Dễ bảo trì (Maintainability): Thay đổi triển khai không ảnh hưởng đến code sử dụng chúng.​
- Quản lý tập trung (Centralized Management): Framework quản lý tất cả các phụ thuộc ở một nơi.​
- Tính linh hoạt (Flexibility): Dễ dàng thay đổi triển khai hoặc cấu hình mà không cần thay đổi code ứng dụng.​