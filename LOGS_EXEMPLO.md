========================================
   EXEMPLO DE LOGS - ANTES E DEPOIS
========================================

🔴 ANTES (Inconsistente):
===========================
# Logs normais:
Enviado: {"operacao":"usuario_login","cpf":"123.456.789-01","senha":"123456"}
Recebido: {"operacao":"usuario_login","status":true,"info":"Login realizado","token":"abc123"}

# Logs de erro (problemáticos):
❌ 🔴 PROTOCOLO VIOLATION: usuario_login sem token (seção 4.11)
📤 Erro_servidor enviado e confirmado: {"operacao":"erro_servidor","operacao_enviada":"usuario_login","info":"Resposta usuario_login chegou sem campo 'token' ou token é nulo"}

✅ DEPOIS (Consistente):
==========================
# Logs normais:
Enviado: {"operacao":"usuario_login","cpf":"123.456.789-01","senha":"123456"}
Recebido: {"operacao":"usuario_login","status":true,"info":"Login realizado","token":"abc123"}

# Logs de erro (agora no mesmo formato!):
❌ 🔴 PROTOCOLO VIOLATION: usuario_login sem token (seção 4.11)
Enviado: {"operacao":"erro_servidor","operacao_enviada":"usuario_login","info":"Resposta usuario_login chegou sem campo 'token' ou token é nulo"}
Recebido: {"operacao":"erro_servidor","status":true,"info":"Erro reportado e registrado com sucesso"}

========================================
   PRINCIPAIS MELHORIAS
========================================

✅ FORMATO CONSISTENTE:
   - Todas as mensagens seguem padrão "Enviado: " + JSON
   - Todas as respostas seguem padrão "Recebido: " + JSON

✅ VISIBILIDADE CLARA:
   - Fica evidente quando erro_servidor é enviado
   - Mostra confirmação do servidor
   - Mesmo formato visual das outras operações

✅ FÁCIL DEBUGGING:
   - Logs podem ser copiados e testados diretamente
   - JSON completo visível para análise
   - Confirmação de recebimento explícita

✅ PROTOCOLO TRANSPARENTE:
   - Mostra exatamente o que foi enviado/recebido
   - Permite validar conformidade com protocolo
   - Facilita troubleshooting de problemas

========================================
   EXEMPLO PRÁTICO
========================================

Quando um servidor retorna login sem token, o log mostrará:

1. ❌ 🔴 PROTOCOLO VIOLATION: usuario_login sem token (seção 4.11)
2. Enviado: {"operacao":"erro_servidor","operacao_enviada":"usuario_login","info":"Resposta usuario_login chegou sem campo 'token' ou token é nulo"}
3. Recebido: {"operacao":"erro_servidor","status":true,"info":"Erro reportado e registrado com sucesso"}

Assim fica MUITO claro que:
- Um erro foi detectado (linha 1)
- O erro foi reportado ao servidor (linha 2) 
- O servidor confirmou o recebimento (linha 3)

========================================