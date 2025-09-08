# Script para verificar codificação UTF-8 com BOM
Write-Host "======================================" -ForegroundColor Cyan
Write-Host "  Verificando Codificação UTF-8 BOM" -ForegroundColor Cyan  
Write-Host "======================================" -ForegroundColor Cyan
Write-Host ""

function Test-UTF8BOM {
    param([string]$FilePath)
    
    if (-not (Test-Path $FilePath)) {
        return "File not found"
    }
    
    $bytes = [System.IO.File]::ReadAllBytes($FilePath)
    if ($bytes.Length -ge 3 -and $bytes[0] -eq 0xEF -and $bytes[1] -eq 0xBB -and $bytes[2] -eq 0xBF) {
        return "UTF-8 with BOM ✓"
    } else {
        return "No BOM detected ✗"
    }
}

Write-Host "Verificando arquivos principais..." -ForegroundColor Yellow
Write-Host ""

$files = @(
    "README.md",
    "COMO_USAR.md", 
    "NAVEGACAO.md",
    "pom.xml",
    ".gitignore"
)

foreach ($file in $files) {
    $result = Test-UTF8BOM $file
    Write-Host "$file : $result"
}

Write-Host ""
Write-Host "Verificando scripts..." -ForegroundColor Yellow
Get-ChildItem "scripts\*" -Include "*.ps1", "*.bat" | ForEach-Object {
    $result = Test-UTF8BOM $_.FullName
    Write-Host "$($_.Name) : $result"
}

Write-Host ""
Write-Host "Verificando documentação..." -ForegroundColor Yellow
Get-ChildItem "docs\*.md" | ForEach-Object {
    $result = Test-UTF8BOM $_.FullName
    Write-Host "$($_.Name) : $result"
}

Write-Host ""
Write-Host "Verificando alguns arquivos Java..." -ForegroundColor Yellow
$javaFiles = @(
    "src\main\java\com\newpix\server\gui\ServerGUI.java",
    "src\main\java\com\newpix\client\gui\LoginGUI.java",
    "src\main\java\com\newpix\client\gui\MainGUI.java"
)

foreach ($file in $javaFiles) {
    if (Test-Path $file) {
        $result = Test-UTF8BOM $file
        Write-Host "$(Split-Path $file -Leaf) : $result"
    }
}

Write-Host ""
Write-Host "Verificação concluída!" -ForegroundColor Green
Read-Host "Pressione Enter para continuar"
