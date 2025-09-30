# Sistema RAG com Spring Boot e Docker

Sistema de Retrieval-Augmented Generation (RAG) usando Spring Boot, PostgreSQL com pgvector e APIs de IA, totalmente containerizado com Docker.

## 🚀 Funcionalidades

- **Ingestão de documentos**: Processa e armazena documentos em chunks
- **Busca semântica**: Encontra documentos similares usando embeddings
- **Geração de respostas**: Usa IA para gerar respostas baseadas no contexto
- **APIs REST**: Endpoints para ingestão, busca e consultas
- **Suporte a múltiplos provedores de IA**: OpenAI e Google Gemini

## 🏗️ Arquitetura

- **Spring Boot 3.2**: Framework principal
- **PostgreSQL + pgvector**: Banco de dados local com suporte a embeddings
- **Spring AI**: Integração com APIs de IA
- **Docker Compose**: Orquestração de containers
- **Maven**: Gerenciamento de dependências

## 📋 Pré-requisitos

- Docker e Docker Compose
- Chaves de API (OpenAI ou Google Gemini)
- Git (para clonar o repositório)

## 🚀 Execução Rápida

### Com Docker (Recomendado)

```bash
# 1. Clone o repositório
git clone https://github.com/DanielBarbieri21/Sistema-RAG-com-Spring-Boot-e-Docker.git
cd rag-system

# 2. Configure as variáveis de ambiente
cp env.example .env
# Edite o arquivo .env com suas chaves de API

# 3. Execute o sistema
./start.bat
```

### Scripts Disponíveis

- `start.bat` - Inicia o sistema completo
- `stop.bat` - Para o sistema
- `build.bat` - Constrói a aplicação
- `test.bat` - Executa testes automatizados
- `test-docker.bat` - Testa configuração Docker
- `clean.bat` - Limpeza completa do projeto

## 🔧 Configuração

### Variáveis de Ambiente

Crie um arquivo `.env` baseado no `env.example`:

```env
# Chaves das APIs de IA
OPENAI_API_KEY=your-openai-api-key-here
GEMINI_API_KEY=your-gemini-api-key-here

# Provedor de IA (gemini ou openai)
AI_PROVIDER=gemini
```

### Configuração do Banco

O banco PostgreSQL é configurado automaticamente pelo Docker com:
- Extensão pgvector para embeddings
- Tabelas e índices otimizados
- Scripts de inicialização automáticos

## 📚 APIs Disponíveis

### Health Check
```bash
GET /api/health
```

### Ingestão de Documentos
```bash
POST /api/ingest
Content-Type: application/json

{
  "source": "documento.txt",
  "text": "Conteúdo do documento..."
}
```

### Consulta RAG
```bash
POST /api/query
Content-Type: application/json

{
  "query": "Sua pergunta aqui",
  "limit": 5
}
```

### Configuração
```bash
GET /api/config
```

## 🧪 Testes

Execute os testes automatizados:

```bash
# Testes com curl
./scripts/test_curl.bat

# Testes com Maven
mvn test
```

## 📊 Monitoramento

- **Health Check**: http://localhost:8080/api/health
- **Logs**: `docker-compose logs -f app`
- **Métricas**: http://localhost:8080/actuator

## 🔍 Solução de Problemas

### Problemas Comuns

1. **Erro de conexão com banco**
   - Verifique se o Docker está rodando
   - Execute `docker-compose logs postgres`
   - Reinicie com `stop.bat` e `start.bat`

2. **Erro de API de IA**
   - Verifique as chaves de API no arquivo `.env`
   - Confirme o provedor configurado
   - Teste com `test.bat`

3. **Erro de compilação**
   - Execute `build.bat` para recompilar
   - Verifique logs com `docker-compose logs app`

### Logs

```bash
# Ver logs da aplicação
docker-compose logs -f app
```

## 📁 Estrutura do Projeto

```
rag-system/
├── src/main/java/com/rag/     # Código fonte Java
├── scripts/                  # Scripts de inicialização
├── docker-compose.yml         # Orquestração Docker
├── Dockerfile                # Imagem da aplicação
├── start.bat                 # Iniciar sistema
├── stop.bat                  # Parar sistema
├── build.bat                 # Construir aplicação
├── test.bat                  # Testes automatizados
├── env.example              # Configurações de exemplo
└── README.md                 # Documentação
```

## 🤝 Contribuição

1. Fork o projeto
2. Crie uma branch para sua feature
3. Commit suas mudanças
4. Push para a branch
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo LICENSE para detalhes.

## 🆘 Suporte

Para suporte, abra uma issue no repositório ou entre em contato.

---

## 👨‍💻 Desenvolvido por

IronDev Software. Contato: dibarbieri21@gmail.com | (32) 99118-6728


