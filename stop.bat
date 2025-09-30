@echo off
echo 🛑 Parando Sistema RAG
echo =====================

echo.
echo 📦 Parando containers...
docker-compose down

echo.
echo 🧹 Removendo volumes (dados serão perdidos)...
set /p choice="Deseja remover os dados do banco? (s/N): "
if /i "%choice%"=="s" (
    echo Removendo volumes...
    docker-compose down -v
    echo ✅ Dados removidos
) else (
    echo ✅ Dados preservados
)

echo.
echo ✅ Sistema parado com sucesso!
pause
