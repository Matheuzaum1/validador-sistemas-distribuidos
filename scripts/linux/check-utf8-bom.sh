#!/bin/bash

echo "🔍 Verificando Codificação UTF-8 com BOM"
echo "========================================"

# Função para verificar se um arquivo tem BOM UTF-8
check_utf8_bom() {
    local file="$1"
    
    # Verificar se o arquivo tem BOM UTF-8 (EF BB BF)
    if file "$file" | grep -q "UTF-8 Unicode (with BOM)"; then
        echo "✅ $file - UTF-8 com BOM"
        return 0
    elif file "$file" | grep -q "UTF-8 Unicode text"; then
        echo "⚠️  $file - UTF-8 sem BOM"
        return 1
    else
        echo "❌ $file - Codificação desconhecida"
        return 2
    fi
}

# Ir para o diretório raiz do projeto
cd "$(dirname "$0")/../.."

echo "📋 Verificando arquivos de documentação (.md)..."
find . -name "*.md" -not -path "./target/*" -not -path "./.git/*" | while read -r file; do
    check_utf8_bom "$file"
done

echo
echo "📋 Verificando scripts PowerShell (.ps1)..."
find . -name "*.ps1" -not -path "./target/*" -not -path "./.git/*" | while read -r file; do
    check_utf8_bom "$file"
done

echo
echo "📋 Verificando scripts Batch (.bat)..."
find . -name "*.bat" -not -path "./target/*" -not -path "./.git/*" | while read -r file; do
    check_utf8_bom "$file"
done

echo
echo "📋 Verificando código Java (.java)..."
find . -name "*.java" -not -path "./target/*" -not -path "./.git/*" | while read -r file; do
    check_utf8_bom "$file"
done

echo
echo "📋 Verificando configurações (.xml, .properties)..."
find . \( -name "*.xml" -o -name "*.properties" \) -not -path "./target/*" -not -path "./.git/*" | while read -r file; do
    check_utf8_bom "$file"
done

echo
echo "🎯 Verificação concluída!"
echo
echo "💡 Dica para converter para UTF-8 com BOM:"
echo "   - Use um editor como VS Code, Notepad++ ou similar"
echo "   - Ou use o comando: "
echo "     sed -i '1s/^/\xEF\xBB\xBF/' arquivo.txt"
