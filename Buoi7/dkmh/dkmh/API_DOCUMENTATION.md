# API Documentation - Hệ Thống Đăng Ký Môn Học

## Tổng Quan Các Phương Pháp Truy Vấn Được Sử Dụng

### 1. **Query Method** (Truy vấn bằng tên method)
- `findBySinhVien_MaSVAndHocKy()` - Tìm phiếu đăng ký theo mã sinh viên và học kỳ
- `existsBySinhVienAndLopHocPhanAndHocKy()` - Kiểm tra sinh viên đã đăng ký lớp chưa

### 2. **@Query JPQL** (Java Persistence Query Language)
- `findByMonHocAndHocKy()` - Tìm lớp học phần theo mã môn học và học kỳ
- `findByMaSVAndMaLopAndHocKy()` - Tìm phiếu đăng ký cụ thể
- `deleteByMaSVAndMaLopAndHocKy()` - Xóa phiếu đăng ký cụ thể
- `deleteBySinhVienAndHocKy()` - Xóa tất cả phiếu đăng ký của sinh viên

### 3. **Native Query** (SQL thuần)
- `incrementSoSvHienTai()` - Tăng số sinh viên hiện tại
- `decrementSoSvHienTai()` - Giảm số sinh viên hiện tại
- `countRegistrationsByMaSVAndHocKy()` - Đếm số môn đã đăng ký
- `findMaLopByMaSVAndHocKy()` - Lấy danh sách mã lớp đã đăng ký

---

## APIs Endpoint

### 1. **Lấy Tất Cả Môn Học**
```http
GET /api/dang-ky/tat-ca-mon-hoc
```

**Response:**
```json
[
  {
    "maMH": "IT1110",
    "tenMH": "Nhập môn lập trình",
    "soTinChi": 3
  }
]
```

---

### 2. **Lấy Môn Học Theo Học Kỳ**
```http
GET /api/dang-ky/mon-hoc?hocKy=2025-1
```

**Parameters:**
- `hocKy` (String): Mã học kỳ (VD: "2025-1")

**Response:**
```json
[
  {
    "maMH": "IT1110",
    "tenMH": "Nhập môn lập trình",
    "soTinChi": 3
  }
]
```

---

### 3. **Lấy Lớp Học Phần Theo Môn Học**
```http
GET /api/dang-ky/lop-hoc-phan?maMH=IT1110&hocKy=2025-1
```

**Parameters:**
- `maMH` (String): Mã môn học
- `hocKy` (String): Mã học kỳ

**Response:**
```json
[
  {
    "maLop": "IT1110-01",
    "tenLop": "Lớp 01",
    "soSvToiDa": 50,
    "soSvHienTai": 30,
    "khungGioHoc": "T2(1-3)",
    "phongHoc": "101-D8",
    "giangVien": "Nguyễn Văn A"
  }
]
```

---

### 4. **Lấy Phiếu Đăng Ký Của Sinh Viên**
```http
GET /api/dang-ky/{maSV}/{hocKy}
```

**Path Variables:**
- `maSV` (String): Mã sinh viên
- `hocKy` (String): Mã học kỳ

**Example:**
```http
GET /api/dang-ky/SV001/2025-1
```

**Response:**
```json
{
  "maSV": "SV001",
  "tenSV": "Nguyễn Văn A",
  "khoaHoc": "Khoa CNTT",
  "hocKy": "2025-1",
  "tongSoTinChi": 12,
  "danhSachDaDangKy": [
    {
      "maMH": "IT1110",
      "tenMH": "Nhập môn lập trình",
      "soTinChi": 3,
      "khungGioHoc": "T2(1-3)",
      "giangVien": "Nguyễn Văn A",
      "tenLop": "Lớp 01",
      "phongHoc": "101-D8"
    }
  ]
}
```

---

### 5. **TẠO Phiếu Đăng Ký Mới** ⭐
```http
POST /api/dang-ky
Content-Type: application/json
```

**Request Body:**
```json
{
  "maSV": "SV001",
  "hocKy": "2025-1",
  "danhSachMaLop": ["IT1110-01", "IT1120-02", "IT1130-01"]
}
```

