# API Documentation - Hệ thống Đăng Ký Môn Học (DKMH)

## Tổng quan

Hệ thống Đăng Ký Môn Học cung cấp các API để quản lý:
- ✅ **Xác thực (Authentication)**: Đăng nhập, đăng xuất, kiểm tra token
- ✅ **Quản lý người dùng (User)**: Tạo tài khoản, xem thông tin người dùng
- ✅ **Quản lý môn học (MonHoc)**: Xem danh sách môn học, tạo môn học mới
- ✅ **Quản lý lớp học phần (LopHocPhan)**: Xem lớp học phần, tạo lớp học phần
- ✅ **Đăng ký môn học (Registration)**: Thêm/xóa lớp vào draft, nộp phiếu đăng ký, xem phiếu đăng ký

**Base URL**: `http://localhost:8080`

---

## 1. Authentication API

### 1.1. Đăng nhập
**Endpoint**: `POST /api/auth/login`

**Mô tả**: Đăng nhập vào hệ thống và nhận JWT token

**Request Body**:
```json
{
  "maSV": "20210001",
  "matKhau": "password123"
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "authenticated": true
  }
}
```

**Lỗi có thể gặp**:
- `401 Unauthorized`: Sai mã sinh viên hoặc mật khẩu

---

### 1.2. Kiểm tra token (Introspect)
**Endpoint**: `POST /api/auth/introspect`

**Mô tả**: Kiểm tra tính hợp lệ của JWT token

**Request Body**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "valid": true
  }
}
```

---

### 1.3. Đăng xuất
**Endpoint**: `POST /api/auth/logout`

**Mô tả**: Đăng xuất và vô hiệu hóa token hiện tại

**Request Body**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": null
}
```

---

## 2. User API

### 2.1. Tạo tài khoản người dùng (Đăng ký)
**Endpoint**: `POST /api/user`

**Mô tả**: Tạo tài khoản sinh viên mới

**Request Body**:
```json
{
  "maSV": "20210001",
  "ten": "Nguyen Van A",
  "matKhau": "password123"
}
```

**Validation**:
- `maSV`: Bắt buộc, không được để trống
- `matKhau`: Tối thiểu 8 ký tự

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "maSV": "20210001",
    "ten": "Nguyen Van A",
    "matKhau": "encoded_password",
    "roles": ["USER"]
  }
}
```

---

### 2.2. Lấy danh sách người dùng
**Endpoint**: `GET /api/user`

**Mô tả**: Lấy danh sách tất cả người dùng (yêu cầu authentication)

**Headers**:
```
Authorization: Bearer <token>
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": [
    {
      "maSV": "20210001",
      "ten": "Nguyen Van A",
      "matKhau": "encoded_password",
      "roles": ["USER"]
    },
    {
      "maSV": "20210002",
      "ten": "Tran Thi B",
      "matKhau": "encoded_password",
      "roles": ["USER", "ADMIN"]
    }
  ]
}
```

---

### 2.3. Lấy thông tin người dùng theo ID
**Endpoint**: `GET /api/user/{userId}`

**Mô tả**: Lấy thông tin chi tiết của một người dùng

**Headers**:
```
Authorization: Bearer <token>
```

**Path Parameters**:
- `userId` (string): Mã sinh viên

**Example**: `GET /api/user/20210001`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "maSV": "20210001",
    "ten": "Nguyen Van A",
    "matKhau": "encoded_password",
    "roles": ["USER"]
  }
}
```

---

### 2.4. Lấy thông tin cá nhân
**Endpoint**: `GET /api/user/myInfo`

**Mô tả**: Lấy thông tin của người dùng hiện tại (từ token)

**Headers**:
```
Authorization: Bearer <token>
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": null,
  "result": {
    "maSV": "20210001",
    "ten": "Nguyen Van A",
    "matKhau": "encoded_password",
    "roles": ["USER"]
  }
}
```

---

## 3. Môn Học API

### 3.1. Lấy tất cả môn học
**Endpoint**: `GET /api/mon-hoc/tat-ca-mon-hoc`

**Mô tả**: Lấy danh sách tất cả môn học trong hệ thống

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy tất cả môn học thành công",
  "result": [
    {
      "maMH": "IT1110",
      "tenMH": "Nhập môn lập trình",
      "soTinChi": 3
    },
    {
      "maMH": "IT2020",
      "tenMH": "Cấu trúc dữ liệu và giải thuật",
      "soTinChi": 4
    }
  ]
}
```

---

### 3.2. Lấy môn học theo học kỳ
**Endpoint**: `GET /api/mon-hoc/mon-hoc?hocKy={hocKy}`

**Mô tả**: Lấy danh sách môn học được mở trong học kỳ cụ thể

**Query Parameters**:
- `hocKy` (string): Mã học kỳ (ví dụ: "2025-1")

**Example**: `GET /api/mon-hoc/mon-hoc?hocKy=2025-1`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy môn học trong học kỳ thành công",
  "result": [
    {
      "maMH": "IT1110",
      "tenMH": "Nhập môn lập trình",
      "soTinChi": 3
    }
  ]
}
```

