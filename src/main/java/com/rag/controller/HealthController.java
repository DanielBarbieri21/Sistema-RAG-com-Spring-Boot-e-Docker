package com.rag.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    /**
     * Endpoint de health check geral
     * GET /api/health
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "RAG System",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0"
        );
        
        return ResponseEntity.ok(health);
    }
    
    /**
     * Endpoint de health check detalhado
     * GET /api/health/detailed
     */
    @GetMapping("/detailed")
    public ResponseEntity<Map<String, Object>> detailedHealth() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "RAG System with Spring Boot + pgvector",
            "timestamp", LocalDateTime.now(),
            "version", "1.0.0",
            "components", Map.of(
                "database", "PostgreSQL with pgvector",
                "ai_model", "OpenAI GPT-3.5-turbo",
                "embedding_model", "text-embedding-3-small",
                "vector_dimension", 1536
            ),
            "endpoints", Map.of(
                "ingest", "/api/ingest",
                "query", "/api/query",
                "health", "/api/health"
            )
        );
        
        return ResponseEntity.ok(health);
    }
}
