package main

import java.io.File

fun main(args: Array<String>) {
    createFile("C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\main\\Meho.txt",
            "C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\main", "Meho2")
}

fun createFile(fileToCopy: String, fileToCreate: String, featureName: String) {
    val packageStrings = fileToCreate.split("java")
    var packageName = ""
    if (packageStrings.size== 2){
        packageName = packageStrings[1].replace("\\",".").removePrefix(".")
    }
    var textToWrite = File(fileToCopy)
            .readText()
            .replace("{feature}", featureName)
    if (packageName.isNotEmpty()){
        textToWrite = textToWrite.replace("{package}", packageName)
    }

    File("$fileToCreate\\$featureName.java").writeText(textToWrite)
}