---

### 3.3. Tạo môn học mới
**Endpoint**: `POST /api/mon-hoc`

**Mô tả**: Tạo môn học mới (yêu cầu quyền admin)

**Request Body**:
```json
{
  "maMH": "IT1110",
  "tenMH": "Nhập môn lập trình",
  "soTinChi": 3
}
```

**Validation**:
- `maMH`: Tối thiểu 6 ký tự

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Tạo môn học thành công",
  "result": {
    "maMH": "IT1110",
    "tenMH": "Nhập môn lập trình",
    "soTinChi": 3
  }
}
```

---

## 4. Lớp Học Phần API

### 4.1. Lấy lớp học phần theo môn học
**Endpoint**: `GET /api/lop-hoc-phan?maMH={maMH}&hocKy={hocKy}`

**Mô tả**: Lấy danh sách các lớp học phần của một môn học trong học kỳ

**Query Parameters**:
- `maMH` (string): Mã môn học
- `hocKy` (string): Mã học kỳ

**Example**: `GET /api/lop-hoc-phan?maMH=IT1110&hocKy=2025-1`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy lớp học phần thành công",
  "result": [
    {
      "maLop": "IT1110-01",
      "tenLop": "Nhập môn lập trình - Lớp 01",
      "soSvToiDa": 50,
      "soSvHienTai": 35,
      "khungGioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
      "phongHoc": "D3-301",
      "giangVien": "TS. Nguyen Van X"
    }
  ]
}
```

---

### 4.2. Tạo lớp học phần mới
**Endpoint**: `POST /api/lop-hoc-phan`

**Mô tả**: Tạo lớp học phần mới (yêu cầu quyền admin)

**Request Body**:
```json
{
  "maLop": "LHP006",
  "tenLop": "Mang may tinh",
  "soSvToiDa": 40,
  "khungGioHoc": "T3 7h–9h",
  "phongHoc": "A101",
  "giangVien": "Nguyễn Văn B",
  "hocKy": "2024-1",
  "maMH": "MH006"
}
```

**Validation**:
- `maLop`: Tối thiểu 6 ký tự
- `soSvToiDa`: Tối đa 50 sinh viên
- `maMH`: Bắt buộc

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Tạo lớp học phần thành công",
  "result": {
    "maLop": "IT1110-01",
    "tenLop": "Nhập môn lập trình - Lớp 01",
    "soSvToiDa": 50,
    "soSvHienTai": 0,
    "khungGioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
    "phongHoc": "D3-301",
    "giangVien": "TS. Nguyen Van X"
  }
}
```

---

### 4.3. Lấy tất cả lớp học phần (phân trang)
**Endpoint**: `GET /api/lop-hoc-phan/all-sorted?page={page}&size={size}`

**Mô tả**: Lấy danh sách tất cả lớp học phần có phân trang và sắp xếp

**Query Parameters**:
- `page` (int): Số trang (mặc định: 0)
- `size` (int): Số lượng phần tử trên mỗi trang (mặc định: 5)

**Example**: `GET /api/lop-hoc-phan/all-sorted?page=0&size=10`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy danh sách lớp học phần thành công",
  "result": {
    "content": [
      {
        "maLop": "IT1110-01",
        "tenLop": "Nhập môn lập trình - Lớp 01",
        "soSvToiDa": 50,
        "soSvHienTai": 35,
        "khungGioHoc": "Thứ 2: 8h-10h",
        "phongHoc": "D3-301",
        "giangVien": "TS. Nguyen Van X"
      }
    ],
    "pageable": {
      "pageNumber": 0,
      "pageSize": 10
    },
    "totalElements": 25,
    "totalPages": 3
  }
}
```

---

## 5. Đăng Ký Môn Học API

### 5.1. Lấy phiếu đăng ký
**Endpoint**: `GET /api/dang-ky/{hocKy}`

**Mô tả**: Lấy phiếu đăng ký (kết quả đã nộp) của sinh viên hiện tại

**Headers**:
```
Authorization: Bearer <token>
```

**Path Parameters**:
- `hocKy` (string): Mã học kỳ

