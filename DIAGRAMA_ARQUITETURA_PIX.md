# 📊 DIAGRAMA DE ARQUITETURA DO BANCO DE DADOS PIX

## Estrutura de Tabelas e Relacionamentos

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         SISTEMA VALIDADOR - NEWPIX                         │
└─────────────────────────────────────────────────────────────────────────────┘

┌──────────────────────────────────────┐
│          TABELA: usuarios            │
├──────────────────────────────────────┤
│ ⚡ cpf (PRIMARY KEY) - TEXT          │
│    nome - TEXT                       │
│    senha - TEXT (BCrypt)             │
│    saldo - REAL                      │
│    criado_em - TEXT                  │
│    atualizado_em - TEXT              │
│    conta_ativa - INTEGER             │
└──────────────────────────────────────┘
         ↑                      ↑
         │ FK                   │ FK
         │                      │
┌────────┴──────────────────┐  │
│   TABELA: chaves_pix      │  │
├───────────────────────────┤  │
│ ⚡ id (PRIMARY KEY)       │  │
│    cpf_dono → usuarios    │◄─┘
│    tipo_chave - TEXT      │
│    valor_chave (UNIQUE)   │  (email, telefone,
│    criada_em - TEXT       │   cpf, cnpj, aleatoria)
│    ativa - INTEGER        │
└───────────────────────────┘
         │
         │ (valor_chave)
         ↓
    ┌─────────────────────┐
    │  Buscar CPF por     │
    │   chave Pix ←────── Resolução
    └─────────────────────┘
         ↓
┌────────────────────────────────────────────────┐
│     TABELA: transacoes_pix                     │
├────────────────────────────────────────────────┤
│ ⚡ id (PRIMARY KEY)                            │
│    chave_pix_origem - TEXT                    │
│    chave_pix_destino - TEXT                   │
│    cpf_origem → usuarios (FK)                 │
│    cpf_destino → usuarios (FK)                │
│    valor - REAL                               │
│    timestamp - TEXT                           │
│    status - TEXT (sucesso/pendente/falha)     │
│    identificador_pix - TEXT (UNIQUE)          │
└────────────────────────────────────────────────┘
         ↑
         │
    ┌────┴────────────────────┐
    │  ÍNDICES PARA BUSCA:   │
    ├────────────────────────┤
    │ • idx_transacoes_pix   │
    │   _origem (cpf_origem) │
    │                        │
    │ • idx_transacoes_pix   │
    │   _destino (cpf_dest)  │
    │                        │
    │ • idx_transacoes_pix   │
    │   _timestamp           │
    └────────────────────────┘

┌──────────────────────────────────────┐
│  TABELA: transacoes (TRADICIONAL)    │
├──────────────────────────────────────┤
│ ⚡ id (PRIMARY KEY)                  │
│    cpf_origem - TEXT                 │
│    cpf_destino - TEXT                │
│    valor - REAL                      │
│    timestamp - TEXT                  │
│    tipo_transacao - TEXT             │
│    (transferencia/deposito)          │
└──────────────────────────────────────┘
         (Mantém compatibilidade)
```

---

## 🔄 Fluxo de Uma Transferência Pix

```
┌─ Usuário A deseja enviar R$ 100 para Usuário B via Pix

