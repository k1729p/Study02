@echo on
@set SITE=http://localhost:8380
@set CURL=curl -g -i -H "Accept: application/json" -H "Content-Type: application/json"
@set HR_YELLOW=@powershell -Command Write-Host "----------------------------------------------------------------------" -foreground "Yellow"
@set HR_RED=@powershell    -Command Write-Host "----------------------------------------------------------------------" -foreground "Red"

%HR_YELLOW%
@powershell -Command Write-Host "Load sample dataset" -foreground "Green"
%CURL% "%SITE%/loadSampleDataset"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find aggregate by department name" -foreground "Green"
%CURL% "%SITE%/aggregate/find/findByDepartmentName?departmentName=D-Name-1"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find all departments" -foreground "Green"
%CURL% "%SITE%/departments"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find department by name" -foreground "Green"
%CURL% "%SITE%/departments/find/findByName?name=D-Name-1"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find all employees" -foreground "Green"
%CURL% "%SITE%/employees"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find employee by first name and last name" -foreground "Green"
%CURL% "%SITE%/employees/find/findByFirstNameAndLastName?firstName=EF-Name-101&lastName=EL-Name-101"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find unknown department. Receiving empty response [200 OK]" -foreground "Magenta"
%CURL% "%SITE%/departments/find/findByName?name=unknown"
@echo.

%HR_YELLOW%
@powershell -Command Write-Host "Find unknown employee. Receiving error response [404 Not Found]" -foreground "Magenta"
%CURL% "%SITE%/employees/find/findByFirstNameAndLastName?firstName=unknown&lastName=unknown"
@echo.

%HR_RED%
@powershell -Command Write-Host "FINISH" -foreground "Red"
pause