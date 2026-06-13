# HƯỚNG DẪN TEST TOÀN BỘ PROJECT SMART HEALTHCARE

> Project: Smart Healthcare Platform  
> Kiểu project: Spring Boot MVC + Thymeleaf + MySQL + Bootstrap 4  
> Không dùng `@RestController`, không dùng JavaScript.  
> Phân quyền bằng `Session` + `Interceptor`.

---

# 1. Chuẩn bị trước khi test

## 1.1. Kiểm tra MySQL

Mở MySQL và đảm bảo database có thể tạo được:

```sql
CREATE DATABASE IF NOT EXISTS smart_healthcare_db;
```

Trong `application.properties` kiểm tra:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/smart_healthcare_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=123456

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

Nếu mật khẩu MySQL của bạn khác `123456` thì phải sửa lại.

---

## 1.2.Tài khoản mẫu:

| Vai trò | Email | Mật khẩu |
|---|---|---|
| Admin | `admin@gmail.com` | `123456` |
| Doctor | `doctor@gmail.com` | `123456` |
| Patient | `patient@gmail.com` | `123456` |

Nếu đổi logic hash password thì nhớ đảm bảo password trong `DataSeeder` cũng được hash giống lúc login.

---

# 2. Chạy project

Chạy file chính:

```txt
HealthcareApplication.java
```

Nếu chạy đúng, console có dạng:

```txt
Tomcat started on port 8080
Started HealthcareApplication
```

Sau đó mở trình duyệt:

```txt
http://localhost:8080/login
```

---

# 3. Test đăng ký tài khoản bệnh nhân

## 3.1. Vào trang đăng ký

```txt
http://localhost:8080/register
```

## 3.2. Test validate đăng ký

Bấm đăng ký khi chưa nhập gì.

Kết quả đúng:

| Trường | Lỗi mong muốn |
|---|---|
| Họ tên | Vui lòng nhập họ tên |
| Email | Vui lòng nhập email |
| Mật khẩu | Vui lòng nhập mật khẩu |
| Số điện thoại | Vui lòng nhập số điện thoại |
| Địa chỉ | Vui lòng nhập địa chỉ |

Logic cần nhớ:

```txt
Dùng DTO + @Valid + BindingResult.
Nếu có lỗi validate thì trả lại form.
Message lỗi nằm ngay dưới input tương ứng.
```

## 3.3. Test email sai định dạng

Nhập:

```txt
Email: abc
```

Kết quả đúng:

```txt
Email không đúng định dạng
```

Lưu ý yêu cầu đề:

```txt
Chưa nhập thì báo "Vui lòng nhập email".
Nhập sai rồi mới báo "Email không đúng định dạng".
Không được hiện nhiều lỗi cùng lúc cho cùng một trường.
```

## 3.4. Test đăng ký thành công

Nhập ví dụ:

```txt
Họ tên: Tô Anh Tuấn
Email: totuan.18dz@gmail.com
Mật khẩu: 18062006
Số điện thoại: 0392806118
Địa chỉ: Hà Nội
```

Kết quả đúng:

```txt
Chuyển về /login?success
Hiện thông báo đăng ký thành công
```

Logic cần nhớ:

```txt
User đăng ký mặc định có role PATIENT.
Mật khẩu không lưu thô mà được hash trước khi lưu database.
```

---

# 4. Test đăng nhập và Session

## 4.1. Đăng nhập Admin

Vào:

```txt
http://localhost:8080/login
```

Nhập:

```txt
Email: admin@gmail.com
Password: 123456
```

Kết quả đúng:

```txt
Chuyển sang /admin/dashboard
```

## 4.2. Đăng nhập Doctor

```txt
doctor@gmail.com / 123456
```

Kết quả đúng:

```txt
Chuyển sang /doctor/dashboard
```

## 4.3. Đăng nhập Patient

```txt
patient@gmail.com / 123456
```

Kết quả đúng:

```txt
Chuyển sang /patient/dashboard
```

Logic cần nhớ:

