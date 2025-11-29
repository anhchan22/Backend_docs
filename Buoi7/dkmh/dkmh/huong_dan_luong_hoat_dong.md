# Hướng dẫn luồng hoạt động hệ thống ĐKMH

Tài liệu này tóm tắt luồng xử lý và các API chính dựa trên mã nguồn trong dự án.

I. Tổng quan kiến trúc
- Spring Boot 3, Java 21
- Layers:
  - Controller: nhận/ trả JSON theo chuẩn ApiResponse
  - Service: xử lý nghiệp vụ, validate, gọi repository
  - Repository: Spring Data JPA thao tác DB
  - Mapper: MapStruct map giữa Request/Entity/Response
  - Entity: ánh xạ bảng dữ liệu
  - DTO Request/Response: model cho vào/ra API

II. Các module/packge chính
- com.qlsv.dkmh.controller: MonHocController, LopHocPhanController, RegistrationController
- com.qlsv.dkmh.service: MonHocService, LopHocPhanService, RegistrationService
- com.qlsv.dkmh.repository: MonHocRepository, LopHocPhanRepository, PhieuDangKyRepository, SinhVienRepository
- com.qlsv.dkmh.mapper: MonHocMapper, LopHocPhanMapper, PhieuDangKyMapper
- com.qlsv.dkmh.dto.request: MonHocRequest, LopHocPhanRequest, DangKyRequest, ThemLopVaoDraftRequest, NopPhieuDangKyRequest
- com.qlsv.dkmh.dto.response: ApiResponse, MonHocResponse, LopHocPhanResponse, PhieuDangKyResponse, DraftResponse

III. Chuẩn ApiResponse
- Mọi API trả về:
  {
    "code": 1000,
    "message": "...",
    "result": <payload>
  }
- code=1000: thành công. Nếu phát sinh lỗi nghiệp vụ sẽ ném AppException(ErrorCode.XXX) và GlobalExceptionHandler chuyển về lỗi phù hợp.

IV. Luồng chức năng (đã chuẩn hóa theo yêu cầu)
1) Môn học (MonHoc)
- GET /api/mon-hoc/tat-ca-mon-hoc
  - Controller: MonHocController.getAllMonHoc
  - Service: MonHocService.getAllMonHocDTO -> repo.findAllBy() -> map to MonHocResponse
- GET /api/mon-hoc/mon-hoc?hocKy=2025-1
  - Controller: MonHocController.getMonHocHocKy
  - Service: MonHocService.getMonHocByHocKyDTO(hocKy) -> repo.findMonHocByHocKy(hocKy) -> map to MonHocResponse
- POST /api/mon-hoc
  - Body: { maMH, tenMH, soTinChi }
  - Controller: MonHocController.createMonHoc
  - Service: MonHocService.createMonHoc(request)
    - Validate trùng mã (existsById)
    - MapStruct MonHocMapper.toEntity(request)
    - repo.save(entity)
    - map to MonHocResponse

2) Lớp học phần (LopHocPhan)
- GET /api/lop-hoc-phan/lop-hoc-phan?maMH=...&hocKy=...
  - Controller: LopHocPhanController.getLopHocPhan
  - Service: LopHocPhanService.getLopHocPhanByMonHoc(maMH, hocKy)
    - Repo query theo mã môn + học kỳ
    - MapStruct LopHocPhanMapper -> LopHocPhanResponse
- POST /api/lop-hoc-phan
  - Body: LopHocPhanRequest
  - Controller: LopHocPhanController.createLopHocPhan
  - Service: LopHocPhanService.createLopHocPhan
    - Validate dữ liệu, ràng buộc với MonHoc
    - repo.save -> map response

3) Đăng ký môn học (Registration)
- Xem phiếu đăng ký đã nộp
  - GET /api/dang-ky/{maSV}/{hocKy}
  - RegistrationController.getPhieuDangKy -> RegistrationService.getPhieuDangKy
  - Trả về PhieuDangKyResponse (gồm danh sách môn đã đăng ký)
