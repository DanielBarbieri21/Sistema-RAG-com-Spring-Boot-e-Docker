package com.rag.controller;

import com.rag.service.SimpleEmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/config")
public class ConfigController {
    
    private final SimpleEmbeddingService embeddingService;
    
    public ConfigController(SimpleEmbeddingService embeddingService) {
        this.embeddingService = embeddingService;
    }
    
    /**
     * Endpoint para verificar configuração do sistema
     * GET /api/config
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = Map.of(
            "ai_provider", embeddingService.getProviderInfo(),
            "embedding_dimension", embeddingService.getEmbeddingDimension(),
            "supported_providers", new String[]{"openai", "gemini"},
            "current_provider", embeddingService.getProviderInfo().split(": ")[1],
            "timestamp", System.currentTimeMillis()
        );
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * Endpoint para verificar status dos provedores de IA
     * GET /api/config/providers
     */
    @GetMapping("/providers")
    public ResponseEntity<Map<String, Object>> getProvidersStatus() {
        Map<String, Object> providers = Map.of(
            "openai", Map.of(
                "available", true,
                "models", new String[]{"gpt-3.5-turbo", "gpt-4", "text-embedding-3-small"},
                "embedding_dimension", 1536
            ),
            "gemini", Map.of(
                "available", true,
                "models", new String[]{"gemini-pro", "text-embedding-004"},
                "embedding_dimension", 768
            ),
            "current", embeddingService.getProviderInfo().split(": ")[1]
        );
        
        return ResponseEntity.ok(providers);
    }
}