├─ PASSO 1: RESOLVER CHAVE PIX
│  ├─ Entrada: "maria@email.com"
│  ├─ SELECT cpf_dono FROM chaves_pix WHERE valor_chave = 'maria@email.com'
│  └─ Saída: "987.654.321-02"
│
├─ PASSO 2: VALIDAR SALDO (Lógica de Negócio)
│  ├─ SELECT saldo FROM usuarios WHERE cpf = '123.456.789-01'
│  └─ Se saldo >= 100.00 → continua
│
├─ PASSO 3: EXECUTAR TRANSFERÊNCIA (Lógica de Negócio)
│  ├─ UPDATE usuarios SET saldo = saldo - 100 WHERE cpf = '123.456.789-01'
│  ├─ UPDATE usuarios SET saldo = saldo + 100 WHERE cpf = '987.654.321-02'
│  └─ COMMIT
│
├─ PASSO 4: REGISTRAR TRANSAÇÃO PIX
│  ├─ INSERT INTO transacoes_pix (
│  │     chave_pix_origem = 'joao@email.com',
│  │     chave_pix_destino = 'maria@email.com',
│  │     cpf_origem = '123.456.789-01',
│  │     cpf_destino = '987.654.321-02',
│  │     valor = 100.00,
│  │     timestamp = NOW(),
│  │     identificador_pix = 'UUID-ÚNICO'
│  │  )
│  └─ COMMIT
│
└─ PASSO 5: SUCESSO! Transferência concluída
```

---

## 📈 Índices de Performance

```
┌─────────────────────────────────────────┐
│     ÍNDICES CRIADOS PARA OTIMIZAÇÃO    │
├─────────────────────────────────────────┤
│                                         │
│  TABELA: transacoes                     │
│  ├─ idx_transacoes_origem               │
│  │  └─ Busca: WHERE cpf_origem = ?     │
│  ├─ idx_transacoes_destino              │
│  │  └─ Busca: WHERE cpf_destino = ?    │
│  └─ idx_transacoes_timestamp            │
│     └─ Ordem: ORDER BY timestamp DESC   │
│                                         │
│  TABELA: chaves_pix                     │
│  ├─ idx_chaves_pix_dono                 │
│  │  └─ Busca: WHERE cpf_dono = ?       │
│  ├─ idx_chaves_pix_valor                │
│  │  └─ Busca: WHERE valor_chave = ?    │
│  └─ idx_chaves_pix_tipo                 │
│     └─ Filtro: WHERE tipo_chave = ?    │
│                                         │
│  TABELA: transacoes_pix                 │
│  ├─ idx_transacoes_pix_origem           │
│  │  └─ Busca: WHERE cpf_origem = ?     │
│  ├─ idx_transacoes_pix_destino          │
│  │  └─ Busca: WHERE cpf_destino = ?    │
│  └─ idx_transacoes_pix_timestamp        │
│     └─ Ordem: ORDER BY timestamp DESC   │
│                                         │
└─────────────────────────────────────────┘
```

---

## 🔑 Tipos de Chaves Pix Suportadas

```
┌──────────────────────────────────────────────────────────────┐
│            TIPOS DE CHAVES PIX DISPONÍVEIS                   │
├──────────────────────────────────────────────────────────────┤
│                                                              │
│  1️⃣  CPF                                                    │
│      ├─ Formato: 12345678901 (sem formatação)              │
│      ├─ Uso: Transferência entre pessoa física             │
│      └─ Exemplo: 12345678901                               │
│                                                              │
│  2️⃣  EMAIL                                                  │
│      ├─ Formato: usuario@dominio.com                       │
│      ├─ Uso: Identificação via email                       │
│      └─ Exemplo: joao@email.com                            │
│                                                              │
│  3️⃣  TELEFONE                                               │
│      ├─ Formato: +55XXXXXXXXXX                             │
│      ├─ Uso: Transferência via número                      │
│      └─ Exemplo: +5511999999999                            │
│                                                              │
│  4️⃣  CNPJ                                                   │
│      ├─ Formato: 12345678000195 (sem formatação)          │
│      ├─ Uso: Transferência para pessoa jurídica            │
│      └─ Exemplo: 12345678000195                            │
│                                                              │
│  5️⃣  CHAVE ALEATÓRIA                                        │
│      ├─ Formato: UUID (xxxxxxxx-xxxx-xxxx-xxxx-xxxx...)   │
│      ├─ Uso: Chave gerada aleatoriamente                   │
│      └─ Exemplo: a1b2c3d4-e5f6-7890-abcd-ef1234567890     │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

---

## 🎯 Operações Principais

```
┌──────────────────────────────────────────────┐
│      API DE MÉTODOS - OPERAÇÕES PYDIX       │
├──────────────────────────────────────────────┤
│                                              │
│  📝 REGISTRAR CHAVE PIX                      │
│  ├─ registrarChavePix(cpf, tipo, valor)     │
│  ├─ Entrada: CPF, tipo da chave, valor      │
│  └─ Saída: true/false                       │
│                                              │
│  🔍 BUSCAR POR CHAVE PIX                     │
│  ├─ buscarCpfPorChavePix(valor)             │
│  ├─ Entrada: Valor da chave                 │
│  └─ Saída: CPF do proprietário              │
│                                              │
│  📋 LISTAR CHAVES DO USUÁRIO                 │
│  ├─ listarChavesPix(cpf)                    │
│  ├─ Entrada: CPF                            │
│  └─ Saída: Lista de chaves                  │
│                                              │
│  🚫 DESATIVAR CHAVE PIX                      │
│  ├─ desativarChavePix(valor)                │
│  ├─ Entrada: Valor da chave                 │
│  └─ Saída: true/false                       │
│                                              │
│  💳 REGISTRAR TRANSAÇÃO PIX                  │
│  ├─ registrarTransacaoPix(...)              │
│  ├─ Entrada: Chaves, CPFs, valor, ID        │
│  └─ Saída: true/false                       │
│                                              │
│  📊 LISTAR TRANSAÇÕES PIX                    │
│  ├─ listarTransacoesPix(cpf)                │
│  ├─ Entrada: CPF                            │
│  └─ Saída: Lista de transações (JSON)       │
│                                              │
│  📈 CONTAR CHAVES ATIVAS                     │
│  ├─ countChavesPix()                        │
│  └─ Saída: Número total                     │
│                                              │
│  📊 CONTAR TRANSAÇÕES PIX                    │
│  ├─ countTransacoesPix()                    │
│  └─ Saída: Número total                     │
│                                              │
└──────────────────────────────────────────────┘
```