**Example**: `GET /api/dang-ky/2025-1`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy phiếu đăng ký thành công",
  "result": {
    "maSV": "20210001",
    "tenSV": "Nguyen Van A",
    "khoaHoc": "2021",
    "hocKy": "2025-1",
    "danhSachMHDaDangKy": [
      {
        "maMH": "IT1110",
        "tenMH": "Nhập môn lập trình",
        "soTinChi": 3,
        "gioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
        "giangVien": "TS. Nguyen Van X",
        "tenLop": "IT1110-01",
        "phongHoc": "D3-301"
      },
      {
        "maMH": "IT2020",
        "tenMH": "Cấu trúc dữ liệu",
        "soTinChi": 4,
        "gioHoc": "Thứ 3: 10h-12h",
        "giangVien": "TS. Tran Thi Y",
        "tenLop": "IT2020-02",
        "phongHoc": "D3-302"
      }
    ],
    "tongSoTinChi": 7
  }
}
```

---

### 5.2. Xóa phiếu đăng ký
**Endpoint**: `DELETE /api/dang-ky/{hocKy}`

**Mô tả**: Xóa toàn bộ phiếu đăng ký của sinh viên hiện tại trong học kỳ

**Headers**:
```
Authorization: Bearer <token>
```

**Path Parameters**:
- `hocKy` (string): Mã học kỳ

**Example**: `DELETE /api/dang-ky/2025-1`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Xóa phiếu đăng ký thành công",
  "result": "Đã xóa phiếu đăng ký học kỳ 2025-1"
}
```

---

### 5.3. Lấy draft (bản nháp)
**Endpoint**: `GET /api/dang-ky/draft/{hocKy}`

**Mô tả**: Lấy danh sách lớp đã chọn nhưng chưa nộp của sinh viên hiện tại

**Headers**:
```
Authorization: Bearer <token>
```

**Path Parameters**:
- `hocKy` (string): Mã học kỳ

