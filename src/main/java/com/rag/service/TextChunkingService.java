package com.rag.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TextChunkingService {
    
    // Configurações de chunking
    private static final int CHUNK_SIZE = 500; // tokens aproximados
    private static final int OVERLAP_SIZE = 50; // tokens de sobreposição
    private static final int CHARS_PER_TOKEN = 4; // estimativa: 4 caracteres por token
    
    // Calcula tamanho em caracteres baseado na estimativa de tokens
    private static final int CHUNK_SIZE_CHARS = CHUNK_SIZE * CHARS_PER_TOKEN;
    private static final int OVERLAP_SIZE_CHARS = OVERLAP_SIZE * CHARS_PER_TOKEN;
    
    
    /**
     * Divide um texto em chunks com sobreposição
     * @param text texto a ser dividido
     * @return lista de chunks
     */
    public List<String> chunkText(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        List<String> chunks = new ArrayList<>();
        String cleanText = text.trim();
        
        // Se o texto é menor que o tamanho do chunk, retorna o texto inteiro
        if (cleanText.length() <= CHUNK_SIZE_CHARS) {
            chunks.add(cleanText);
            return chunks;
        }
        
        int start = 0;
        
        while (start < cleanText.length()) {
            int end = Math.min(start + CHUNK_SIZE_CHARS, cleanText.length());
            
            // Tenta quebrar em uma sentença próxima ao final
            String chunk = cleanText.substring(start, end);
            
            // Se não é o último chunk, tenta ajustar o final para uma sentença
            if (end < cleanText.length()) {
                int lastSentenceEnd = findLastSentenceEnd(chunk);
                if (lastSentenceEnd > 0) {
                    chunk = chunk.substring(0, lastSentenceEnd);
                    end = start + lastSentenceEnd;
                }
            }
            
            chunks.add(chunk.trim());
            
            // Move o início para o próximo chunk com sobreposição
            start = end - OVERLAP_SIZE_CHARS;
            if (start >= cleanText.length()) {
                break;
            }
        }
        
        return chunks;
    }
    
    /**
     * Encontra o último ponto de quebra de sentença no chunk
     * @param chunk texto do chunk
     * @return posição do último ponto de quebra, ou -1 se não encontrar
     */
    private int findLastSentenceEnd(String chunk) {
        // Procura por pontos, exclamações ou interrogações seguidos de espaço
        int lastIndex = -1;
        for (int i = chunk.length() - 1; i >= 0; i--) {
            char c = chunk.charAt(i);
            if (c == '.' || c == '!' || c == '?') {
                // Verifica se há espaço após o ponto
                if (i + 1 < chunk.length() && Character.isWhitespace(chunk.charAt(i + 1))) {
                    lastIndex = i + 1;
                    break;
                }
            }
        }
        return lastIndex;
    }
    
    /**
     * Divide texto em chunks com metadados adicionais
     * @param text texto a ser dividido
     * @param source fonte do documento
     * @return lista de chunks com informações de posição
     */
    public List<ChunkInfo> chunkTextWithMetadata(String text, String source) {
        List<String> chunks = chunkText(text);
        List<ChunkInfo> chunkInfos = new ArrayList<>();
        
        for (int i = 0; i < chunks.size(); i++) {
            ChunkInfo chunkInfo = new ChunkInfo();
            chunkInfo.setChunkText(chunks.get(i));
            chunkInfo.setChunkIndex(i);
            chunkInfo.setSource(source);
            chunkInfo.setTotalChunks(chunks.size());
            chunkInfos.add(chunkInfo);
        }
        
        return chunkInfos;
    }
    
    /**
     * Classe para armazenar informações do chunk
     */
    public static class ChunkInfo {
        private String chunkText;
        private int chunkIndex;
        private String source;
        private int totalChunks;
        
        // Construtores
        public ChunkInfo() {}
        
        public ChunkInfo(String chunkText, int chunkIndex, String source, int totalChunks) {
            this.chunkText = chunkText;
            this.chunkIndex = chunkIndex;
            this.source = source;
            this.totalChunks = totalChunks;
        }
        
        // Getters e Setters
        public String getChunkText() {
            return chunkText;
        }
        
        public void setChunkText(String chunkText) {
            this.chunkText = chunkText;
        }
        
        public int getChunkIndex() {
            return chunkIndex;
        }
        
        public void setChunkIndex(int chunkIndex) {
            this.chunkIndex = chunkIndex;
        }
        
        public String getSource() {
            return source;
        }
        
        public void setSource(String source) {
            this.source = source;
        }
        
        public int getTotalChunks() {
            return totalChunks;
        }
        
        public void setTotalChunks(int totalChunks) {
            this.totalChunks = totalChunks;
        }
    }
}
