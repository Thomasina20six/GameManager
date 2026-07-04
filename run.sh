#!/bin/bash

set -e

JAVAFX_PATH="javafx-sdk-26/lib"

echo "Cleaning output..."
rm -rf out
mkdir -p out

echo "Compiling project..."

javac --module-path $JAVAFX_PATH \
      --add-modules javafx.controls,javafx.fxml,javafx.media \
      -d out \
      $(find src/main/java -name "*.java")

echo "Copying resources..."

cp -r src/main/resources/* out/

echo "Running application..."

java --module-path $JAVAFX_PATH \
     --add-modules javafx.controls,javafx.fxml,javafx.media \
     --enable-native-access=javafx.media,javafx.graphics \
     -cp out Main