```java
session.setAttribute("currentUser", user);
session.setAttribute("role", user.getRole().name());
```

Giải thích:

```txt
Sau khi đăng nhập, hệ thống lưu user và role vào session.
Những request sau đó sẽ đọc session để biết người dùng là ai và có quyền gì.
```

---

# 5. Test phân quyền bằng Interceptor

## 5.1. Test Admin

Đăng nhập Admin rồi thử vào:

```txt
http://localhost:8080/admin/dashboard
http://localhost:8080/doctor/dashboard
http://localhost:8080/patient/dashboard
```

Kết quả đúng:

| URL | Kết quả |
|---|---|
| `/admin/dashboard` | Vào được |
| `/doctor/dashboard` | Bị chặn |
| `/patient/dashboard` | Bị chặn |

## 5.2. Test Doctor

Đăng nhập Doctor rồi thử:

```txt
http://localhost:8080/doctor/dashboard
http://localhost:8080/admin/dashboard
http://localhost:8080/patient/dashboard
```

Kết quả đúng:

| URL | Kết quả |
|---|---|
| `/doctor/dashboard` | Vào được |
| `/admin/dashboard` | Bị chặn |
| `/patient/dashboard` | Bị chặn |

## 5.3. Test Patient

Đăng nhập Patient rồi thử:

```txt
http://localhost:8080/patient/dashboard
http://localhost:8080/admin/dashboard
http://localhost:8080/doctor/dashboard
```

Kết quả đúng:

| URL | Kết quả |
|---|---|
| `/patient/dashboard` | Vào được |
| `/admin/dashboard` | Bị chặn |
| `/doctor/dashboard` | Bị chặn |

Logic cần nhớ:

```txt
AuthInterceptor chạy trước Controller.
Nếu chưa đăng nhập thì chuyển về /login.
Nếu vào sai role thì chuyển sang /access-denied.
```

Ví dụ logic:

```java
if (uri.startsWith("/admin") && !"ADMIN".equals(role)) {
    response.sendRedirect("/access-denied");
    return false;
}
```

---

# 6. Test phân quyền nút bấm trên giao diện

Đây là phần mở rộng Hướng 2: Advanced Security.

## 6.1. Admin

Đăng nhập Admin.

Kết quả đúng:

```txt
Chỉ thấy nút:
- Quản lý thuốc
- Cấp phát thuốc
```

## 6.2. Doctor

Đăng nhập Doctor.

Kết quả đúng:

```txt
Chỉ thấy nút:
- Danh sách chờ khám
```

## 6.3. Patient

Đăng nhập Patient.

Kết quả đúng:

```txt
Chỉ thấy nút:
- Đặt lịch khám
- Lịch khám của tôi
- Lịch sử bệnh án
```

Logic cần nhớ:

```html
<a th:if="${role == 'ADMIN'}" href="/admin/medicines">
    Quản lý thuốc
</a>
```

Giải thích:

```txt
Interceptor chặn ở đường dẫn.
Thymeleaf th:if ẩn/hiện nút ở giao diện.
Như vậy hệ thống có phân quyền cả View và URI.
```

---

# 7. Test Admin quản lý thuốc

Đăng nhập Admin.

Vào:

```txt
http://localhost:8080/admin/medicines
```

## 7.1. Test danh sách thuốc

Kết quả đúng:

```txt
Hiện danh sách thuốc chưa bị xóa mềm.
```

Logic cần nhớ:

```java
List<Medicine> findByDeletedFalse();
```

Thuốc bị xóa mềm thì:

```java
medicine.setDeleted(true);
```

không xóa thật khỏi database.

---

## 7.2. Test thêm thuốc

Vào:

```txt
http://localhost:8080/admin/medicines/create
```

Nhập:

```txt
Tên thuốc: Amoxicillin
Nhà sản xuất: DHG Pharma
Giá thuốc: 25000
Số lượng tồn kho: 100
```

Kết quả đúng:

```txt
Thêm thành công và quay lại danh sách thuốc.
```

---

## 7.3. Test validate thêm thuốc

Bỏ trống form và bấm lưu.

