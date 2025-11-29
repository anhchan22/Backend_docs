# Exception Handling trong Spring Boot — Hướng dẫn tổng quát

Mục tiêu: trình bày khái niệm, kiến trúc và các bước thực hiện để xây dựng hệ thống xử lý lỗi (exception handling) thống nhất cho REST API trong Spring Boot.

## Mục lục
- Tổng quan
- Thiết kế contract phản hồi lỗi
- Tạo tập mã lỗi (Error codes)
- Viết custom exception
- Tạo Global Exception Handler (`@ControllerAdvice` / `@RestControllerAdvice`)
- Xử lý validation errors
- Mapping sang HTTP Status phù hợp
- Logging và tracing
- Kiểm thử
- Best practices & cải tiến

---

## Tổng quan
- Mục đích của exception handler:
  - Tập trung việc chuyển đổi exception thành HTTP response có cấu trúc.
  - Tránh leak stacktrace/chi tiết nội bộ ra client.
  - Thống nhất định dạng lỗi để client dễ xử lý.
- Thành phần chính:
  - DTO phản hồi lỗi (ví dụ `ApiResponse` / `ErrorResponse`).
  - Enum/registry mã lỗi (optional nhưng khuyến khích).
  - Custom exceptions dùng trong business logic.
  - Global handler (`@ControllerAdvice`/`@RestControllerAdvice`) để map exception -> response.

---

## 1. Thiết kế contract phản hồi lỗi
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

---

## 2. Tạo tập mã lỗi (Error codes)
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

Lợi ích:
- Đồng bộ thông điệp, dễ i18n, dễ map sang HTTP status nếu cần.

---

## 3. Viết custom exception
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

---

## 4. Tạo Global Exception Handler
Sử dụng `@ControllerAdvice` hoặc `@RestControllerAdvice` để xử lý ngoại lệ toàn cục.

Ví dụ tổng quát:

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

---

## 5. Xử lý validation errors (Request validation)
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

---

## 6. Mapping sang HTTP Status phù hợp
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

---

## 7. Logging & tracing
- Log ở mức phù hợp: validation -> INFO/DEBUG, unexpected -> ERROR với stacktrace.
- Bao gồm correlationId / requestId trong logs và trả về header để dễ trace.
- Không trả stacktrace chi tiết cho client trong production.

---

## 8. Kiểm thử
- Unit test cho `GlobalExceptionHandler` bằng MockMvc:
  - Khi service ném `AppException(ErrorCode.X)` → assert HTTP status và JSON body có `code` và `message`.
  - Khi request invalid → assert `200/400` theo thiết kế và `details` chứa field errors.
- Integration tests: khởi chạy controller + slice tests nếu cần.

Ví dụ ngắn dùng MockMvc (Junit + Spring Test):
```java
mockMvc.perform(get("/api/resource/123"))
    .andExpect(status().isNotFound())
    .andExpect(jsonPath("$.code").value(ErrorCode.RESOURCE_NOT_FOUND.getCode()));
```

---

## 9. Best practices & đề xuất
- Duy trì 1 contract lỗi thống nhất cho toàn service.
- Sử dụng enum `ErrorCode` cho dễ maintain và i18n.
- Không expose internal exception messages trực tiếp cho client.
- Map `ErrorCode` sang `HttpStatus` hợp lý (đừng trả `400` cho mọi thứ).
- Cân nhắc trả `timestamp`, `path`, `requestId` trong `ErrorResponse` để debug.
- Cân nhắc tách handler cho `ControllerAdvice` theo module nếu cần behavior khác nhau.
- Thêm unit/integration tests cho handlers và mapping.

---

## Kết luận
- Quy trình cơ bản: thiết kế contract -> quản lý mã lỗi -> ném `AppException` trong business logic -> xử lý ở `@ControllerAdvice` -> trả response JSON thống nhất.
- Hệ thống đã sẵn sàng để mở rộng: map HTTP status, i18n, logging, correlation-id.
- Bước tiếp theo (nếu bạn muốn tôi làm tiếp): tạo mẫu file trong dự án, thay đổi `GlobalExceptionHandler` để map `ErrorCode` -> `HttpStatus`, hoặc viết test mẫu.

