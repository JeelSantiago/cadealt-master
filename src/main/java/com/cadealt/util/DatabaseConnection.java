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
                // Inserir 10 hinos de exemplo da Harpa
                String[] hinosExemplo = {
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('001', 'Chuvas de Graça', 'Chuvas de bênçãos teremos\n\nChuvas de bênçãos teremos\nQue Jesus prometeu\nDa sua graça os riachos correrão\nChuvas teremos de bênçãos dos céus\n\nChuvas de bênçãos\nChuvas de bênçãos queremos\nChuvas de bênçãos\nDe Jesus queremos já', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('002', 'A Ti, ó Deus, Supremo Bem', 'A Ti, ó Deus, Supremo Bem\n\nA Ti, ó Deus, Supremo Bem\nDe todo o coração\nElevo a minha gratidão\nEm santa adoração\n\nPor Tua graça, ó meu Senhor\nQue tudo me supriu\nTe adorarei com fervor\nEnquanto aqui vivi', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('003', 'Vem a Mim', 'Vem a Mim\n\nVem a Mim, ó pecador\nEis abertos os Meus braços para ti\nTenho gozo e vida aqui\nVem a Mim! Vem a Mim!\n\nEis repleto de amor\nO Meu peito aberto está\nVem, ali acharás\nPlena paz! Plena paz!', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('004', 'Ó Desce, Fogo Santo', 'Ó desce, fogo santo\n\nÓ desce, fogo santo\nTeu poder derrama aqui\nVem queimar o sacrifício\nQue Te oferto com prazer\n\nEstou sobre o altar\nPara Te adorar, ó Deus\nMeu espírito consagro\nPara sempre, só a Ti', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('005', 'Ceia do Senhor', 'Ó que paz, ó que graça\n\nÓ que paz, ó que graça\nNesta Ceia do Senhor\nMeditando em Seu amor\nQue na cruz Ele mostrou\n\nÓ Jesus! Teu sangue\nDerramaste em meu favor\nMas agora vivo estás\nGlória a Ti, meu Salvador!', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('006', 'Creio na Bíblia', 'Creio na Bíblia\n\nCreio na Bíblia, sim, creio na Bíblia\nSó nela posso confiar\nÉ a Palavra de Deus\nQue do céu veio a nós dar\n\nTodos os homens errarão\nMas a Palavra ficará\nPois é a Bíblia\nA Palavra de Jeová', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('007', 'Eu Te Amo, ó Deus', 'Eu te amo, ó Deus\n\nEu te amo, ó Deus\nTua voz ouvi\nFoi assim que encontrei\nPaz e gozo em Ti\n\nMeu Jesus, ao morrer\nMinh\'alma remiu\nNão posso esquecer\nO que por mim sofreu', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('008', 'Em Jesus, Meu Salvador', 'Em Jesus confio\n\nEm Jesus confio, pois Ele me salvou\nNo Seu sangue precioso Ele me lavou\nMinha vida toda hoje consagro a Ti\nÓ meu Salvador, vem em mim habitar\n\nCom Jesus seguro estou\nNo Seu amor vou descansar\nCom Jesus seguro estou\nNão terei que temer', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('009', 'Ao Deus Onipotente', 'Ao Deus onipotente\n\nAo Deus onipotente louvai\nSe manifestou\nE que só Ele é digno\nDe glória e louvor\n\nCantemos aleluia\nAo Rei dos reis, Jesus\nQue por nós padeceu\nE morreu lá na cruz', 'harpa')",
                    "INSERT INTO hinos (numero, titulo, letra, tipo) VALUES ('010', 'Saudai o Nome de Jesus', 'Saudai o nome de Jesus\n\nSaudai o nome de Jesus\nArcanjos, Vosso Rei honrai\nServos do céu, em santa luz\nA coroa a Cristo levai\n\nRemi do mundo o Criador\nA queda humana reparou\nÉ Rei, Senhor e Salvador\nJesus a todos resgatou', 'harpa')"
                };

                for (String sql : hinosExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("✓ 10 Hinos de exemplo inseridos.");
            }

            // Verificar louvores
            rs = stmt.executeQuery("SELECT COUNT(*) FROM louvores");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] louvoresExemplo = {
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Porque Ele Vive', 'Bill Gaither', 'Deus enviou Seu Filho amado\n\nDeus enviou Seu Filho amado\nPara morrer em meu lugar\nNa cruz morreu por meus pecados\nMas vivo está, foi me salvar\n\nPorque Ele vive, posso crer no amanhã\nPorque Ele vive, temor não há\nMas eu bem sei que o meu futuro\nEstá nas mãos do meu Jesus que vivo está')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Quão Grande és Tu', 'Carl Boberg', 'Senhor, meu Deus\n\nSenhor, meu Deus, quando eu maravilhado\nFico a pensar nas obras de Tuas mãos\nO céu azul, de estrelas pontilhado\nO Teu poder mostrando a criação\n\nEntão minh alma canta a Ti, Senhor\nQuão grande és Tu! Quão grande és Tu!\nEntão minh alma canta a Ti, Senhor\nQuão grande és Tu! Quão grande és Tu!')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Bondade de Deus', 'Jenn Johnson', 'Eu amo a Ti, ó Senhor\n\nEu amo a Ti, ó Senhor, és meu amparo\nEu creio em Ti, ó Senhor, és o meu Deus\nEu Te louvarei todos os meus dias\nSim, cantarei, Tu és bom, sempre és bom\n\nTua bondade me seguirá\nMe seguirá, Senhor\nMinha vida entregarei\nPra Te adorar')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Ruja o Leão', 'Davi Sacer', 'Prepare o caminho\n\nPrepare o caminho e as portas levantai\nPra que o Rei da Glória entre\nPrepare o caminho e as portas levantai\nPra que o Rei da Glória entre\n\nQuem é o Rei da Glória?\nO Senhor forte e poderoso\nO Senhor poderoso nas batalhas\n\nRuja o Leão de Judá\nO meu Salvador\nRuja o Leão de Judá\nPra honra e glória do Senhor')",
                    "INSERT INTO louvores (titulo, autor, letra) VALUES ('Oceanos', 'Hillsong United', 'Tu me chamas sobre as águas\n\nTu me chamas sobre as águas\nOnde os meus pés podem falhar\nE ali Te encontro no mistério\nEm meio ao mar, confiarei\n\nAo Teu nome clamarei\nE além das ondas olharei\nSe o mar crescer somente em Ti descansarei\nPois eu sou Teu e Tu és meu\n\nTua graça cobre os meus temores\nTua forte mão me guiará\nSe estou cercado pelo medo\nTu és fiel, nunca vais falhar')"
                };

                for (String sql : louvoresExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("✓ 5 Louvores de exemplo inseridos.");
            }

            // Verificar versículos
            rs = stmt.executeQuery("SELECT COUNT(*) FROM versiculos");
            if (rs.next() && rs.getInt(1) == 0) {
                String[] versiculosExemplo = {
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('João', 3, 16, 'Porque Deus amou o mundo de tal maneira que deu o seu Filho unigênito, para que todo aquele que nele crê não pereça, mas tenha a vida eterna.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Salmos', 23, 1, 'O Senhor é o meu pastor; nada me faltará.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Filipenses', 4, 13, 'Tudo posso naquele que me fortalece.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Romanos', 8, 28, 'E sabemos que todas as coisas contribuem juntamente para o bem daqueles que amam a Deus, daqueles que são chamados segundo o seu propósito.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Provérbios', 3, 5, 'Confia no Senhor de todo o teu coração, e não te estribes no teu próprio entendimento.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Isaías', 40, 31, 'Mas os que esperam no Senhor renovarão as suas forças; subirão com asas como águias; correrão, e não se cansarão; caminharão, e não se fatigarão.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Jeremias', 29, 11, 'Porque eu bem sei os pensamentos que tenho a vosso respeito, diz o Senhor; pensamentos de paz, e não de mal, para vos dar o fim que esperais.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Mateus', 11, 28, 'Vinde a mim, todos os que estais cansados e oprimidos, e eu vos aliviarei.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Salmos', 46, 1, 'Deus é o nosso refúgio e fortaleza, socorro bem presente na angústia.', 'ARC')",
                    "INSERT INTO versiculos (livro, capitulo, versiculo, texto, versao) VALUES ('Josué', 1, 9, 'Não to mandei eu? Esforça-te, e tem bom ânimo; não temas, nem te espantes; porque o Senhor teu Deus é contigo, por onde quer que andares.', 'ARC')"
                };

                for (String sql : versiculosExemplo) {
                    stmt.execute(sql);
                }
                System.out.println("✓ 10 Versículos de exemplo inseridos.");
            }

        } catch (SQLException e) {
            System.err.println("Erro ao popular dados de exemplo: " + e.getMessage());
        }
    }
}
