#!/bin/bash

JAVAFX=/usr/share/openjfx/lib
MODULES=javafx.base,javafx.controls,javafx.fxml,javafx.graphics,javafx.media,javafx.swing,javafx.web

mkdir -p out
javac --module-path $JAVAFX --add-modules=$MODULES -d out $(find src -name "*.java")
java  --module-path $JAVAFX --add-modules=$MODULES -cp out Main
find . -name "*.class" -type f -delete
