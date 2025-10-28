# [Buổi 1] Nhập môn CSDL
## I. CSDL là gì ?
 - Cơ sở dữ liệu là một bộ lưu trữ dữ liệu có cấu trúc được lưu trữ trong một
thiết bị điện tử. Dữ liệu có thể là văn bản, video, hình ảnh hoặc bất kỳ định
dạng nào khác.
 - CSDL thể hiện các khía cạnh khác nhau của thế giới thực.
 - Một cơ sở dữ liệu quan hệ lưu trữ dữ liệu dưới dạng bảng và cơ sở dữ liệu
 - SQL (Ngôn ngữ truy vấn có cấu trúc) là ngôn ngữ tiêu chuẩn để truy cập và
thao tác dữ liệu trong cơ sở dữ liệu quan hệ.
## II. Hệ quản trị CSDL là gì ?
 - Hệ Quản trị CSDL là một phần mềm cho phép tạo, cập nhật và truy xuất dữ liệu
theo cách có tổ chức. Nó cũng cung cấp bảo mật cho cơ sở dữ liệu.
 - Cụ thể hơn, gồm một số đặc điểm chính:
    1. Cho phép người dùng tạo mới CSDL
    2. Cho phép người dùng truy vấn cơ sở dữ liệu
    3. Hỗ trợ lưu trữ số lượng lớn dữ liệu
    4. Kiểm soát truy nhập dữ liệu
 - Ví dụ về DBMS quan hệ là MySQL, Oracle, Microsoft SQL Server, Postgresql. Ví
dụ về DBMS NoSQL là MongoDB, Cassandra, DynamoDB và Redis.
 - Các chức năng chính của Hệ quản trị CSDL:
    1. Mô hình hóa được dữ liệu
    2. Lưu trữ và truy vấn dữ liệu
    3. Hỗ trợ nhiều người dùng đồng thời
    4. Bảo mật cho dữ liệu
    5. Backup và Recovery
 - Nhược điểm của Hệ quản trị CSDL:
    1. Phức tạp
    2. Kích thước lớn
    3. Chi phí mua và bảo trì
    4. Ảnh hưởng lớn khi có lỗi
    5. Thêm giá thành cho phần cứng hỗ trợ
## III. Cài đặt MS SQL Server
## IV. Câu lệnh tạo database, table trong MS SQL Server

### 1. Tạo Database
Để tạo một database mới trong MS SQL Server, sử dụng câu lệnh `CREATE DATABASE`:

```sql
CREATE DATABASE TenDatabase;
```

Ví dụ:
```sql
CREATE DATABASE QuanLySinhVien;
```

Hoặc có thể tạo database với các tùy chọn chi tiết:
```sql
CREATE DATABASE QuanLySinhVien
ON 
( NAME = 'QuanLySinhVien',
  FILENAME = 'C:\Data\QuanLySinhVien.mdf',
  SIZE = 100MB,
  MAXSIZE = 500MB,
  FILEGROWTH = 10MB )
LOG ON 
( NAME = 'QuanLySinhVien_Log',
  FILENAME = 'C:\Data\QuanLySinhVien.ldf',
  SIZE = 10MB,
  FILEGROWTH = 10% );
```

### 2. Sử dụng Database
Trước khi tạo table, cần chọn database để làm việc:
```sql
USE QuanLySinhVien;
```

### 3. Tạo Table
Để tạo table, sử dụng câu lệnh `CREATE TABLE`:

**Cú pháp cơ bản:**
```sql
CREATE TABLE TenTable (
    TenCot1 KieuDuLieu [CONSTRAINT],
    TenCot2 KieuDuLieu [CONSTRAINT],
    ...
);
```

**Ví dụ tạo table SinhVien:**
```sql
CREATE TABLE SinhVien (
    MaSV INT PRIMARY KEY IDENTITY(1,1),
    HoTen NVARCHAR(100) NOT NULL,
    NgaySinh DATE,
    GioiTinh NVARCHAR(10),
    DiaChi NVARCHAR(200),
    Email VARCHAR(100) UNIQUE,
    SoDienThoai VARCHAR(15)
);
```

### 4. Các kiểu dữ liệu thường dùng trong MS SQL Server
- **INT**: Số nguyên (4 bytes)
- **BIGINT**: Số nguyên lớn (8 bytes)
- **VARCHAR(n)**: Chuỗi ký tự có độ dài biến đổi
- **NVARCHAR(n)**: Chuỗi Unicode có độ dài biến đổi
- **CHAR(n)**: Chuỗi ký tự có độ dài cố định
- **DATE**: Ngày tháng
- **DATETIME**: Ngày giờ
- **DECIMAL(p,s)**: Số thập phân với độ chính xác p và s chữ số thập phân
- **BIT**: Kiểu boolean (0 hoặc 1)

### 5. Các ràng buộc (Constraints)
- **PRIMARY KEY**: Khóa chính, đảm bảo tính duy nhất và không null
- **FOREIGN KEY**: Khóa ngoại, tham chiếu đến khóa chính của table khác
- **NOT NULL**: Không cho phép giá trị null
- **UNIQUE**: Đảm bảo tính duy nhất
- **CHECK**: Kiểm tra điều kiện
- **DEFAULT**: Giá trị mặc định

**Ví dụ table với các ràng buộc:**
```sql
CREATE TABLE MonHoc (
    MaMH INT PRIMARY KEY IDENTITY(1,1),
    TenMH NVARCHAR(100) NOT NULL,
    SoTinChi INT CHECK (SoTinChi > 0),
    TrangThai BIT DEFAULT 1
);

CREATE TABLE Diem (
    MaSV INT,
    MaMH INT,
    DiemGiuaKy DECIMAL(4,2) CHECK (DiemGiuaKy >= 0 AND DiemGiuaKy <= 10),
    DiemCuoiKy DECIMAL(4,2) CHECK (DiemCuoiKy >= 0 AND DiemCuoiKy <= 10),
    PRIMARY KEY (MaSV, MaMH),
    FOREIGN KEY (MaSV) REFERENCES SinhVien(MaSV),
    FOREIGN KEY (MaMH) REFERENCES MonHoc(MaMH)
);
```

### 6. Xóa Database và Table
**Xóa Table:**
```sql
DROP TABLE TenTable;
```

**Xóa Database:**
```sql
DROP DATABASE TenDatabase;
```

### 7. Lưu ý quan trọng
- Tên database và table không được chứa ký tự đặc biệt
- Sử dụng NVARCHAR cho dữ liệu tiếng Việt
- IDENTITY(1,1) tự động tăng giá trị bắt đầu từ 1, mỗi lần tăng 1
- Luôn backup database trước khi thực hiện các thao tác quan trọng