**Response (Success - 201 Created):**
```json
{
  "maSV": "SV001",
  "tenSV": "Nguyễn Văn A",
  "khoaHoc": "Khoa CNTT",
  "hocKy": "2025-1",
  "tongSoTinChi": 9,
  "danhSachDaDangKy": [...]
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "error": "Lớp IT1110-01 đã đầy"
}
```

**Các Phương Pháp Truy Vấn Được Sử Dụng:**
- ✅ Query Method: `findById()`, `existsBySinhVienAndLopHocPhanAndHocKy()`
- ✅ @Query JPQL: `deleteBySinhVienAndHocKy()`
- ✅ Native Query: `countRegistrationsByMaSVAndHocKy()`, `findMaLopByMaSVAndHocKy()`, `incrementSoSvHienTai()`, `decrementSoSvHienTai()`

**Các Ràng Buộc:**
- Sinh viên tồn tại
- Lớp học phần tồn tại
- Lớp còn chỗ trống
- Không được đăng ký quá 10 môn
- Không đăng ký trùng lớp

---

### 6. **SỬA Phiếu Đăng Ký** ⭐
```http
PUT /api/dang-ky
Content-Type: application/json
```

**Request Body:**
```json
{
  "maSV": "SV001",
  "hocKy": "2025-1",
  "danhSachMaLop": ["IT1110-01", "IT1130-01", "IT1140-01"]
}
```

**Mô Tả:**
- API này sẽ so sánh danh sách lớp cũ và mới
- Tự động XÓA các lớp không còn trong danh sách mới
- Tự động THÊM các lớp mới vào danh sách
- Giữ nguyên các lớp đã có

**Ví Dụ:**
- Danh sách cũ: `["IT1110-01", "IT1120-02"]`
- Danh sách mới: `["IT1110-01", "IT1130-01"]`
- Kết quả: Xóa `IT1120-02`, Giữ `IT1110-01`, Thêm `IT1130-01`

**Response (Success - 200 OK):**
```json
{
  "maSV": "SV001",
  "tenSV": "Nguyễn Văn A",
  "khoaHoc": "Khoa CNTT",
  "hocKy": "2025-1",
  "tongSoTinChi": 9,
  "danhSachDaDangKy": [...]
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "error": "Không tìm thấy phiếu đăng ký cũ"
}
```

**Các Phương Pháp Truy Vấn Được Sử Dụng:**
- ✅ Query Method: `findById()`
- ✅ @Query JPQL: `deleteByMaSVAndMaLopAndHocKy()`
- ✅ Native Query: `findMaLopByMaSVAndHocKy()`, `incrementSoSvHienTai()`, `decrementSoSvHienTai()`

---

### 7. **XÓA Toàn Bộ Phiếu Đăng Ký** ⭐
```http
DELETE /api/dang-ky/{maSV}/{hocKy}
```

**Path Variables:**
- `maSV` (String): Mã sinh viên
- `hocKy` (String): Mã học kỳ

**Example:**
```http
DELETE /api/dang-ky/SV001/2025-1
```

**Response (Success - 200 OK):**
```json
{
  "message": "Đã xóa thành công 3 môn học trong phiếu đăng ký của sinh viên SV001 học kỳ 2025-1"
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "error": "Không tìm thấy phiếu đăng ký để xóa"
}
```

**Các Phương Pháp Truy Vấn Được Sử Dụng:**
- ✅ Query Method: `findById()`
- ✅ @Query JPQL: `deleteBySinhVienAndHocKy()`
- ✅ Native Query: `findMaLopByMaSVAndHocKy()`, `decrementSoSvHienTai()`

**Mô Tả:**
- Xóa tất cả phiếu đăng ký của sinh viên trong học kỳ
- Tự động giảm số sinh viên hiện tại của các lớp đã đăng ký

---

### 8. **XÓA Một Môn Học Khỏi Phiếu Đăng Ký** ⭐
```http
DELETE /api/dang-ky/{maSV}/{hocKy}/{maLop}
```

**Path Variables:**
- `maSV` (String): Mã sinh viên
- `hocKy` (String): Mã học kỳ
- `maLop` (String): Mã lớp học phần

