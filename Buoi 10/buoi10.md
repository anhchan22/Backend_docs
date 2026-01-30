# Buổi 10: Security basic implement
## I. Spring security là gì ?
 - Spring Security là một framework mạnh mẽ và cực kỳ linh hoạt để bảo mật các ứng dụng Java được xây dựng trên Spring Framework. Nó cung cấp một giải pháp toàn diện cho cả việc xác thực (Authentication) và phân quyền (Authorization).

## II. JWT là gì ?
 - JWT (JSON Web Token) là một chuỗi mã hóa đại diện cho danh tính của người dùng. Sau khi người dùng đăng nhập thành công, server sẽ tạo một JWT và gửi về cho client. Từ đó, client chỉ cần đính kèm JWT trong mỗi request để chứng minh “tôi đã đăng nhập”.
### 1. Cấu trúc của JWT
![alt text](image-1.png)
 - JWT gồm 3 phần  cách nhau bởi dấu chấm (.):
 `[Header].[Payload].[Signature]`
    - Header: Thông thường sẽ bao gồm 2 phần: loại token và thuật toán dùng để ký (ví dụ: HS256)


    - Payload: chứa thông tin người dùng (username, roles, thời gian hết hạn,…)
    - Signature: được ký bởi secret key để đảm bảo tính toàn vẹn. Để tạo ra được signature chúng ta sẽ phải có: base64Url encoded header, base64Url encoded payload, secret key, sign algorithm
### 2.  JWT hoạt động như thế nào?
![alt text](image.png)
- Đối với các hệ thống sử dụng token-based authentication hay cụ thể hơn là sử dụng JWT thì khi người dùng đăng nhập vào hệ thống sẽ nhận lại một JWT nếu đăng nhập thành công.

- Từ các request tiếp theo JWT sẽ được đính kèm trong phần header và thường có dạng như sau:
    - `Authorization: Bearer <JWT>`

Ví dụ:
```
curl --location 'https://api.myblog/posts' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyLCJleHAiOjIwMjMxMjAxMzQsImVtYWlsIjoibGFwdHJpbmhiMmFAZ21haWwuY29tIn0.Qa24mhnjonbkOm18YK8kKot-l8ULB0AfMFBN5L-KoXA' \
--header 'Content-Type: application/json' \
--data '{
    ...
}'
```
- Hệ thống sẽ sử dụng JWT này để xác thực và phân quyền người dùng được phép truy cập vào những tài nguyên, services, APIs nào của hệ thống.
### 3. Thêm các dependencies cho JWT và Spring Security.
```
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>

    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
    </dependency>
```
## III. Authentication, Authorization là gì ?
