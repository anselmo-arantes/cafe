# GitHub Actions + AWS OIDC para push no Amazon ECR

Este documento elimina credenciais estáticas do GitHub Actions e usa identidade federada entre GitHub e AWS para publicar a imagem Docker do serviço no Amazon ECR.

## Escopo
- Repositório GitHub: este repositório.
- Branch autorizada: `main`.
- Região AWS: `sa-east-1`.
- Repositório ECR: `cafe`.
- Secret GitHub obrigatório: `AWS_ROLE_TO_ASSUME`.
- Variables GitHub obrigatórias:
  - `AWS_REGION=sa-east-1`
  - `ECR_REPOSITORY=cafe`

## 1. Garantir o provider OIDC do GitHub na conta AWS

Valide se o provider `https://token.actions.githubusercontent.com` já existe:

```bash
aws iam list-open-id-connect-providers
```

Se não existir, crie com o audience `sts.amazonaws.com`:

```bash
aws iam create-open-id-connect-provider \
  --url https://token.actions.githubusercontent.com \
  --client-id-list sts.amazonaws.com \
  --thumbprint-list 6938fd4d98bab03faadb97b34396831e3780aea1
```

> Observação: o thumbprint pode mudar ao longo do tempo. Valide o valor antes de executar em produção.

## 2. Criar a IAM Role exclusiva para o workflow deste repositório

Substitua `<owner>`, `<repo>` e `<account-id>` pelos valores reais.

### Trust policy restrita

Crie o arquivo `github-oidc-trust-policy.json`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::<account-id>:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
          "token.actions.githubusercontent.com:sub": "repo:<owner>/<repo>:ref:refs/heads/main"
        }
      }
    }
  ]
}
```

Crie a role:

```bash
aws iam create-role \
  --role-name GitHubActionsCafeEcrPushRole \
  --assume-role-policy-document file://github-oidc-trust-policy.json
```

## 3. Anexar permissões mínimas para push no ECR

Crie o arquivo `github-ecr-push-policy.json`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "ecr:GetAuthorizationToken",
        "ecr:BatchCheckLayerAvailability",
        "ecr:CompleteLayerUpload",
        "ecr:InitiateLayerUpload",
        "ecr:UploadLayerPart",
        "ecr:PutImage",
        "ecr:BatchGetImage"
      ],
      "Resource": "*"
    }
  ]
}
```

Crie e anexe a policy inline na role:

```bash
aws iam put-role-policy \
  --role-name GitHubActionsCafeEcrPushRole \
  --policy-name GitHubActionsCafeEcrPushPolicy \
  --policy-document file://github-ecr-push-policy.json
```

## 4. Criar ou validar o repositório ECR `cafe` em `sa-east-1`

Verifique se o repositório já existe:

```bash
aws ecr describe-repositories \
  --region sa-east-1 \
  --repository-names cafe
```

Se não existir, crie:

```bash
aws ecr create-repository \
  --region sa-east-1 \
  --repository-name cafe
```

## 5. Configurar GitHub repository settings → Secrets and variables → Actions

### Secret
Crie o secret:
- `AWS_ROLE_TO_ASSUME=arn:aws:iam::<account-id>:role/GitHubActionsCafeEcrPushRole`

### Variables
Crie as variables:
- `AWS_REGION=sa-east-1`
- `ECR_REPOSITORY=cafe`

## 6. Workflow GitHub Actions do repositório

O workflow versionado neste repositório está em `.github/workflows/publish-ecr.yml` e faz:
1. validação do secret e das variables obrigatórias;
2. autenticação com AWS via OIDC (`id-token: write`);
3. login no ECR;
4. build da imagem Docker;
5. push das tags `${GITHUB_SHA}` e `latest`.

## 7. Validação pós-configuração

### Validar permissões da role
```bash
aws iam get-role --role-name GitHubActionsCafeEcrPushRole
aws iam get-role-policy --role-name GitHubActionsCafeEcrPushRole --policy-name GitHubActionsCafeEcrPushPolicy
```

### Validar o repositório ECR
```bash
aws ecr describe-repositories --region sa-east-1 --repository-names cafe
```

### Validar no GitHub
- Acesse `Settings` → `Secrets and variables` → `Actions`.
- Confirme o secret `AWS_ROLE_TO_ASSUME`.
- Confirme as variables `AWS_REGION` e `ECR_REPOSITORY`.
- Execute manualmente o workflow `Build and publish image to Amazon ECR` ou faça push na branch `main`.

## 8. Resultado esperado

Após a configuração:
- o GitHub Actions não precisa mais de `AWS_ACCESS_KEY_ID` ou `AWS_SECRET_ACCESS_KEY`;
- apenas a branch `main` do repositório configurado consegue assumir a role;
- a imagem da aplicação é publicada no ECR `cafe` em `sa-east-1`.