---

## 🛡️ Segurança e Integridade

```
┌──────────────────────────────────────────────────┐
│    MECANISMOS DE SEGURANÇA IMPLEMENTADOS       │
├──────────────────────────────────────────────────┤
│                                                  │
│  ✅ CHAVES ÚNICAS                               │
│     └─ UNIQUE(valor_chave) previne duplicatas   │
│                                                  │
│  ✅ INTEGRIDADE REFERENCIAL                     │
│     ├─ Foreign Key: cpf_dono → usuarios         │
│     ├─ Foreign Key: cpf_origem → usuarios       │
│     └─ Foreign Key: cpf_destino → usuarios      │
│                                                  │
│  ✅ IDENTIFICADOR ÚNICO POR TRANSAÇÃO            │
│     └─ UNIQUE(identificador_pix) previne        │
│        duplicação de transações                 │
│                                                  │
│  ✅ RASTREAMENTO COMPLETO                       │
│     ├─ Timestamp em cada operação               │
│     ├─ Status registrado                        │
│     └─ Histórico imutável                       │
│                                                  │
│  ✅ ENCRIPTAÇÃO                                 │
│     └─ Senhas com BCrypt                        │
│                                                  │
│  ✅ CONTROLE DE ACESSO                          │
│     ├─ Chave Pix vinculada a CPF                │
│     ├─ Usuário só acessa seus dados             │
│     └─ Token para autenticação                  │
│                                                  │
└──────────────────────────────────────────────────┘
```

---

## 📊 Exemplo de Dados no Banco

```
┌─ TABELA: usuarios
│  CPF              │ Nome              │ Saldo
│  123.456.789-01   │ João Silva        │ 500.00
│  987.654.321-02   │ Maria Santos      │ 300.00
│  111.222.333-44   │ Pedro Oliveira    │ 1000.00

├─ TABELA: chaves_pix
│  ID │ CPF            │ Tipo  │ Valor              │ Ativa
│  1  │ 123.456.789-01 │ email │ joao@email.com     │ 1
│  2  │ 123.456.789-01 │ tel   │ +5511987654321     │ 1
│  3  │ 987.654.321-02 │ email │ maria@email.com    │ 1
│  4  │ 111.222.333-44 │ cpf   │ 11122233344        │ 1

└─ TABELA: transacoes_pix
   ID │ Origem         │ Destino        │ Valor │ Status
   1  │ joao@email     │ maria@email    │ 100   │ sucesso
   2  │ maria@email    │ cpf:111...     │ 50    │ sucesso
   3  │ joao@email     │ maria@email    │ 25    │ sucesso
```

---

## 🚀 Próximas Integrações

```
┌─────────────────────────────────────────────────────┐
│         CAMADAS DE INTEGRAÇÃO FUTURA               │
├─────────────────────────────────────────────────────┤
│                                                     │
│  Camada 1: API REST                                 │
│  ├─ POST   /api/pix/registrar-chave                 │
│  ├─ GET    /api/pix/chaves/{cpf}                    │
│  ├─ POST   /api/pix/transferir                      │
│  └─ GET    /api/pix/transacoes/{cpf}                │
│                                                     │
│  Camada 2: GUI (ClientGUI)                          │
│  ├─ Tela de Registro de Chaves Pix                  │
│  ├─ Tela de Transferência Pix                       │
│  ├─ Visualizador de Histórico                       │
│  └─ Gerenciador de Chaves                           │
│                                                     │
│  Camada 3: ServerHandler                            │
│  ├─ handleRegistrarChavePix()                       │
│  ├─ handleTransferenciaPix()                        │
│  ├─ handleListarChavesPix()                         │
│  └─ handleListarTransacoesPix()                     │
│                                                     │
│  Camada 4: Validações                               │
│  ├─ Validar formato de email                        │
│  ├─ Validar formato de telefone                     │
│  ├─ Validar CPF/CNPJ                                │
│  └─ Validar UUID de chave aleatória                 │
│                                                     │
└─────────────────────────────────────────────────────┘
```

---

**Diagrama Atualizado:** 12 de novembro de 2025  
**Versão:** 1.0.0 - NewPix