**Example:**
```http
DELETE /api/dang-ky/SV001/2025-1/IT1110-01
```

**Response (Success - 200 OK):**
```json
{
  "maSV": "SV001",
  "tenSV": "Nguyễn Văn A",
  "khoaHoc": "Khoa CNTT",
  "hocKy": "2025-1",
  "tongSoTinChi": 6,
  "danhSachDaDangKy": [
    // Danh sách môn còn lại sau khi xóa
  ]
}
```

**Response (Error - 400 Bad Request):**
```json
{
  "error": "Không tìm thấy phiếu đăng ký cho lớp IT1110-01"
}
```

**Các Phương Pháp Truy Vấn Được Sử Dụng:**
- ✅ @Query JPQL: `findByMaSVAndMaLopAndHocKy()`, `deleteByMaSVAndMaLopAndHocKy()`
- ✅ Native Query: `decrementSoSvHienTai()`

**Mô Tả:**
- Xóa một môn học cụ thể khỏi phiếu đăng ký
- Giữ nguyên các môn khác
- Tự động giảm số sinh viên hiện tại của lớp

---

## Tổng Kết Các Phương Pháp Truy Vấn

| Loại Truy Vấn | Số Lượng Method | Ví Dụ |
|---------------|-----------------|-------|
| **Query Method** | 3 | `findById()`, `existsBySinhVienAndLopHocPhanAndHocKy()` |
| **@Query JPQL** | 5 | `findByMonHocAndHocKy()`, `deleteBySinhVienAndHocKy()` |
| **Native Query** | 4 | `incrementSoSvHienTai()`, `countRegistrationsByMaSVAndHocKy()` |

---

## Testing với Postman/cURL

### 1. Tạo Phiếu Đăng Ký
```bash
curl -X POST http://localhost:8080/api/dang-ky \
  -H "Content-Type: application/json" \
  -d '{
    "maSV": "SV001",
    "hocKy": "2025-1",
    "danhSachMaLop": ["IT1110-01", "IT1120-02"]
  }'
```

### 2. Sửa Phiếu Đăng Ký
```bash
curl -X PUT http://localhost:8080/api/dang-ky \
  -H "Content-Type: application/json" \
  -d '{
    "maSV": "SV001",
    "hocKy": "2025-1",
    "danhSachMaLop": ["IT1110-01", "IT1130-01", "IT1140-01"]
  }'
```

### 3. Xóa Toàn Bộ Phiếu Đăng Ký
```bash
curl -X DELETE http://localhost:8080/api/dang-ky/SV001/2025-1
```

### 4. Xóa Một Môn Khỏi Phiếu Đăng Ký
```bash
curl -X DELETE http://localhost:8080/api/dang-ky/SV001/2025-1/IT1110-01
```

---

## Error Handling

Tất cả các API đều xử lý lỗi và trả về dạng:

```json
{
  "error": "Mô tả lỗi chi tiết"
}
```

**Các Lỗi Thường Gặp:**
- `Không tìm thấy sinh viên: {maSV}`
- `Không tìm thấy lớp học phần: {maLop}`
- `Lớp {maLop} đã đầy`
- `Sinh viên đã đăng ký lớp {maLop}`
- `Vượt quá số lượng môn học tối đa (10 môn)`
- `Không tìm thấy phiếu đăng ký cũ`
- `Không tìm thấy phiếu đăng ký để xóa`

---

## Database Changes

Các thao tác tự động cập nhật database:
- ✅ Tăng/giảm `soSvHienTai` khi đăng ký/hủy đăng ký
- ✅ Transaction đảm bảo tính toàn vẹn dữ liệu
- ✅ Cascade delete khi xóa phiếu đăng ký

---

## Notes

1. Tất cả các method trong Service đều có `@Transactional` để đảm bảo tính toàn vẹn dữ liệu
2. API sử dụng đa dạng cách truy vấn: Query Method, @Query JPQL, Native Query
3. Hệ thống tự động quản lý số lượng sinh viên trong lớp
4. Có validation đầy đủ trước khi thực hiện thao tác
5. Response luôn trả về phiếu đăng ký đầy đủ sau khi thao tác