Kết quả đúng:

| Trường | Lỗi mong muốn |
|---|---|
| Tên thuốc | Vui lòng nhập tên thuốc |
| Nhà sản xuất | Vui lòng nhập nhà sản xuất |
| Giá thuốc | Vui lòng nhập giá thuốc |
| Tồn kho | Vui lòng nhập số lượng tồn kho |

Nhập giá nhỏ hơn 1000:

```txt
Giá thuốc: 500
```

Kết quả đúng:

```txt
Giá thuốc phải từ 1000 trở lên
```

Nhập tồn kho âm:

```txt
Số lượng tồn kho: -1
```

Kết quả đúng:

```txt
Số lượng tồn kho không được âm
```

---

## 7.4. Test sửa thuốc

Bấm nút:

```txt
Sửa
```

Sửa ví dụ:

```txt
Giá thuốc: 30000
Tồn kho: 120
```

Kết quả đúng:

```txt
Cập nhật thành công.
```

---

## 7.5. Test tìm kiếm thuốc

Nhập keyword:

```txt
para
```

Kết quả đúng:

```txt
Hiện thuốc có tên chứa "para", không phân biệt hoa thường.
```

Logic cần nhớ:

```java
findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
```

---

## 7.6. Test xóa thuốc

Bấm:

```txt
Xóa
```

Kết quả đúng:

```txt
Thuốc biến mất khỏi danh sách.
Nhưng trong database vẫn còn, deleted = true.
```

SQL kiểm tra:

```sql
SELECT * FROM medicines;
```

---

# 8. Test bệnh nhân đặt lịch khám

Đăng nhập Patient.

Vào:

```txt
http://localhost:8080/patient/appointments/create
```

---

## 8.1. Test validate form đặt lịch

Bấm đặt lịch khi chưa chọn gì.

Kết quả đúng:

| Trường | Lỗi mong muốn |
|---|---|
| Chuyên khoa | Vui lòng chọn chuyên khoa |
| Bác sĩ | Vui lòng chọn bác sĩ |
| Ngày khám | Vui lòng chọn ngày khám |
| Giờ khám | Vui lòng chọn giờ khám |

Logic cần nhớ:

```txt
Dữ liệu form đi vào AppointmentDTO.
Controller dùng @Valid để kiểm tra.
Nếu lỗi thì trả lại appointment-form.html.
```

---

## 8.2. Test không cho đặt lịch quá khứ

Chọn:

```txt
Ngày khám: ngày hôm qua
Giờ khám: bất kỳ
```

Kết quả đúng:

```txt
Ngày khám không được ở quá khứ
```

Hoặc nếu chọn hôm nay nhưng giờ đã qua:

```txt
Không được đặt lịch trong quá khứ
```

Logic cần nhớ:

```java
if (date.isBefore(today) || date.isEqual(today) && time.isBefore(now)) {
    throw new RuntimeException("Không được đặt lịch trong quá khứ");
}
```

---

## 8.3. Test đặt lịch thành công

Chọn:

```txt
Chuyên khoa: Nội tổng quát
Bác sĩ: Bác sĩ Nguyễn Văn A
Ngày khám: ngày mai
Giờ khám: 09:00
```

Kết quả đúng:

```txt
Chuyển sang /patient/appointments
Hiện lịch mới trạng thái WAITING
```

Logic cần nhớ:

```txt
Khi bệnh nhân đặt lịch thành công:
- Tạo bản ghi Appointment
- Gắn patient hiện tại từ session
- Gắn doctor đã chọn
- Status mặc định là WAITING
```

---

# 9. Test chống trùng lịch bác sĩ

Sau khi đã có lịch:

```txt
Doctor: Bác sĩ Nguyễn Văn A
Ngày: ngày mai
Giờ: 09:00
```

Tiếp tục đặt lại đúng bác sĩ, ngày, giờ đó.

Kết quả đúng:

```txt
Bác sĩ đã có lịch khám vào khung giờ này
```

Logic cần nhớ:

