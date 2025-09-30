package com.rag.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConfig implements CommandLineRunner {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DatabaseConfig(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔧 Configurando banco de dados Supabase...");
        
        // Verificar se a extensão pgvector existe
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM pg_extension WHERE extname = 'vector'", Integer.class);
            System.out.println("✅ Extensão pgvector já está instalada");
        } catch (Exception e) {
            System.err.println("❌ Extensão pgvector não encontrada. Execute o script supabase_setup.sql no Supabase");
            System.err.println("   Arquivo: scripts/supabase_setup.sql");
        }
        
        // Verificar se a tabela documents existe
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM information_schema.tables WHERE table_name = 'documents'", Integer.class);
            System.out.println("✅ Tabela documents encontrada");
        } catch (Exception e) {
            System.err.println("❌ Tabela documents não encontrada. Execute o script supabase_setup.sql no Supabase");
            System.err.println("   Arquivo: scripts/supabase_setup.sql");
        }
        
        // Verificar se os índices existem
        try {
            Integer indexCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM pg_indexes WHERE tablename = 'documents'", 
                Integer.class
            );
            System.out.println("✅ Encontrados " + (indexCount != null ? indexCount : 0) + " índices na tabela documents");
        } catch (Exception e) {
            System.err.println("⚠️ Não foi possível verificar índices: " + e.getMessage());
        }
        
        // Testar conexão com uma consulta simples
        try {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM documents", Integer.class);
            System.out.println("✅ Conexão com Supabase funcionando. Total de documentos: " + (count != null ? count.intValue() : 0));
        } catch (Exception e) {
            System.err.println("❌ Erro ao conectar com Supabase: " + e.getMessage());
            System.err.println("   Verifique as credenciais no arquivo .env");
        }
        
        System.out.println("🎉 Configuração do banco de dados concluída!");
    }
}
