# AGENTS.md

## 1) Objetivo do projeto
Implementar um microserviço de catálogo para o e-commerce MVP da cafeteira portátil, com API HTTP em Spring Boot, persistência no DynamoDB e foco em simplicidade, clareza, baixo custo e evolução futura.

## 2) Escopo funcional
- Expor dados públicos do produto por SKU.
- Expor disponibilidade do produto por SKU.
- Permitir operações administrativas de criação, atualização geral, atualização de preço, atualização de estoque, ativação/desativação e listagem.
- Persistir dados no DynamoDB usando AWS SDK v2 e DynamoDbEnhancedClient.
- Expor health check via Actuator.
- Expor documentação OpenAPI/Swagger.
- Incluir testes unitários essenciais.

## 3) Stack obrigatória
- Java 21
- Spring Boot 3.x
- Spring Web
- Spring Validation
- Spring Boot Actuator
- Springdoc OpenAPI / Swagger
- AWS SDK v2 para DynamoDB
- DynamoDbEnhancedClient
- Lombok
- Maven
- JUnit 5
- Mockito

## 4) Estrutura de pastas
- `src/main/java/com/anselmo/ecommerce/catalog`
  - `controller`
  - `dto`
  - `domain`
  - `repository`
  - `service`
  - `exception`
  - `config`
  - `mapper`
- `src/test/java/com/anselmo/ecommerce/catalog`
  - `controller`
  - `service`
  - `mapper`

## 5) Convenções de código
- Nomes claros e descritivos.
- Controllers finos, regras no service.
- Repository abstrai acesso ao DynamoDB.
- DTOs separados por caso de uso.
- Validação com Bean Validation nos requests.
- Erros HTTP padronizados via `@RestControllerAdvice`.
- Sem `TODO` no código final.
- Sem overengineering.

## 6) Critérios de aceite
1. Projeto compila.
2. AGENTS.md existe e está coerente com este escopo.
3. Endpoints especificados existem.
4. Validações retornam 400 para dados inválidos.
5. Tratamento global de erros implementado.
6. Integração com DynamoDB abstraída em repository.
7. Busca por SKU implementada via GSI.
8. Swagger disponível.
9. README criado com instruções e exemplos.
10. Testes básicos presentes.
11. Código limpo, sem TODOs.
12. Reviewer valida explicitamente os pontos do checklist.

## 7) Restrições
- Não usar JPA.
- Não usar banco relacional.
- Não criar arquitetura complexa.
- Não separar em múltiplos módulos Maven.
- Não implementar autenticação.
- Não implementar mensageria.
- Não implementar cache.
- Não implementar observabilidade avançada.
- Não implementar features fora do escopo.

## 8) Responsabilidades do Builder
- Implementar estrutura e código completo.
- Garantir compilação e testes essenciais.
- Criar documentação mínima executável.
- Seguir estritamente o escopo e restrições.

## 9) Responsabilidades do Reviewer
- Revisar design, consistência e legibilidade.
- Verificar validações e tratamento de erros.
- Verificar aderência ao AGENTS.md e critérios de aceite.
- Sugerir correções objetivas sem overengineering.

## 10) Ordem de implementação
1. Criar AGENTS.md.
2. Criar estrutura do projeto.
3. Criar `pom.xml`.
4. Criar models e DTOs.
5. Criar config DynamoDB.
6. Criar repository.
7. Criar service.
8. Criar controllers.
9. Criar tratamento de erros.
10. Configurar OpenAPI.
11. Criar testes.
12. Criar README.
13. Executar revisão do Reviewer.
14. Aplicar correções finais.

## 11) Definição de pronto
Concluído apenas quando:
- AGENTS.md existe.
- Builder finalizou implementação.
- Reviewer revisou criticamente com checklist explícito.
- Ajustes do review aplicados.
- Projeto está coerente, compilável e pronto para execução local.