**Example**: `GET /api/dang-ky/draft/2025-1`

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Lấy draft thành công",
  "result": {
    "maSV": "20210001",
    "hocKy": "2025-1",
    "danhSachLopDaChon": [
      {
        "maLop": "IT1110-01",
        "tenLop": "Nhập môn lập trình - Lớp 01",
        "maMH": "IT1110",
        "tenMH": "Nhập môn lập trình",
        "soTinChi": 3,
        "khungGioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
        "giangVien": "TS. Nguyen Van X",
        "phongHoc": "D3-301"
      }
    ],
    "tongSoTinChi": 3,
    "duDieuKienNop": false
  }
}
```

**Giải thích**:
- `duDieuKienNop`: `true` nếu tổng số tín chỉ từ 10-15, `false` nếu không đủ điều kiện

---

### 5.4. Thêm lớp vào draft
**Endpoint**: `POST /api/dang-ky/draft/add`

**Mô tả**: Thêm một lớp học phần vào draft (kiểm tra xung đột thời gian)

**Headers**:
```
Authorization: Bearer <token>
```

**Request Body**:
```json
{
  "hocKy": "2025-1",
  "maLop": "IT1110-01"
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Thêm lớp vào draft thành công",
  "result": {
    "maSV": "20210001",
    "hocKy": "2025-1",
    "danhSachLopDaChon": [
      {
        "maLop": "IT1110-01",
        "tenLop": "Nhập môn lập trình - Lớp 01",
        "maMH": "IT1110",
        "tenMH": "Nhập môn lập trình",
        "soTinChi": 3,
        "khungGioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
        "giangVien": "TS. Nguyen Van X",
        "phongHoc": "D3-301"
      }
    ],
    "tongSoTinChi": 3,
    "duDieuKienNop": false
  }
}
```

**Lỗi có thể gặp**:
- `409 Conflict`: Xung đột thời gian với lớp đã chọn
- `400 Bad Request`: Lớp đã đầy hoặc không tồn tại

---

### 5.5. Xóa lớp khỏi draft
**Endpoint**: `DELETE /api/dang-ky/draft/remove`

**Mô tả**: Xóa một lớp học phần khỏi draft

**Headers**:
```
Authorization: Bearer <token>
```

**Request Body**:
```json
{
  "hocKy": "2025-1",
  "maLop": "IT1110-01"
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Xóa lớp khỏi draft thành công",
  "result": {
    "maSV": "20210001",
    "hocKy": "2025-1",
    "danhSachLopDaChon": [],
    "tongSoTinChi": 0,
    "duDieuKienNop": false
  }
}
```

---

### 5.6. Nộp phiếu đăng ký (Submit draft)
**Endpoint**: `POST /api/dang-ky/draft/submit`

**Mô tả**: Nộp phiếu đăng ký (lưu draft vào database, kiểm tra tín chỉ 10-15)

**Headers**:
```
Authorization: Bearer <token>
```

**Request Body**:
```json
{
  "hocKy": "2025-1"
}
```

**Response Success (200)**:
```json
{
  "code": 1000,
  "message": "Nộp phiếu đăng ký thành công",
  "result": {
    "maSV": "20210001",
    "tenSV": "Nguyen Van A",
    "khoaHoc": "2021",
    "hocKy": "2025-1",
    "danhSachMHDaDangKy": [
      {
        "maMH": "IT1110",
        "tenMH": "Nhập môn lập trình",
        "soTinChi": 3,
        "gioHoc": "Thứ 2: 8h-10h, Thứ 5: 14h-16h",
        "giangVien": "TS. Nguyen Van X",
        "tenLop": "IT1110-01",
        "phongHoc": "D3-301"
      },
      {
        "maMH": "IT2020",
        "tenMH": "Cấu trúc dữ liệu",
        "soTinChi": 4,
        "gioHoc": "Thứ 3: 10h-12h",
        "giangVien": "TS. Tran Thi Y",
        "tenLop": "IT2020-02",
        "phongHoc": "D3-302"
      }
    ],
    "tongSoTinChi": 12
  }
}
```

**Lỗi có thể gặp**:
- `400 Bad Request`: Số tín chỉ không hợp lệ (phải từ 10-15)
- `400 Bad Request`: Draft rỗng

---

## 6. Error Codes

Hệ thống sử dụng các mã lỗi chuẩn:

| HTTP Status | Code | Message | Mô tả |
|-------------|------|---------|-------|
| 200 | 1000 | Success | Thành công |
| 400 | 1001 | Bad Request | Dữ liệu đầu vào không hợp lệ |
| 401 | 1002 | Unauthorized | Chưa xác thực hoặc token không hợp lệ |
| 403 | 1003 | Forbidden | Không có quyền truy cập |
| 404 | 1004 | Not Found | Không tìm thấy tài nguyên |
| 409 | 1005 | Conflict | Xung đột dữ liệu (ví dụ: xung đột thời gian) |
| 500 | 9999 | Internal Server Error | Lỗi server |


---

## 8. Authentication & Authorization

### JWT Token
- Tất cả các API (trừ login và register) đều yêu cầu JWT token
- Token được truyền qua header: `Authorization: Bearer <token>`
- Token có thời gian hết hạn (được cấu hình trong server)

### Roles
- **USER**: Sinh viên thông thường - có thể xem và đăng ký môn học
- **ADMIN**: Quản trị viên - có thể tạo môn học, lớp học phần

---

## 9. Business Rules

### Đăng ký môn học
1. **Số tín chỉ**: Sinh viên phải đăng ký từ 10-15 tín chỉ mỗi học kỳ
2. **Xung đột thời gian**: Không được đăng ký các lớp có thời gian trùng nhau
3. **Sức chứa lớp**: Lớp học phần có tối đa 50 sinh viên
4. **Draft**: Sinh viên có thể thêm/xóa lớp trong draft trước khi nộp chính thức
5. **Xóa phiếu đăng ký**: Có thể xóa toàn bộ phiếu đăng ký đã nộp

---

## 10. Examples với cURL

### Đăng ký tài khoản
```bash
curl -X POST http://localhost:8080/api/user \
  -H "Content-Type: application/json" \
  -d '{
    "maSV": "20210001",
    "ten": "Nguyen Van A",
    "matKhau": "password123"
  }'
```

### Đăng nhập
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "maSV": "20210001",
    "matKhau": "password123"
  }'
```

### Lấy danh sách môn học
```bash
curl -X GET "http://localhost:8080/api/mon-hoc/mon-hoc?hocKy=2025-1"
```

### Thêm lớp vào draft
```bash
curl -X POST http://localhost:8080/api/dang-ky/draft/add \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "hocKy": "2025-1",
    "maLop": "IT1110-01"
  }'
```

### Nộp phiếu đăng ký
```bash
curl -X POST http://localhost:8080/api/dang-ky/draft/submit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "hocKy": "2025-1"
  }'
```

---

## 11. Notes

- API base URL: `http://localhost:8080`
- Port có thể thay đổi tùy theo cấu hình trong `application.yaml`
- Tất cả request và response đều sử dụng `Content-Type: application/json`
- Mã sinh viên (`maSV`) được sử dụng làm username
- Token JWT được tự động parse để lấy thông tin sinh viên hiện tại

---


