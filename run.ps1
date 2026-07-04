# Clean output folder
Remove-Item -Recurse -Force out -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path out | Out-Null

$javafxPath = ".\javafx-sdk-26.0.1\lib"

Write-Host "Compiling project..."

javac --module-path $javafxPath `
      --add-modules javafx.controls,javafx.fxml,javafx.media `
      -d out `
      (Get-ChildItem -Recurse -Filter *.java src/main/java | ForEach-Object { $_.FullName })

Write-Host "Copying resources..."

Copy-Item -Recurse -Force src/main/resources/* out/

Write-Host "Running application..."

java --module-path $javafxPath `
     --add-modules javafx.controls,javafx.fxml,javafx.media `
     --enable-native-access=javafx.media,javafx.graphics `
     -cp out Main