package com.rag.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "documents")
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "source", nullable = false)
    private String source;
    
    @Column(name = "chunk_text", columnDefinition = "TEXT", nullable = false)
    private String chunkText;
    
    @Column(name = "chunk_index", nullable = false)
    private Integer chunkIndex;
    
    @Column(name = "embedding", columnDefinition = "vector(768)")
    private String embedding; // Formato: [0.1,0.2,0.3,...] para pgvector
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Construtores
    public Document() {
        this.createdAt = LocalDateTime.now();
    }
    
    public Document(String source, String chunkText, Integer chunkIndex, String embedding, Map<String, Object> metadata) {
        this();
        this.source = source;
        this.chunkText = chunkText;
        this.chunkIndex = chunkIndex;
        this.embedding = embedding;
        this.metadata = metadata;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public String getChunkText() {
        return chunkText;
    }
    
    public void setChunkText(String chunkText) {
        this.chunkText = chunkText;
    }
    
    public Integer getChunkIndex() {
        return chunkIndex;
    }
    
    public void setChunkIndex(Integer chunkIndex) {
        this.chunkIndex = chunkIndex;
    }
    
    public String getEmbedding() {
        return embedding;
    }
    
    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }
    
    public Map<String, Object> getMetadata() {
        return metadata;
    }
    
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    @Override
    public String toString() {
        return "Document{" +
                "id=" + id +
                ", source='" + source + '\'' +
                ", chunkIndex=" + chunkIndex +
                ", createdAt=" + createdAt +
                '}';
    }
}
