API (Application Programming Interface): là tập hợp các quy tắc, giao diện, hoặc phương thức để 2 hệ thống (hoặc 2 thành phần phần mềm) giao tiếp với nhau. API không chỉ dành cho web — có API cho thư viện, hệ điều hành, cơ sở dữ liệu, và dịch vụ web.
REST API (Representational State Transfer API): là một kiểu API web tuân theo kiến trúc REST. REST dùng các nguyên tắc/thói quen (constraints) để thiết kế dịch vụ web trên giao thức HTTP (hoặc tương tự) — ví dụ: stateless, resources (tài nguyên), uniform interface, cacheable, client-server, layer system, và (tùy chọn) HATEOAS.
Những điểm khác biệt chính (cụ thể)
Phạm trù

API: khái niệm tổng quát (mọi hình thức giao tiếp chương trình).
REST API: một loại API tuân theo kiến trúc REST, thường là API web trên HTTP.
Giao thức/Transport

API: có thể dùng bất kỳ giao thức nào (function call nội bộ, sockets, HTTP, gRPC, message queue, v.v.).
REST API: thường dùng HTTP(S) với các phương thức như GET/POST/PUT/DELETE/PATCH.
Kiến trúc & ràng buộc

API: không yêu cầu các ràng buộc kiến trúc cụ thể.
REST API: có những ràng buộc rõ ràng (stateless — server không lưu trạng thái client giữa các request; resources được biểu diễn qua URI; uniform interface; cacheable; v.v.).
Dạng dữ liệu và tiêu chuẩn

API: có thể trao đổi nhiều định dạng (binary, XML, JSON, protobuf, v.v.).
REST API: thường dùng JSON hoặc XML; không bắt buộc nhưng JSON phổ biến.
Trạng thái (state)

API: có thể trạng thái (stateful) hoặc không.
REST API: nên là stateless (mỗi request mang đủ thông tin để server xử lý).
HATEOAS (tùy chọn)

REST (theo lý thuyết) khuyến nghị HATEOAS — responses chứa liên kết (links) tới actions tiếp theo. Trong thực tế nhiều REST API không triển khai đầy đủ HATEOAS.
Ví dụ so sánh với các kiểu API khác

SOAP: API web với chuẩn XML và nhiều chuẩn kèm theo (WS-*); thường nặng hơn, có contract (WSDL).
GraphQL: API cho phép client yêu cầu chính xác dữ liệu cần; không theo mô hình REST truyền thống.
gRPC: API hiệu năng cao, dùng protobuf, thường cho microservices nội bộ.
Ví dụ nhanh (HTTP)
REST API (tài nguyên users):
GET /users -> lấy danh sách
GET /users/123 -> lấy user id=123
POST /users -> tạo user mới (thân request JSON)
PUT /users/123 -> cập nhật user 123
Một API không-REST (ví dụ RPC-style HTTP):
POST /getUserById với body { "id": 123 } — đây là kiểu gọi hàm qua HTTP, không tuân URI/resource theo REST.
Khi nào dùng REST API vs các loại API khác
Dùng REST API nếu:
Bạn muốn một dịch vụ web dễ hiểu, phù hợp với CRUD trên resources.
Muốn tận dụng HTTP caching, status codes, và hạ tầng web chuẩn.
Hợp cho public APIs hoặc dịch vụ đơn giản/medium complexity.
Dùng GraphQL nếu:
Client cần truy vấn linh hoạt, tránh over/under-fetching.
Dùng gRPC nếu:
Cần hiệu năng rất cao, streaming, và contract rõ ràng giữa microservices.
Dùng SOAP nếu:
Cần transaction-level features, WS-Security hoặc tương tác với hệ thống legacy yêu cầu SOAP.
Lưu ý thực tiễn / Best practices
REST không = HTTP-GET/POST only; dùng đúng HTTP verbs theo ý nghĩa.
Trả mã trạng thái HTTP hợp lý (200, 201, 204, 400, 401, 404, 500…).
Thiết kế URL theo resources, tránh verbs trong path (ví dụ: dùng POST /orders not /createOrder).
Document rõ ràng (OpenAPI/Swagger cho REST; GraphQL schema cho GraphQL).
Xem xét versioning (v1) nếu API public.
Tóm tắt ngắn (1 câu)
API là khái niệm chung cho giao tiếp giữa phần mềm; REST API là một kiểu API web tuân theo các nguyên tắc kiến trúc REST, thường dùng HTTP và resources, với những lợi ích về tính đơn giản, khả năng cache và chuẩn web.