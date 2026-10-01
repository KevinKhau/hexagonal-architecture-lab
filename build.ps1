# Compile tout le labo et lance les tests.
# Usage : ./build.ps1            (tests)
#         ./build.ps1 memory     (lance l'app en mode mémoire)
#         ./build.ps1 file       (lance l'app en mode fichier)
param([string]$Mode = "test")

$ErrorActionPreference = "Stop"
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $root

# Console en UTF-8 (sinon les ✔/✘ s'affichent en ? sous Windows)
chcp 65001 | Out-Null
$env:JAVA_TOOL_OPTIONS = "-Dfile.encoding=UTF-8"

New-Item -ItemType Directory -Force out | Out-Null

$sources = Get-ChildItem -Recurse -Filter *.java src | ForEach-Object { $_.FullName }
Write-Host "Compilation de $($sources.Count) fichiers..."
javac -encoding UTF-8 -d out $sources

Write-Host ""
switch ($Mode) {
    "memory" { java -cp out hexa.compose.CliRunner memory }
    "file"   { java -cp out hexa.compose.CliRunner file }
    default  {
        Write-Host "=== Tests ==="
        java -cp out hexa.test.TestRunner
    }
}
