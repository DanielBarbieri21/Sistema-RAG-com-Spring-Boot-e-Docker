package com.rag.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class SimpleEmbeddingService {
    
    @Value("${app.ai.provider:gemini}")
    private String aiProvider;
    
    public SimpleEmbeddingService() {
    }
    
    /**
     * Gera embedding para um texto usando API externa
     * @param text texto para gerar embedding
     * @return array de floats representando o embedding
     */
    public float[] generateEmbedding(String text) {
        try {
            if ("gemini".equalsIgnoreCase(aiProvider)) {
                return generateGeminiEmbedding(text);
            } else {
                return generateOpenAIEmbedding(text);
            }
        } catch (Exception e) {
            // Fallback: gerar embedding simulado para testes
            return generateMockEmbedding(text);
        }
    }
    
    /**
     * Gera embedding usando Gemini API
     */
    private float[] generateGeminiEmbedding(String text) {
        // Implementação simplificada - em produção usar API real
        return generateMockEmbedding(text);
    }
    
    /**
     * Gera embedding usando OpenAI API
     */
    private float[] generateOpenAIEmbedding(String text) {
        // Implementação simplificada - em produção usar API real
        return generateMockEmbedding(text);
    }
    
    /**
     * Gera embedding simulado para testes
     */
    private float[] generateMockEmbedding(String text) {
        float[] embedding = new float[768]; // Dimensão do Gemini
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] = (float) (Math.random() * 2 - 1); // Valores entre -1 e 1
        }
        return embedding;
    }
    
    /**
     * Converte array de floats para string no formato aceito pelo pgvector
     * @param embedding array de floats
     * @return string no formato [0.1,0.2,0.3,...]
     */
    public String embeddingToString(float[] embedding) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < embedding.length; i++) {
            sb.append(embedding[i]);
            if (i < embedding.length - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    /**
     * Gera embedding e retorna como string formatada para pgvector
     * @param text texto para gerar embedding
     * @return string formatada para inserção no pgvector
     */
    public String generateEmbeddingString(String text) {
        float[] embedding = generateEmbedding(text);
        return embeddingToString(embedding);
    }
    
    /**
     * Retorna informações sobre o provedor de IA atual
     * @return string com informações do provedor
     */
    public String getProviderInfo() {
        return String.format("Provedor de IA: %s (modo simulado)", aiProvider.toUpperCase());
    }
    
    /**
     * Retorna a dimensão do embedding baseada no provedor
     * @return dimensão do embedding
     */
    public int getEmbeddingDimension() {
        return "gemini".equalsIgnoreCase(aiProvider) ? 768 : 1536;
    }
}

