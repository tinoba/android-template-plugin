package main

import main.Generator.ColumnUI
import java.io.File

fun createModelFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var fileText = "public final class $featureName{\n\n\t"

    columnList.forEach {
        fileText += "public final ${getTypeString(it.type)} ${it.name};\n\t"
    }

    fileText += "\n\tpublic $featureName("

    columnList.forEachIndexed { index, databaseColumn ->
        fileText += "final ${getTypeString(databaseColumn.type)} ${databaseColumn.name}"
        if (columnList.size != index + 1) {
            fileText += ","
        }
    }
    fileText += "){\n\t"
    columnList.forEach {
        fileText += "\tthis.${it.name} = ${it.name};\n\t"
    }
    fileText += "}\n}"

    File("$path${File.separator}$featureName.java").writeText(fileText)
}

fun getDatabaseColumnList(columnUiList : List<ColumnUI>): List<DatabaseColumn> {
    return columnUiList.map { it ->
        DatabaseColumn(
                it.nameTextField.text,
                it.typeComboBox.selectedItem as String,
                it.primaryKeyCheckBox.isSelected
        )
    }
}

fun getTypeString(type: String): String {
    return when (type) {
        "TEXT" -> "String"
        "BOOLEAN" -> "boolean"
        "INTEGER" -> "int"
        "REAL" -> "double"
        "NUMERIC" -> "double"
        else -> ""
    }
}

fun main(args: Array<String>) {
    createModelFile("C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\main",
            "Student", listOf(DatabaseColumn("ime", "TEXT", false),
            DatabaseColumn("prezime","TEXT",false)))
}