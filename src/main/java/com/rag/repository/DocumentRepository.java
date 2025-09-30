package com.rag.repository;

import com.rag.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    /**
     * Busca os documentos mais similares usando pgvector
     * @param queryEmbedding embedding da query em formato string para pgvector
     * @param limit número máximo de resultados
     * @return lista de documentos ordenados por similaridade
     */
    @Query(value = """
        SELECT d.*, (d.embedding <-> CAST(:queryEmbedding AS vector)) AS distance
        FROM documents d
        ORDER BY distance
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findSimilarDocuments(@Param("queryEmbedding") String queryEmbedding, @Param("limit") int limit);
    
    /**
     * Busca documentos por source
     */
    List<Document> findBySource(String source);
    
    /**
     * Conta documentos por source
     */
    long countBySource(String source);
    
    /**
     * Insere documento com embedding usando SQL nativo
     */
    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO documents (source, chunk_text, chunk_index, embedding, metadata, created_at)
        VALUES (:source, :chunkText, :chunkIndex, CAST(:embedding AS vector), CAST(:metadata AS jsonb), :createdAt)
        """, nativeQuery = true)
    void insertDocumentWithEmbedding(
        @Param("source") String source,
        @Param("chunkText") String chunkText,
        @Param("chunkIndex") Integer chunkIndex,
        @Param("embedding") String embedding,
        @Param("metadata") String metadata,
        @Param("createdAt") java.time.LocalDateTime createdAt
    );
}