```java
existsByDoctorIdAndAppointmentDateAndAppointmentTimeAndStatusNot(
    doctorId,
    appointmentDate,
    appointmentTime,
    AppointmentStatus.CANCELLED
);
```

Giải thích:

```txt
Nếu lịch cùng bác sĩ, ngày, giờ và chưa bị hủy thì không cho đặt.
Nếu lịch đã CANCELLED thì slot được giải phóng và có thể đặt lại.
```

---

# 10. Test hủy lịch trước 24 giờ

Đăng nhập Patient.

Vào:

```txt
http://localhost:8080/patient/appointments
```

Bấm:

```txt
Hủy lịch
```

## 10.1. Trường hợp hủy được

Nếu lịch cách hiện tại hơn 24 giờ:

```txt
Hủy thành công
Status chuyển WAITING -> CANCELLED
```

## 10.2. Trường hợp không hủy được

Nếu lịch gần hơn 24 giờ:

```txt
Chỉ được hủy lịch trước thời điểm khám ít nhất 24 giờ
```

Logic cần nhớ:

```java
LocalDateTime appointmentDateTime = LocalDateTime.of(date, time);

if (appointmentDateTime.minusHours(24).isBefore(LocalDateTime.now())) {
    throw new RuntimeException("Chỉ được hủy lịch trước thời điểm khám ít nhất 24 giờ");
}
```

Giải thích:

```txt
Người dùng chỉ được hủy khi thời điểm hiện tại còn cách lịch khám ít nhất 24 giờ.
Khi hủy thành công, status = CANCELLED.
Slot đó được mở lại cho người khác đặt.
```

---

# 11. Test bác sĩ khám bệnh và kê thuốc

## 11.1. Chuẩn bị

Cần có một lịch khám trạng thái:

```txt
WAITING
```

Nếu chưa có thì đăng nhập Patient và đặt lịch trước.

---

## 11.2. Đăng nhập Doctor

```txt
doctor@gmail.com / 123456
```

Vào:

```txt
http://localhost:8080/doctor/appointments
```

Kết quả đúng:

```txt
Hiện danh sách bệnh nhân chờ khám.
```

---

## 11.3. Nhập kết quả khám

Bấm:

```txt
Khám bệnh
```

Nhập:

```txt
Triệu chứng:
Ho, sốt nhẹ, đau họng

Chẩn đoán:
Viêm họng nhẹ
```

Kê thuốc:

```txt
Thuốc: Paracetamol
Số lượng: 2
Hướng dẫn: Uống sau ăn

Thuốc: Vitamin C
Số lượng: 3
Hướng dẫn: Uống buổi sáng
```

Bấm:

```txt
Lưu kết quả khám
```

Kết quả đúng:

```txt
Quay lại danh sách chờ khám.
Lịch vừa khám biến mất khỏi danh sách.
```

---

## 11.4. Logic Transaction cần nhớ

Khi bác sĩ lưu khám bệnh, hệ thống làm nhiều việc cùng lúc:

```txt
1. Lưu MedicalRecord
2. Tạo Prescription
3. Tạo PrescriptionDetail
4. Cập nhật Appointment từ WAITING sang COMPLETED
```

Code dùng:

```java
@Transactional
```

Giải thích khi bảo vệ:

```txt
Vì thao tác khám bệnh ảnh hưởng nhiều bảng.
Nếu lưu bệnh án thành công nhưng lưu đơn thuốc lỗi thì dữ liệu sẽ bị rác.
Do đó em dùng @Transactional để nếu một bước lỗi thì rollback toàn bộ.
```

SQL kiểm tra:

```sql
SELECT * FROM appointments;
SELECT * FROM medical_records;
SELECT * FROM prescriptions;
SELECT * FROM prescription_details;
```

Kết quả đúng:

```txt
appointments.status = COMPLETED
medical_records có symptoms và diagnosis
prescriptions.status = WAITING_DISPENSE
prescription_details có thuốc, số lượng, hướng dẫn
```

---

# 12. Test bệnh nhân xem lịch sử bệnh án

Đăng nhập Patient.

Vào:

