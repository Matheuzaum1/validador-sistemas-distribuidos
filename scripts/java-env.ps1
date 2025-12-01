# Configuracao automatica do JAVA_HOME para este projeto
# Gerado automaticamente pelo configurar-java.ps1

$env:JAVA_HOME = "C:\Program Files\Common Files\Oracle\Java"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

Write-Host "[OK] Java configurado para o projeto (Java $(java -version 2>&1 | Select-Object -First 1 | ForEach-Object { $_.Split(' ')[2].Trim('"') }))" -ForegroundColor Green
