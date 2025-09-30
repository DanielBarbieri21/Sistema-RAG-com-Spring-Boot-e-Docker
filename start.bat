@echo off
echo 🚀 Iniciando Sistema RAG
echo ========================

echo.
echo 📦 Construindo e iniciando containers...
docker-compose up --build -d

echo.
echo ⏳ Aguardando serviços ficarem prontos...
timeout /t 10 /nobreak >nul

echo.
echo 🔍 Verificando status dos serviços...
docker-compose ps

echo.
echo ✅ Sistema iniciado com sucesso!
echo.
echo 📊 Acesse:
echo   - API: http://localhost:8080
echo   - Health: http://localhost:8080/api/health
echo   - Logs: docker-compose logs -f app
echo.
echo Para parar: stop.bat
pause