- Chỉ cho phép SỬA phiếu đăng ký đã nộp
  - PUT /api/dang-ky
  - Body: DangKyRequest { maSV, hocKy, danhSachMaLop }
  - RegistrationService.updatePhieuDangKy:
    - Kiểm tra tồn tại phiếu hiện tại
    - Kiểm tra xung đột thời khóa biểu giữa các lớp trong danh sách mong muốn
    - Kiểm tra tổng tín chỉ sau cập nhật phải nằm trong [10, 15]
    - Kiểm tra lớp đầy khi thêm mới; xóa/ thêm chênh lệch so với danh sách hiện có
  - (ĐÃ BỎ) POST /api/dang-ky (tạo mới) và DELETE /api/dang-ky/... (xóa phiếu hoặc xóa môn)

4) DRAFT đăng ký (chưa nộp)
- GET /api/dang-ky/draft/{maSV}/{hocKy}
  - Trả về DraftResponse gồm danh sách lớp đã chọn tạm thời và tổng số tín chỉ, cờ đủ điều kiện nộp
- POST /api/dang-ky/draft/add
  - Body: { maSV, hocKy, maLop }
  - Kiểm tra lớp đầy; kiểm tra xung đột thời gian với các lớp đã chọn trong draft; tăng tạm số SV lớp; thêm vào draft
- DELETE /api/dang-ky/draft/remove
  - Body: { maSV, hocKy, maLop }
  - Gỡ khỏi draft; giảm tạm số SV lớp
- POST /api/dang-ky/draft/submit
  - Body: { maSV, hocKy }
  - Kiểm tra tổng tín chỉ 10–15; xóa phiếu cũ (nếu có) và ghi lại phiếu mới theo draft; xóa draft
- (ĐÃ BỎ) DELETE /api/dang-ky/draft/{maSV}/{hocKy} – xóa toàn bộ draft

5) Mapping (MapStruct)
- MonHocMapper: map MonHocRequest <-> MonHoc <-> MonHocResponse
- LopHocPhanMapper: map Entity -> Response, Request -> Entity
- PhieuDangKyMapper:
  - toMonHocDaDangKyResponse(PhieuDangKy): lấy thông tin từ PhieuDangKy.lopHocPhan.monHoc ... để build item response
  - toEntity(DangKyRequest) ignore các trường liên kết: sinhVien, lopHocPhan (phải set thủ công ở service)

Lý do phải set thủ công sinhVien, lopHocPhan ở service:
- Request chỉ có mã (String) nhưng Entity yêu cầu object đầy đủ -> cần truy vấn DB để lấy entity tương ứng
- MapStruct không thực hiện truy vấn DB
- Service chịu trách nhiệm validate (tồn tại, hết chỗ, xung đột thời gian, học kỳ hợp lệ, ...)

V. Các lỗi thường gặp và cách kiểm tra
- 404/405 khi gọi /api/mon-hoc: đảm bảo phương thức và path đúng:
  - Lấy tất cả: GET /api/mon-hoc/tat-ca-mon-hoc
  - Lấy theo học kỳ: GET /api/mon-hoc/mon-hoc?hocKy=2025-1
  - Tạo mới: POST /api/mon-hoc (body JSON đúng schema)
- 500 khi GET /api/dang-ky/mon-hoc?hocKy=...: hiện không có endpoint như vậy; endpoint đúng nằm ở MonHocController.getMonHocHocKy -> /api/mon-hoc/mon-hoc
- Port 8080 in use: dừng tiến trình đang chiếm 8080 hoặc đổi server.port trong application.yaml

VI. Dọn dẹp cấu trúc
- Dự án Maven chuẩn nằm tại thư mục dkmh/ (module). Thư mục src/ trôi nổi ngoài module (D:/.../dkmh/src/main/java/...) là dư thừa do trùng tên gốc. Giữ mã nguồn trong module Maven dkmh/dkmh để tránh nhầm lẫn class trùng.

VII. Cách mở rộng
- Thêm DTO/Mapper tương ứng khi thêm field mới
- Viết kiểm thử cho RegistrationService: xung đột lịch, giới hạn tín chỉ 10–15, lớp đầy
- Bổ sung GlobalExceptionHandler để chuẩn hóa lỗi nghiệp vụ

VIII. Tham chiếu lớp/DTO phản hồi Draft
- DraftResponse gồm:
  - maSV, hocKy
  - danhSachLopDaChon: list item { maLop, tenLop, maMH, tenMH, soTinChi, khungGioHoc, giangVien, phongHoc }
  - tongSoTinChi, duDieuKienNop (>=10 && <=15)

Tài liệu này phản ánh đúng các controller/service hiện có trong mã nguồn tại thời điểm viết.
