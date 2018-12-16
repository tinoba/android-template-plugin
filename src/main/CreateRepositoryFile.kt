package main

import java.io.File

fun createOrMergeRepositoryFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    when {
        path.endsWith(".kt") -> mergeKotlinRepositoryFile(path, featureName, columnList)
        path.endsWith(".java") -> mergeJavaRepositoryFile(path, featureName, columnList)
        else -> createRepositoryFile(path, featureName, columnList)
    }
}

fun mergeJavaRepositoryFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val repositoryFile = File(path)
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    var repositoryFileTextStringBuilder = StringBuilder(repositoryFile.readText())

    var newMethodsRepositoryString = "\n\n    Completable update$featureName($featureName ${featureName.toLowerCase()});\n" +
            "\n" +
            "    Single<List<$featureName>> get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsRepositoryString += "${getTypeString(it.type).capitalize()} ${it.name}"
        if (index != filteredColumnList.size - 1) {
            newMethodsRepositoryString += ", "
        }
    }
    newMethodsRepositoryString += ");\n" +
            "\n" +
            "    Single<List<$featureName>> get${featureName}List();"

    repositoryFileTextStringBuilder = repositoryFileTextStringBuilder.insert(repositoryFileTextStringBuilder.length - 2, newMethodsRepositoryString)

    println(repositoryFileTextStringBuilder.toString())
    repositoryFile.writeText(repositoryFileTextStringBuilder.toString())
}

fun mergeKotlinRepositoryFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val file = File(path)
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    var daoFileTextStringBuilder = StringBuilder(file.readText())

    var newMethodsDaoString = "\n    fun update$featureName(${featureName.toLowerCase()}: $featureName): Completable\n" +
            "\n" +
            "    fun get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoString += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoString += ", "
        }
    }
    newMethodsDaoString += "): Single<List<$featureName>>\n" +
            "\n" +
            "    fun get${featureName}List(): Single<List<$featureName>>\n"

    daoFileTextStringBuilder = daoFileTextStringBuilder.insert(daoFileTextStringBuilder.length - 1, newMethodsDaoString)

    println(daoFileTextStringBuilder.toString())
    file.writeText(daoFileTextStringBuilder.toString())
}

fun createRepositoryFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    var fileText = "import rx.Completable\n" +
            "import rx.Single\n" +
            "\n" +
            "interface ${featureName}Repository {\n\n" +
            "    fun update$featureName(${featureName.toLowerCase()}: $featureName): Completable\n" +
            "\n" +
            "    fun get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        fileText += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            fileText += ", "
        }
    }
    fileText += "): Single<List<$featureName>>\n" +
            "\n" +
            "    fun get${featureName}List(): Single<List<$featureName>>\n}"

    File("$path${File.separator}${featureName}Repository.kt").writeText(fileText)
}