```txt
http://localhost:8080/patient/history
```

Kết quả đúng:

```txt
Hiện ngày khám
Hiện giờ khám
Hiện tên bác sĩ
Hiện chuyên khoa
Hiện triệu chứng
Hiện chẩn đoán
Hiện danh sách thuốc đã kê
Hiện trạng thái cấp phát thuốc
```

Logic cần nhớ:

```txt
CORE-07 không chỉ hiển thị một danh sách đơn giản.
Nó phải lấy dữ liệu liên kết:
Patient -> Appointment -> Doctor -> MedicalRecord -> Prescription -> PrescriptionDetail -> Medicine.
```

Giải thích annotation liên quan:

```txt
@ManyToOne: nhiều lịch hẹn thuộc về một bác sĩ hoặc một bệnh nhân.
@OneToOne: một lịch khám có một bệnh án.
@OneToMany: một đơn thuốc có nhiều chi tiết thuốc.
```

---

# 13. Test Admin cấp phát thuốc và trừ tồn kho

Đăng nhập Admin.

Vào:

```txt
http://localhost:8080/admin/prescriptions
```

Kết quả đúng:

```txt
Hiện danh sách đơn thuốc đang WAITING_DISPENSE.
```

Bấm:

```txt
Xác nhận phát thuốc
```

---

## 13.1. Trường hợp đủ tồn kho

Kết quả đúng:

```txt
Trừ tồn kho thuốc.
Prescription chuyển sang DISPENSED.
Đơn thuốc biến mất khỏi danh sách chờ cấp phát.
```

SQL kiểm tra:

```sql
SELECT * FROM medicines;
SELECT * FROM prescriptions;
```

---

## 13.2. Trường hợp thiếu tồn kho

Có thể sửa tồn kho thuốc về thấp trong Admin quản lý thuốc.

Ví dụ đơn cần 10 viên nhưng kho chỉ còn 2.

Bấm cấp phát.

Kết quả đúng:

```txt
Báo lỗi thuốc không đủ tồn kho.
Không trừ bất kỳ thuốc nào.
Prescription vẫn WAITING_DISPENSE.
```

Logic cần nhớ:

```txt
Hệ thống kiểm tra tồn kho tất cả thuốc trước.
Nếu tất cả đều đủ thì mới trừ kho.
Nếu một thuốc thiếu thì chặn toàn bộ.
```

Code có `@Transactional` để đảm bảo:

```txt
Nếu trừ thuốc A xong nhưng thuốc B lỗi thì rollback lại thuốc A.
Không bị sai tồn kho.
```

---

# 14. Test đăng xuất

Bấm:

```txt
Đăng xuất
```

Kết quả đúng:

```txt
Session bị hủy.
Chuyển về /login.
Không thể vào lại /admin, /doctor, /patient nếu chưa đăng nhập.
```

Logic cần nhớ:

```java
session.invalidate();
```

---

# 15. Luồng demo đẹp nhất khi nộp bài

Nên demo theo thứ tự này:

```txt
1. Login Admin
2. Test phân quyền: Admin vào /doctor/dashboard bị chặn
3. Admin quản lý thuốc: thêm/sửa/tìm kiếm thuốc
4. Logout Admin

5. Login Patient
6. Patient đặt lịch khám
7. Patient xem lịch của tôi thấy WAITING
8. Logout Patient

9. Login Doctor
10. Doctor xem danh sách chờ khám
11. Doctor nhập triệu chứng, chẩn đoán, kê thuốc
12. Lịch chuyển thành COMPLETED
13. Logout Doctor

14. Login Patient
15. Patient xem lịch sử bệnh án
16. Thấy đơn thuốc đang Chờ cấp phát
17. Logout Patient

18. Login Admin
19. Admin vào cấp phát thuốc
20. Xác nhận phát thuốc
21. Kiểm tra tồn kho giảm
22. Logout Admin
```

---

# 16. Các lỗi thường gặp khi test

## 16.1. Login đúng nhưng không vào được

Kiểm tra password seed có hash chưa.

