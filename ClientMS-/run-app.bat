@echo off
echo Configurando Java 17 para la aplicacion...

REM Configurar JAVA_HOME y PATH para Java 17
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%

REM Configurar Maven para usar Java 17
set MAVEN_OPTS=-Djava.home="%JAVA_HOME%"

REM Configurar password de MongoDB
set DB_PASSWORD=1KsQK8dZgcUtiHr7

echo Java version:
java -version

echo.
echo Compilando aplicacion con Java 17...
call mvnw.cmd clean package -DskipTests

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Compilacion exitosa! Ejecutando aplicacion...
    echo.
    echo 🚀 La aplicacion estara disponible en:
    echo    - API: http://localhost:8080/clientes
    echo    - Swagger: http://localhost:8080/swagger-ui.html
    echo.
    java -jar target\client-ms-0.0.1-SNAPSHOT.jar
) else (
    echo.
    echo ❌ Error en la compilacion. Revisa los logs arriba.
    pause
)
