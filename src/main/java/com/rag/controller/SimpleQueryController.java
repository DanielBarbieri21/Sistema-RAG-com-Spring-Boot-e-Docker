package com.rag.controller;

import com.rag.service.SimpleRagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/query")
public class SimpleQueryController {
    
    private final SimpleRagService ragService;
    
    public SimpleQueryController(SimpleRagService ragService) {
        this.ragService = ragService;
    }
    
    /**
     * Endpoint principal para consultas RAG
     * POST /api/query
     * Body: { "query": "pergunta do usuário", "top_k": 8 }
     */
    @PostMapping
    public ResponseEntity<SimpleRagService.RagResponse> query(@RequestBody QueryRequest request) {
        try {
            if (request.getQuery() == null || request.getQuery().trim().isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            int topK = request.getTopK() != null ? request.getTopK() : 8;
            
            SimpleRagService.RagResponse response = ragService.query(request.getQuery(), topK);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            // Em caso de erro, retorna uma resposta de erro
            SimpleRagService.RagResponse errorResponse = new SimpleRagService.RagResponse(
                "Erro ao processar sua pergunta: " + e.getMessage(),
                java.util.List.of(),
                Map.of("error", e.getMessage())
            );
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * Endpoint para consulta simples (GET)
     * GET /api/query?q=pergunta&top_k=8
     */
    @GetMapping
    public ResponseEntity<SimpleRagService.RagResponse> querySimple(
            @RequestParam String q,
            @RequestParam(defaultValue = "8") int top_k) {
        
        QueryRequest request = new QueryRequest(q, top_k);
        return query(request);
    }
    
    /**
     * Endpoint de health check específico para RAG
     * GET /api/query/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        try {
            // Testa uma query simples para verificar se o sistema está funcionando
            ragService.query("teste", 1);
            
            Map<String, Object> health = Map.of(
                "status", "healthy",
                "rag_system", "operational",
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            Map<String, Object> health = Map.of(
                "status", "unhealthy",
                "rag_system", "error",
                "error", e.getMessage(),
                "timestamp", System.currentTimeMillis()
            );
            
            return ResponseEntity.status(503).body(health);
        }
    }
    
    /**
     * Classe para request de query
     */
    public static class QueryRequest {
        private String query;
        private Integer topK;
        
        public QueryRequest() {}
        
        public QueryRequest(String query, Integer topK) {
            this.query = query;
            this.topK = topK;
        }
        
        public String getQuery() {
            return query;
        }
        
        public void setQuery(String query) {
            this.query = query;
        }
        
        public Integer getTopK() {
            return topK;
        }
        
        public void setTopK(Integer topK) {
            this.topK = topK;
        }
    }
}