Nếu trong `AuthService` login so sánh:

```java
hashPassword(dto.getPassword())
```

thì trong `DataSeeder` cũng phải lưu:

```java
.password(hashPassword("123456"))
```

---

## 16.2. Admin vào được trang Doctor

Kiểm tra:

```txt
AuthInterceptor có chạy không?
WebConfig có đăng ký Interceptor chưa?
Package có cùng gốc với HealthcareApplication không?
```

Cấu trúc đúng:

```txt
com.healthcare
├── HealthcareApplication.java
├── config
├── controller
├── interceptor
```

---

## 16.3. Lỗi LazyInitializationException

Có thể do Thymeleaf gọi quan hệ LAZY sau khi session JPA đóng.

Cách xử lý nhanh trong bài học:

```properties
spring.jpa.open-in-view=true
```

Mặc định Spring Boot thường đang bật sẵn.

Cách chuẩn hơn:

```txt
Viết query join fetch hoặc chuyển dữ liệu sang DTO trước khi đưa ra view.
```

---

## 16.4. Lỗi prescription details null

Nếu gặp lỗi khi kê thuốc:

```txt
Cannot invoke "List.add" because details is null
```

Sửa `Prescription.java`:

```java
@Builder.Default
@OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, orphanRemoval = true)
private List<PrescriptionDetail> details = new ArrayList<>();
```

Hoặc khi tạo `Prescription`:

```java
.details(new ArrayList<>())
```

---

## 16.5. Lỗi id thuốc null

Nếu form tạo sẵn 3 dòng thuốc mà bạn bỏ trống dòng 2 hoặc dòng 3, service cần bỏ qua dòng trống:

```java
if (item.getMedicineId() == null) {
    continue;
}
```

---

# 17. Câu trả lời nhanh khi giảng viên hỏi logic

## Vì sao dùng Session?

```txt
Vì project là Web MVC truyền thống.
Sau khi đăng nhập, em lưu currentUser và role vào session.
Mỗi request sau đó Interceptor đọc session để phân quyền.
```

## Vì sao dùng Interceptor?

```txt
Interceptor giúp kiểm tra đăng nhập và phân quyền tập trung trước khi vào Controller.
Như vậy không cần viết lặp lại logic kiểm tra role trong từng Controller.
```

## LAZY là gì?

```txt
LAZY là chỉ tải dữ liệu quan hệ khi thật sự cần dùng.
Ví dụ lấy Appointment thì chưa tải Doctor ngay, khi gọi appointment.getDoctor() mới tải.
Dùng LAZY để tránh query thừa và giảm nặng database.
```

## EAGER là gì?

```txt
EAGER là tải dữ liệu quan hệ ngay lập tức cùng entity chính.
Dễ dùng nhưng nếu lạm dụng sẽ tạo nhiều query nặng.
```

## CascadeType.ALL là gì?

```txt
CascadeType.ALL nghĩa là thao tác với entity cha sẽ tác động luôn đến entity con.
Ví dụ Prescription có nhiều PrescriptionDetail.
Khi lưu Prescription thì lưu luôn các PrescriptionDetail.
```

## Vì sao không dùng CascadeType.ALL bừa bãi?

```txt
Vì có thể gây xóa dây chuyền nguy hiểm.
Ví dụ nếu dùng CascadeType.ALL từ Doctor sang Appointment, khi xóa Doctor có thể xóa luôn lịch khám.
```

## Vì sao dùng @Transactional?

```txt
Vì một nghiệp vụ ảnh hưởng nhiều bảng.
Ví dụ khám bệnh phải lưu bệnh án, lưu đơn thuốc, lưu chi tiết thuốc và đổi trạng thái lịch khám.
Nếu một bước lỗi thì rollback toàn bộ để không sinh dữ liệu rác.
```

## Xóa mềm là gì?

```txt
Xóa mềm là không xóa bản ghi khỏi database, chỉ đổi deleted = true.
Khi hiển thị thì chỉ lấy deleted = false.
Cách này giúp giữ lịch sử dữ liệu.
```

---
