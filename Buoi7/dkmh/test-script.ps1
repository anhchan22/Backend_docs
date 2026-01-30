# TEST SCRIPT - Copy paste vào PowerShell

# 1. Đăng nhập admin
$loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/auth/login" -Method Post -ContentType "application/json" -Body '{"maSV":"admin","matKhau":"admin"}'
$token = $loginResponse.result.token
Write-Host "Token: $token" -ForegroundColor Green

# 2. Test debug auth
Write-Host "`nTesting debug-auth..." -ForegroundColor Yellow
try {
    $debugResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/user/debug-auth" -Method Get -Headers @{Authorization="Bearer $token"}
    Write-Host "Debug Response: $($debugResponse.result)" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

# 3. Test get all users
Write-Host "`nTesting get all users..." -ForegroundColor Yellow
try {
    $usersResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/user" -Method Get -Headers @{Authorization="Bearer $token"}
    Write-Host "Users Count: $($usersResponse.result.Count)" -ForegroundColor Green
    Write-Host "Success!" -ForegroundColor Green
} catch {
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Status Code: $($_.Exception.Response.StatusCode)" -ForegroundColor Red
}
