@echo off
echo 🔨 Construindo Sistema RAG
echo ===========================

echo.
echo 📦 Construindo aplicação...
mvn clean package -DskipTests

if %errorlevel% neq 0 (
    echo ❌ Erro na compilação
    pause
    exit /b 1
)

echo.
echo 🐳 Construindo imagem Docker...
docker-compose build

if %errorlevel% neq 0 (
    echo ❌ Erro na construção da imagem
    pause
    exit /b 1
)

echo.
echo ✅ Construção concluída com sucesso!
echo.
echo Para iniciar: start.bat
pause
