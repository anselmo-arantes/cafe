# Catalog Microservice (MVP)

Microserviço de catálogo para o e-commerce MVP da **cafeteira portátil**, construído com Java 21 + Spring Boot + DynamoDB.

## Stack
- Java 21
- Spring Boot 3.x (Web, Validation, Actuator)
- Springdoc OpenAPI (Swagger)
- AWS SDK v2 (DynamoDbClient + DynamoDbEnhancedClient)
- Maven
- JUnit 5 + Mockito

## Como executar

### Pré-requisitos
- Java 21
- Maven 3.9+
- AWS credentials configuradas via provider chain padrão (`~/.aws/credentials`, env vars, role etc.)
- Tabela DynamoDB existente

### Rodar localmente
```bash
mvn spring-boot:run
```

### Rodar testes
```bash
mvn test
```

## Endpoints

### Público
- `GET /api/v1/catalog/products/{sku}`
- `GET /api/v1/catalog/products/{sku}/availability`

### Admin
- `POST /api/v1/admin/catalog/products`
- `PUT /api/v1/admin/catalog/products/{id}`
- `PATCH /api/v1/admin/catalog/products/{id}/price`
- `PATCH /api/v1/admin/catalog/products/{id}/stock`
- `PATCH /api/v1/admin/catalog/products/{id}/status`
- `GET /api/v1/admin/catalog/products`

### Técnico
- `GET /actuator/health`

### Estoque
- `GET /api/v1/inventory/{sku}`
- `PATCH /api/v1/admin/inventory/{sku}/adjust`
- `POST /api/v1/inventory/reservations`
- `POST /api/v1/inventory/reservations/{reservationId}/confirm`
- `POST /api/v1/inventory/reservations/{reservationId}/release`

### Checkout
- `POST /api/v1/checkout`

### Pedidos
- `GET /api/v1/orders/{orderId}`
- `GET /api/v1/admin/orders`
- `PATCH /api/v1/orders/{orderId}/status`

### Pagamento
- `POST /api/v1/payments`
- `GET /api/v1/payments/{paymentId}`
- `POST /api/v1/payments/webhook`

### Carrinho
- `GET /api/v1/cart/{cartId}`
- `POST /api/v1/cart/{cartId}/items`
- `PATCH /api/v1/cart/{cartId}/items/{sku}`
- `DELETE /api/v1/cart/{cartId}/items/{sku}`
- `DELETE /api/v1/cart/{cartId}/items`

## Swagger
- UI: `http://localhost:8080/swagger-ui.html`
- JSON: `http://localhost:8080/v3/api-docs`

## Exemplo de chamadas

### Buscar produto público
```bash
curl -X GET http://localhost:8080/api/v1/catalog/products/CAFETEIRA-PORTATIL-001
```

### Criar produto (admin)
```bash
curl -X POST http://localhost:8080/api/v1/admin/catalog/products \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "CAFETEIRA-PORTATIL-001",
    "name": "Cafeteira Portátil",
    "shortDescription": "Cafeteira portátil para preparo rápido em qualquer lugar.",
    "fullDescription": "Modelo portátil, compacto e ideal para viagens.",
    "price": 249.90,
    "active": true,
    "stockQuantity": 25,
    "mainImageUrl": "https://cdn.exemplo.com/images/cafeteira-main.jpg",
    "imageUrls": [
      "https://cdn.exemplo.com/images/cafeteira-main.jpg",
      "https://cdn.exemplo.com/images/cafeteira-side.jpg"
    ]
  }'
```


### Buscar carrinho
```bash
curl -X GET http://localhost:8080/api/v1/cart/cart-1
```

### Adicionar item no carrinho
```bash
curl -X POST http://localhost:8080/api/v1/cart/cart-1/items \
  -H "Content-Type: application/json" \
  -d '{
    "sku": "CAFETEIRA-PORTATIL-001",
    "quantity": 1
  }'
```


### Consultar estoque
```bash
curl -X GET http://localhost:8080/api/v1/inventory/CAFETEIRA-PORTATIL-001
```

### Ajustar estoque (admin)
```bash
curl -X PATCH http://localhost:8080/api/v1/admin/inventory/CAFETEIRA-PORTATIL-001/adjust \
  -H "Content-Type: application/json" \
  -d '{
    "availableQuantity": 30
  }'
```

### Reservar estoque
```bash
curl -X POST http://localhost:8080/api/v1/inventory/reservations \
  -H "Content-Type: application/json" \
  -d '{
    "reservationId": "res-001",
    "sku": "CAFETEIRA-PORTATIL-001",
    "quantity": 2
  }'
```


### Executar checkout
```bash
curl -X POST http://localhost:8080/api/v1/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "cartId": "cart-1",
    "customerEmail": "cliente@exemplo.com"
  }'
```

### Confirmar pagamento (webhook simulado)
```bash
curl -X POST http://localhost:8080/api/v1/payments/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "paymentId": "PAYMENT_ID",
    "status": "PAID"
  }'
```

## Imagens Docker no Amazon ECR

### Repositório e URI esperada
O repositório ECR deste serviço deve ser `cafe`, com URI no formato:

```text
<account-id>.dkr.ecr.sa-east-1.amazonaws.com/cafe:<tag>
```

