# Cargar variables desde .env
Get-Content .env | ForEach-Object {
    if ($_ -match "^\s*#") { return } # Ignorar comentarios
    if ($_ -match "^\s*$") { return } # Ignorar líneas vacías

    $name, $value = $_ -split '=', 2
    Set-Item -Path "Env:$name" -Value $value
}

# Ejecutar app
.\mvnw spring-boot:run "-Dspring-boot.run.profiles=dev"