# Hawkbit MCP Server

Este é um servidor MCP (Model Context Protocol) que integra o Eclipse Hawkbit com LLMs através do Spring AI. Ele permite gerenciar targets, distribuições e ações no Hawkbit usando linguagem natural.

## 🚀 Tecnologias

- Java 21
- Spring Boot 3.5.9
- Spring AI 1.1.2 (MCP Server)
- Eclipse Hawkbit SDK

## ⚙️ Configuração

O servidor é configurado através do arquivo application.yaml. As principais configurações são:

```yaml
hawkbit:
  server:
    mgmt-url: http://localhost:8080 # URL de Gerenciamento do Hawkbit
    ddi-url: http://localhost:8085  # URL de Integração Direta de Dispositivos
server:
  port: 8090 # Porta do servidor MCP
```

## 🛠️ Build e Execução

Para compilar o projeto:

```bash
./mvnw clean package
```

Para executar:

```bash
java -jar target/hawkbit-mcp-server-0.0.1-SNAPSHOT.jar
```

Ou diretamente com Maven:

```bash
./mvnw spring-boot:run
```

## 📦 Funcionalidades (Ferramentas MCP)

Este servidor expõe ferramentas para interagir com o Hawkbit, incluindo:

- Gerenciamento de Targets (Criar, Listar, Atualizar, Deletar)
- Gerenciamento de Tags (Listar, Criar, Atribuir)
- Gerenciamento de Ações e Distribuições

## 🤝 Contribuição

Sinta-se à vontade para abrir issues e pull requests para melhorias.
