@echo off
echo 🧹 Limpando Sistema RAG
echo =======================

echo.
echo 🛑 Parando containers...
docker-compose down -v

echo.
echo 🗑️ Removendo imagens antigas...
docker image prune -f

echo.
echo 🧹 Limpando build Maven...
mvn clean

echo.
echo 🗂️ Removendo diretórios temporários...
if exist target rmdir /s /q target
if exist .mvn\wrapper\.mvn rmdir /s /q .mvn\wrapper\.mvn

echo.
echo ✅ Limpeza concluída com sucesso!
echo.
echo Para reconstruir: build.bat
pause
