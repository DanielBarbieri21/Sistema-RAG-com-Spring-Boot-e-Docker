package com.rag.service;

import com.rag.entity.Document;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class PromptBuilderService {
    
    private static final String SYSTEM_PROMPT = """
        Você é um assistente técnico que responde com base apenas nas evidências fornecidas. 
        Suas respostas devem ser precisas, objetivas e baseadas exclusivamente nos trechos de documentos fornecidos.
        
        INSTRUÇÕES IMPORTANTES:
        - Use APENAS as informações dos trechos numerados abaixo para responder
        - Se a informação não existir nos trechos fornecidos, diga claramente: "Não encontrei essa informação nos documentos fornecidos"
        - Sempre cite as fontes usando o formato [ID: X] onde X é o número do trecho
        - Se não souber algo, sugira uma palavra-chave ou documento específico para buscar
        - Mantenha um tom profissional e técnico
        """;
    
    /**
     * Constrói o prompt completo para o LLM
     * @param query pergunta do usuário
     * @param retrievedDocuments documentos recuperados
     * @return prompt formatado
     */
    public String buildPrompt(String query, List<Document> retrievedDocuments) {
        StringBuilder prompt = new StringBuilder();
        
        // System prompt
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        
        // Contextos numerados
        prompt.append("CONTEXTOS DISPONÍVEIS:\n");
        for (int i = 0; i < retrievedDocuments.size(); i++) {
            Document doc = retrievedDocuments.get(i);
            prompt.append(String.format("%d) [ID: %d] Fonte: %s\n", 
                i + 1, doc.getId(), doc.getSource()));
            prompt.append(String.format("Trecho: %s\n\n", doc.getChunkText()));
        }
        
        // Instrução final
        prompt.append("INSTRUÇÃO: Usando apenas os trechos acima, responda à pergunta do usuário de forma clara e objetiva. ");
        prompt.append("Se a informação não existir nos trechos, diga: \"Não encontrei essa informação nos documentos fornecidos.\" ");
        prompt.append("Forneça também 1 sugestão de documento ou palavra-chave para buscar se necessário.\n\n");
        
        // Pergunta do usuário
        prompt.append("PERGUNTA: ").append(query);
        
        return prompt.toString();
    }
    
    /**
     * Constrói um prompt mais simples para casos específicos
     * @param query pergunta do usuário
     * @param contexts lista de contextos formatados
     * @return prompt formatado
     */
    public String buildSimplePrompt(String query, List<String> contexts) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("Baseado nos seguintes contextos, responda à pergunta:\n\n");
        
        for (int i = 0; i < contexts.size(); i++) {
            prompt.append(String.format("Contexto %d: %s\n\n", i + 1, contexts.get(i)));
        }
        
        prompt.append("Pergunta: ").append(query);
        
        return prompt.toString();
    }
    
    /**
     * Formata documentos para exibição em resposta
     * @param documents lista de documentos
     * @return string formatada com informações dos documentos
     */
    public String formatDocumentSources(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return "Nenhuma fonte encontrada.";
        }
        
        StringBuilder sources = new StringBuilder();
        sources.append("Fontes consultadas:\n");
        
        for (int i = 0; i < documents.size(); i++) {
            Document doc = documents.get(i);
            sources.append(String.format("- [%d] %s (chunk %d)\n", 
                doc.getId(), doc.getSource(), doc.getChunkIndex()));
        }
        
        return sources.toString();
    }
    
    /**
     * Cria metadados para um documento
     * @param source fonte do documento
     * @param chunkIndex índice do chunk
     * @param totalChunks total de chunks
     * @return mapa de metadados
     */
    public Map<String, Object> createMetadata(String source, int chunkIndex, int totalChunks) {
        return Map.of(
            "source", source,
            "chunk_index", chunkIndex,
            "total_chunks", totalChunks,
            "created_by", "rag-system",
            "version", "1.0"
        );
    }
}
