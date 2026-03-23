# AGENTS.md

## 1) Objetivo do projeto
e-commerce MVP da cafeteira portátil, com API HTTP em Spring Boot, persistência no DynamoDB e foco em simplicidade, clareza, baixo custo e evolução

## 2) Escopo funcional (macro)
- Expor dados públicos e administrativos de catálogo.
- Suportar fluxo de carrinho, checkout, estoque, pedidos e pagamentos com integração entre serviços.
- Persistência em DynamoDB usando AWS SDK v2 e DynamoDbEnhancedClient.
- Expor health check via Actuator.
- Expor documentação OpenAPI/Swagger por serviço.
- Manter testes unitários essenciais e testes de contrato entre serviços.

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

## 4) Organização alvo (microserviços independentes)
Cada domínio deve ter **repositório e pacote raiz próprios**:

- `com.anselmo.ecommerce.catalog`
- `com.anselmo.ecommerce.cart`
- `com.anselmo.ecommerce.inventory`
- `com.anselmo.ecommerce.checkout`
- `com.anselmo.ecommerce.order`
- `com.anselmo.ecommerce.payment`

### 4.1 Estrutura por serviço (em cada repositório)
- `src/main/java/com/anselmo/ecommerce/<service>`
  - `controller`
  - `dto`
  - `domain`
  - `repository`
  - `service`
  - `exception`
  - `config`
  - `mapper`
- `src/test/java/com/anselmo/ecommerce/<service>`
  - `controller`
  - `service`
  - `mapper`

### 4.2 Estado atual e transição
- Estado atual (temporário): módulos de domínio ainda podem coexistir sob `com.anselmo.ecommerce.catalog`.
- Estado alvo (obrigatório para conclusão da migração): cada serviço isolado em seu repositório, sem dependência de código-fonte direta entre domínios.

## 5) Convenções de código
- Nomes claros e descritivos.
- A cada novo PR, incrementar `project.version` no `pom.xml` antes de publicar ou versionar artefatos/imagens.
- Controllers finos, regras no service.
- Repository abstrai acesso ao DynamoDB.
- DTOs separados por caso de uso.
- Validação com Bean Validation nos requests.
- Erros HTTP padronizados via `@RestControllerAdvice`.
- Sem `TODO` no código final.
- Sem overengineering.

## 6) Critérios de aceite (migração)
1. Projeto compila.
2. AGENTS.md existe e está coerente com a estratégia de microserviços.
3. Endpoints por serviço existem e estão documentados.
4. Validações retornam 400 para dados inválidos.
5. Tratamento global de erros implementado por serviço.
6. Integração com DynamoDB abstraída em repository.
7. Busca por SKU implementada no serviço de catálogo via GSI.
8. Swagger disponível por serviço.
9. README criado/atualizado com instruções e exemplos por serviço.
10. Testes básicos presentes por serviço.
11. Código limpo, sem TODOs.
12. Reviewer valida explicitamente o checklist de migração.
13. Serviços não compartilham banco/tabela sem ownership definido.
14. Comunicação entre serviços ocorre via contrato (HTTP/evento), sem dependência de pacote interno de outro serviço.

## 7) Restrições
- Não usar JPA.
- Não usar banco relacional.
- Não criar arquitetura complexa desnecessária.
- Não implementar autenticação neste escopo.
- Não implementar mensageria fora da necessidade explícita de integração.
- Não implementar cache.
- Não implementar observabilidade avançada além do básico operacional.
- Não implementar features fora do escopo.

## 8) Responsabilidades do Builder
- Implementar estrutura e código completo por serviço.
- Garantir compilação e testes essenciais.
- Criar documentação mínima executável.
- Seguir estritamente o escopo e restrições.
- Planejar e executar migração incremental com rollback seguro.

## 9) Responsabilidades do Reviewer
- Revisar design, consistência e legibilidade.
- Verificar validações e tratamento de erros.
- Verificar aderência ao AGENTS.md e critérios de aceite.
- Validar isolamento de domínio entre serviços.
- Sugerir correções objetivas sem overengineering.

## 10) Ordem de migração recomendada
1. Congelar boundaries e contratos.
2. Extrair Catalog Service.
3. Extrair Inventory Service.
4. Extrair Order Service.
5. Extrair Payment Service.
6. Extrair Cart Service.
7. Extrair Checkout Service (orquestrador).
8. Consolidar testes de contrato e operação.

## 11) Definição de pronto
Concluído apenas quando:
- AGENTS.md existe e reflete organização por microserviços.
- Builder finalizou migração incremental planejada.
- Reviewer revisou criticamente com checklist explícito.
- Ajustes do review aplicados.
- Serviços estão coerentes, compiláveis e prontos para execução local independente.
