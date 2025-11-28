package com.cadealt.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária para gerenciar conexão com o banco de dados SQLite
 */
public class DatabaseConnection {

    private static final String DB_URL = "jdbc:sqlite:src/main/resources/db/cadealt.db";
    private static Connection connection;

    /**
     * Obtém a conexão com o banco de dados (Singleton)
     * @return Connection objeto de conexão
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("Conexão com banco de dados estabelecida.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Conexão com banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao fechar conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Inicializa as tabelas do banco de dados
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Tabela de hinos
            String createHinosTable = """
                CREATE TABLE IF NOT EXISTS hinos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    numero TEXT NOT NULL,
                    titulo TEXT NOT NULL,
                    letra TEXT,
                    tipo TEXT DEFAULT 'harpa'
                );
                """;

            // Tabela de louvores
            String createLouvoresTable = """
                CREATE TABLE IF NOT EXISTS louvores (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    titulo TEXT NOT NULL,
                    autor TEXT,
                    letra TEXT
                );
                """;

            // Tabela de versículos
            String createVersiculosTable = """
                CREATE TABLE IF NOT EXISTS versiculos (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    livro TEXT NOT NULL,
                    capitulo INTEGER NOT NULL,
                    versiculo INTEGER NOT NULL,
                    texto TEXT NOT NULL,
                    versao TEXT DEFAULT 'ARC'
                );
                """;

            // Tabela de histórico de cultos
            String createHistoricoTable = """
                CREATE TABLE IF NOT EXISTS historico_culto (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    data TEXT NOT NULL,
                    tipo_culto TEXT NOT NULL,
                    itens TEXT
                );
                """;

            stmt.execute(createHinosTable);
            stmt.execute(createLouvoresTable);
            stmt.execute(createVersiculosTable);
            stmt.execute(createHistoricoTable);

            System.out.println("Banco de dados inicializado com sucesso.");

            // Popular com dados de exemplo se as tabelas estiverem vazias
            popularDadosExemplo(conn);

        } catch (SQLException e) {
            System.err.println("Erro ao inicializar banco de dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Popula o banco com dados de exemplo
     */
    private static void popularDadosExemplo(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            // Verificar se já existem dados
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM hinos");
            if (rs.next() && rs.getInt(1) == 0) {
                // Inserir alguns hinos de exemplo da Harpa
                String[] hinosExemplo = {
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('001', 'Chuvas de Graça', 'Chuvas de bênçãos teremos\n\nChuvas de bênçãos teremos\nQue Jesus prometeu\nDa sua graça os riachos correrão\nChuvas teremos de bênçãos dos céus\n\nChuvas de bênçãos\nChuvas de bênçãos queremos\nChuvas de bênçãos\nDe Jesus queremos já', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('002', 'A Ti, ó Deus, Supremo Bem', 'A Ti, ó Deus, Supremo Bem\n\nA Ti, ó Deus, Supremo Bem\nDe todo o coração\nElevo a minha gratidão\nEm santa adoração\n\nPor Tua graça, ó meu Senhor\nQue tudo me supriu\nTe adorarei com fervor\nEnquanto aqui vivi', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('003', 'Vem a Mim', 'Vem a Mim\n\nVem a Mim, ó pecador\nEis abertos os Meus braços para ti\nTenho gozo e vida aqui\nVem a Mim! Vem a Mim!\n\nEis repleto de amor\nO Meu peito aberto está\nVem, ali acharás\nPlena paz! Plena paz!', 'harpa')"
                };

                for (String sql : hinosExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("Hinos de exemplo inseridos.");
            }

            // Verificar louvores
            rs = stmt.executeQuery("SELECT COUNT(*) FROM louvores");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] louvoresExemplo = {
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Porque Ele Vive', 'Bill Gaither', 'Deus enviou Seu Filho amado\n\nDeus enviou Seu Filho amado\nPara morrer em meu lugar\nNa cruz morreu por meus pecados\nMas vivo está, foi me salvar\n\nPorque Ele vive, posso crer no amanhã\nPorque Ele vive, temor não há\nMas eu bem sei que o meu futuro\nEstá nas mãos do meu Jesus que vivo está')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Quão Grande és Tu', 'Carl Boberg', 'Senhor, meu Deus\n\nSenhor, meu Deus, quando eu maravilhado\nFico a pensar nas obras de Tuas mãos\nO céu azul, de estrelas pontilhado\nO Teu poder mostrando a criação\n\nEntão minh alma canta a Ti, Senhor\nQuão grande és Tu! Quão grande és Tu!\nEntão minh alma canta a Ti, Senhor\nQuão grande és Tu! Quão grande és Tu!')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Bondade de Deus', 'Jenn Johnson', 'Eu amo a Ti, ó Senhor\n\nEu amo a Ti, ó Senhor, és meu amparo\nEu creio em Ti, ó Senhor, és o meu Deus\nEu Te louvarei todos os meus dias\nSim, cantarei, Tu és bom, sempre és bom\n\nTua bondade me seguirá\nMe seguirá, Senhor\nMinha vida entregarei\nPra Te adorar')"
                };

                for (String sql : louvoresExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("Louvores de exemplo inseridos.");
            }

            // Verificar versículos
            rs = stmt.executeQuery("SELECT COUNT(*) FROM versiculos");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] versiculosExemplo = {
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('João', 3, 16, 'Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Salmos', 23, 1, 'O Senhor é o meu pastor; nada me faltará.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Filipenses', 4, 13, 'Tudo posso naquele que me fortalece.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Romanos', 8, 28, 'E sabemos que todas as coisas contribuem juntamente para o bem daqueles que amam a Deus, daqueles que são chamados segundo o seu propósito.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Provérbios', 3, 5, 'Confia no Senhor de todo o teu coração, e não te estribes no teu próprio entendimento.', 'ARC')"
                };

                for (String sql : versiculosExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("Versículos de exemplo inseridos.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao popular dados de exemplo: " + e.getMessage());
        }
    }
}