Com a estratégia atual de versionamento, a imagem publicada segue este padrão:

```text
<account-id>.dkr.ecr.sa-east-1.amazonaws.com/cafe:<numero_incremental>
<account-id>.dkr.ecr.sa-east-1.amazonaws.com/cafe:latest
```

### Nova estratégia de versionamento
A estratégia de versionamento adotada para publicação no ECR usa `github.run_number` como versão numérica incremental.

Toda publicação da imagem deve gerar **duas tags** para o mesmo artefato:
- `numero_incremental`, derivado de `github.run_number`, com valor inteiro crescente a cada execução do workflow.
- `latest`, apenas como alias de conveniência para validações rápidas e uso manual.

Motivos para essa escolha:
- gera um número simples de ler e comunicar operacionalmente;
- facilita o rastreamento no histórico do GitHub Actions;
- substitui a necessidade de usar `${{ github.sha }}` como identificador principal da versão publicada.

Diretriz operacional:
- Ambientes estáveis (`staging` validado, `production` e rollback) devem referenciar sempre a tag numérica incremental.
- A tag `latest` não deve ser a referência oficial de deploy em ambientes estáveis.
- A rastreabilidade deve ser feita correlacionando o `github.run_number` da imagem com a execução correspondente do GitHub Actions.

### Uso recomendado por ambiente
- **Desenvolvimento / validação rápida:** pode usar `latest` para inspeção manual e smoke tests informais.
- **Ambientes estáveis:** usar somente `cafe:<numero_incremental>`.
- **Auditoria e investigação:** localizar a imagem pelo número incremental e correlacionar com a execução do pipeline no GitHub Actions.

### Procedimento de rollback
Quando for necessário reverter uma entrega:
1. Identifique a última tag numérica conhecida como estável no ECR ou no histórico de deploys.
2. Atualize o manifesto, variável de ambiente ou parâmetro de deploy para reapontar a aplicação para `cafe:<numero_incremental_anterior>`.
3. Execute o deploy reutilizando a mesma imagem já publicada, sem rebuild.
4. Valide saúde, smoke tests e logs após a reversão.

Exemplo:
```text
<account-id>.dkr.ecr.sa-east-1.amazonaws.com/cafe:152
```

Se a versão atual falhar e `152` for a última release estável, o rollback deve reaproveitar exatamente essa tag numérica anterior.

### Evolução futura do repositório ECR
Se no futuro o repositório `cafe` passar a hospedar mais de um serviço, evitar colisões operacionais passa a ser obrigatório. Nesse cenário, adotar uma destas estratégias:
- migrar para um repositório por serviço; ou
- padronizar tags com prefixo de serviço, como `catalog-152`, `inventory-152` e `checkout-152`.

Enquanto houver apenas um serviço por repositório, a convenção `cafe:<numero_incremental>` + `cafe:latest` é suficiente.

## Configuração DynamoDB

### Tabela
- Nome: `catalog_products`
- Partition key: `id` (String)

### Tabela de carrinho
- Nome: `cart_items`
- Partition key: `cartId` (String)
- Sort key: `sku` (String)

### Tabela de estoque
- Nome: `inventory_items`
- Partition key: `sku` (String)

### Tabela de reservas de estoque
- Nome: `inventory_reservations`
- Partition key: `reservationId` (String)

### Tabela de pedidos
- Nome: `orders`
- Partition key: `orderId` (String)

### Tabela de pagamentos
- Nome: `payments`
- Partition key: `paymentId` (String)

### GSI
- Nome: `sku-index`
- Partition key: `sku` (String)

### Exemplo com AWS CLI
```bash
aws dynamodb create-table \
  --table-name catalog_products \
  --attribute-definitions \
    AttributeName=id,AttributeType=S \
    AttributeName=sku,AttributeType=S \
  --key-schema AttributeName=id,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --global-secondary-indexes '[
    {
      "IndexName": "sku-index",
      "KeySchema": [{"AttributeName":"sku","KeyType":"HASH"}],
      "Projection": {"ProjectionType":"ALL"}
    }
  ]'
```

## Configurações (`application.yml`)
- `server.port`
- `springdoc.*`
- `management.*` (health)
- `aws.region`
- `aws.dynamodb.table-name`
- `aws.dynamodb.sku-index-name`
- `aws.dynamodb.cart-table-name`
- `aws.dynamodb.inventory-table-name`
- `aws.dynamodb.inventory-reservation-table-name`
- `aws.dynamodb.order-table-name`
- `aws.dynamodb.payment-table-name`
- `app.seed-enabled`

## Seed opcional
Se `app.seed-enabled=true`, o serviço tenta inserir automaticamente o produto padrão da cafeteira portátil ao iniciar, caso o SKU ainda não exista.

## Migração para microserviços independentes (plano)

Documentos de execução da migração incremental:
- `docs/architecture/microservices-boundaries.md`
- `docs/migration/repository-blueprint.md`
- `docs/migration/data-ownership-and-persistence.md`
- `docs/migration/extraction-roadmap.md`
- `docs/platform/api-gateway-and-resilience.md`
- `docs/cicd/contracts-and-pipelines.md`
- `docs/migration/rollout-and-cutover.md`
- `templates/service-blueprint/`
