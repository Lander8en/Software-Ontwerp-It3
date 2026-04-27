Write-Host "Running tests and generating JaCoCo report..."
mvn clean test

Write-Host "Generating coverage report..."
reportgenerator -reports:target\site\jacoco\jacoco.xml -targetdir:coverage-report -reporttypes:Html -sourcedirs:src\main\java

Write-Host "Opening coverage report in default browser..."
Start-Process "$PWD\coverage-report\index.html"
