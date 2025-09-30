package com.rag.service;

import com.rag.entity.Document;
import com.rag.repository.DocumentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class SimpleRagService {
    
    private final DocumentRepository documentRepository;
    private final SimpleEmbeddingService embeddingService;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;
    
    public SimpleRagService(DocumentRepository documentRepository, 
                           SimpleEmbeddingService embeddingService,
                           PromptBuilderService promptBuilderService) {
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Processa uma query usando RAG simplificado
     * @param query pergunta do usuário
     * @param topK número de documentos similares a recuperar
     * @return resposta do RAG com fontes
     */
    public RagResponse query(String query, int topK) {
        try {
            // 1. Gerar embedding da query
            String queryEmbedding = embeddingService.generateEmbeddingString(query);
            
            // 2. Buscar documentos similares
            List<Object[]> similarDocs = documentRepository.findSimilarDocuments(queryEmbedding, topK);
            
            if (similarDocs.isEmpty()) {
                return new RagResponse(
                    "Não encontrei documentos relevantes para sua pergunta.",
                    List.of(),
                    Map.of("status", "no_documents_found")
                );
            }
            
            // 3. Converter resultados para entidades Document
            List<Document> documents = convertToDocuments(similarDocs);
            
            // 4. Construir resposta simples
            String answer = buildSimpleAnswer(query, documents);
            
            // 5. Formatar fontes
            String sources = promptBuilderService.formatDocumentSources(documents);
            
            return new RagResponse(answer, documents, Map.of(
                "sources", sources,
                "total_documents", documents.size(),
                "query", query
            ));
            
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar query RAG: " + e.getMessage(), e);
        }
    }
    
    /**
     * Converte resultados da query nativa para entidades Document
     */
    private List<Document> convertToDocuments(List<Object[]> results) {
        return results.stream()
            .map(row -> {
                Document doc = new Document();
                doc.setId(((Number) row[0]).longValue());
                doc.setSource((String) row[1]);
                doc.setChunkIndex(((Number) row[2]).intValue());
                doc.setChunkText((String) row[3]);
                // Converter metadata de JSON string para Map
                String metadataJson = (String) row[5];
                Map<String, Object> metadata = new HashMap<>();
                if (metadataJson != null && !metadataJson.isEmpty()) {
                    try {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> parsedMetadata = objectMapper.readValue(metadataJson, Map.class);
                        metadata = parsedMetadata;
                    } catch (Exception e) {
                        metadata = Map.of("error", "Erro ao processar metadata");
                    }
                }
                doc.setMetadata(metadata);
                doc.setCreatedAt(((java.sql.Timestamp) row[6]).toLocalDateTime());
                return doc;
            })
            .toList();
    }
    
    /**
     * Constrói uma resposta simples baseada nos documentos encontrados
     */
    private String buildSimpleAnswer(String query, List<Document> documents) {
        StringBuilder answer = new StringBuilder();
        
        answer.append("Baseado nos documentos encontrados, aqui está a resposta:\n\n");
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            answer.append(String.format("**Fonte %d** (ID: %d):\n", i + 1, doc.getId()));
            answer.append(doc.getChunkText()).append("\n\n");
        }
        
        answer.append("**Resumo:** Encontrei ").append(documents.size())
              .append(" documento(s) relevante(s) para sua pergunta: \"").append(query).append("\"");
        
        return answer.toString();
    }
    
    /**
     * Classe para resposta do RAG
     */
    public static class RagResponse {
        private final String answer;
        private final List<Document> sources;
        private final Map<String, Object> metadata;
        
        public RagResponse(String answer, List<Document> sources, Map<String, Object> metadata) {
            this.answer = answer;
            this.sources = sources;
            this.metadata = metadata;
        }
        
        public String getAnswer() {
            return answer;
        }
        
        public List<Document> getSources() {
            return sources;
        }
        
        public Map<String, Object> getMetadata() {
            return metadata;
        }
    }
}
