@echo off
echo 🐳 Testando Docker
echo ==================

echo.
echo 1. Verificando se Docker está rodando...
docker --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker não está instalado ou não está no PATH
    pause
    exit /b 1
)
echo ✅ Docker está disponível

echo.
echo 2. Verificando se Docker Compose está disponível...
docker-compose --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Docker Compose não está disponível
    pause
    exit /b 1
)
echo ✅ Docker Compose está disponível

echo.
echo 3. Construindo imagem...
docker-compose build

if %errorlevel% neq 0 (
    echo ❌ Erro na construção da imagem
    pause
    exit /b 1
)
echo ✅ Imagem construída com sucesso

echo.
echo 4. Iniciando serviços...
docker-compose up -d

echo.
echo 5. Aguardando serviços ficarem prontos...
timeout /t 15 /nobreak >nul

echo.
echo 6. Verificando status dos serviços...
docker-compose ps

echo.
echo 7. Testando API...
curl -s http://localhost:8080/api/health
if %errorlevel% neq 0 (
    echo ❌ API não está respondendo
    echo Verificando logs...
    docker-compose logs app
    pause
    exit /b 1
)
echo ✅ API está funcionando

echo.
echo 8. Parando serviços...
docker-compose down

echo.
echo ✅ Teste Docker concluído com sucesso!
echo.
echo Para usar o sistema: start.bat
pause
