# CADEALT MASTER

Sistema de Projeção para Igrejas desenvolvido em JavaFX 21

## Descrição

CADEALT MASTER é um sistema completo de projeção desenvolvido especialmente para igrejas, permitindo a exibição de letras de músicas, versículos bíblicos, anúncios e apresentações multimídia.

## Tecnologias

- **Java 21**
- **JavaFX 21** - Framework para interface gráfica
- **Maven** - Gerenciamento de dependências
- **SQLite** - Banco de dados
- **ControlsFX** - Componentes adicionais para JavaFX

## Estrutura do Projeto

```
cadealt-master/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── com/cadealt/
│   │   │   │   ├── model/          # Modelos de dados
│   │   │   │   ├── view/           # Classes de visualização
│   │   │   │   ├── controller/     # Controladores MVC
│   │   │   │   ├── dao/            # Data Access Objects
│   │   │   │   ├── util/           # Classes utilitárias
│   │   │   │   └── CadealtMaster.java  # Classe principal
│   │   │   └── module-info.java    # Configuração do módulo
│   │   └── resources/
│   │       ├── fxml/               # Arquivos FXML
│   │       ├── css/                # Folhas de estilo
│   │       └── db/                 # Banco de dados
│   └── test/
│       └── java/                   # Testes unitários
└── pom.xml                         # Configuração Maven
```

## Requisitos

- Java Development Kit (JDK) 21 ou superior
- Maven 3.8 ou superior

## Como Executar

### Compilar o projeto

```bash
mvn clean compile
```

### Executar a aplicação

```bash
mvn javafx:run
```

### Gerar JAR executável

```bash
mvn clean package
```

## Funcionalidades Planejadas

- [ ] Projeção de letras de músicas
- [ ] Exibição de versículos bíblicos
- [ ] Gerenciamento de playlists
- [ ] Suporte para múltiplos monitores
- [ ] Temas personalizáveis
- [ ] Importação/Exportação de conteúdo
- [ ] Controle remoto via rede

## Licença

Todos os direitos reservados © 2024

## Autor

CADEALT Development Team
