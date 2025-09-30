package com.rag.controller;

import com.rag.entity.Document;
import com.rag.repository.DocumentRepository;
import com.rag.service.SimpleEmbeddingService;
import com.rag.service.PromptBuilderService;
import com.rag.service.TextChunkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/ingest")
public class IngestController {
    
    private final DocumentRepository documentRepository;
    private final SimpleEmbeddingService embeddingService;
    private final TextChunkingService chunkingService;
    private final PromptBuilderService promptBuilderService;
    private final ObjectMapper objectMapper;
    
    public IngestController(DocumentRepository documentRepository,
                          SimpleEmbeddingService embeddingService,
                          TextChunkingService chunkingService,
                          PromptBuilderService promptBuilderService) {
        this.documentRepository = documentRepository;
        this.embeddingService = embeddingService;
        this.chunkingService = chunkingService;
        this.promptBuilderService = promptBuilderService;
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Endpoint para ingerir texto
     * POST /api/ingest
     * Body: { "text": "texto para ingerir", "source": "fonte do documento" }
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> ingestText(@RequestBody IngestRequest request) {
        try {
            if (request.getText() == null || request.getText().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Texto não pode ser vazio"));
            }
            
            String source = request.getSource() != null ? request.getSource() : "unknown";
            
            // 1. Dividir texto em chunks
            List<TextChunkingService.ChunkInfo> chunks = chunkingService.chunkTextWithMetadata(
                request.getText(), source);
            
            int processedChunks = 0;
            int errorChunks = 0;
            
            // 2. Processar cada chunk
            for (TextChunkingService.ChunkInfo chunkInfo : chunks) {
                try {
                    // Gerar embedding
                    String embedding = embeddingService.generateEmbeddingString(chunkInfo.getChunkText());
                    
                    // Criar metadados
                    Map<String, Object> metadata = promptBuilderService.createMetadata(
                        chunkInfo.getSource(),
                        chunkInfo.getChunkIndex(),
                        chunkInfo.getTotalChunks()
                    );
                    
                    // Salvar documento
                    // Converter metadata para JSON string
                    String metadataJson = objectMapper.writeValueAsString(metadata);
                    
                    // Usar SQL nativo para inserir com embedding
                    documentRepository.insertDocumentWithEmbedding(
                        chunkInfo.getSource(),
                        chunkInfo.getChunkText(),
                        chunkInfo.getChunkIndex(),
                        embedding,
                        metadataJson,
                        java.time.LocalDateTime.now()
                    );
                    processedChunks++;
                    
                } catch (Exception e) {
                    System.err.println("Erro ao processar chunk " + chunkInfo.getChunkIndex() + ": " + e.getMessage());
                    errorChunks++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("source", source);
            response.put("total_chunks", chunks.size());
            response.put("processed_chunks", processedChunks);
            response.put("error_chunks", errorChunks);
            response.put("message", "Documento ingerido com sucesso");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Erro ao processar ingestão: " + e.getMessage()));
        }
    }
    
    /**
     * Endpoint para listar documentos por fonte
     * GET /api/ingest/source/{source}
     */
    @GetMapping("/source/{source}")
    public ResponseEntity<List<Document>> getDocumentsBySource(@PathVariable String source) {
        List<Document> documents = documentRepository.findBySource(source);
        return ResponseEntity.ok(documents);
    }
    
    /**
     * Endpoint para estatísticas de ingestão
     * GET /api/ingest/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getIngestStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total_documents", documentRepository.count());
        
        // Estatísticas por fonte (exemplo)
        List<String> sources = List.of("document1", "document2", "unknown");
        Map<String, Long> sourceStats = new HashMap<>();
        for (String source : sources) {
            sourceStats.put(source, documentRepository.countBySource(source));
        }
        stats.put("by_source", sourceStats);
        
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Classe para request de ingestão
     */
    public static class IngestRequest {
        private String text;
        private String source;
        
        public IngestRequest() {}
        
        public IngestRequest(String text, String source) {
            this.text = text;
            this.source = source;
        }
        
        public String getText() {
            return text;
        }
        
        public void setText(String text) {
            this.text = text;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
    }
}
