@echo off
echo 🧪 Testando Sistema RAG
echo ========================

echo.
echo 1. Verificando se a API está rodando...
curl -s http://localhost:8080/api/health >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ API não está respondendo. Execute primeiro: start.bat
    pause
    exit /b 1
)
echo ✅ API está funcionando

echo.
echo 2. Ingerindo documento de teste...
curl -X POST http://localhost:8080/api/ingest ^
  -H "Content-Type: application/json" ^
  -d "{\"text\": \"Spring Boot é um framework Java que simplifica o desenvolvimento de aplicações. Ele oferece configuração automática e servidor embarcado.\", \"source\": \"teste.txt\"}"
echo.

echo.
echo 3. Aguardando processamento...
timeout /t 3 /nobreak >nul

echo.
echo 4. Fazendo consulta RAG...
curl -X POST http://localhost:8080/api/query ^
  -H "Content-Type: application/json" ^
  -d "{\"query\": \"O que é Spring Boot?\", \"top_k\": 3}"
echo.

echo.
echo 5. Verificando estatísticas...
curl -s http://localhost:8080/api/ingest/stats
echo.

echo.
echo ✅ Teste concluído com sucesso!
pause
