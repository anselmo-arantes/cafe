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

## Configuração DynamoDB

### Tabela
- Nome: `catalog_products`
- Partition key: `id` (String)

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
- `app.seed-enabled`

## Seed opcional
Se `app.seed-enabled=true`, o serviço tenta inserir automaticamente o produto padrão da cafeteira portátil ao iniciar, caso o SKU ainda não exista.
