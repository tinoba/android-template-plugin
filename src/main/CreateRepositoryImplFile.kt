package main

import java.io.File

fun createOrMergeRepositoryImplFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    when {
        path.endsWith(".kt") -> mergeKotlinRepositoryImplFile(path, featureName, columnList)
        path.endsWith(".java") -> mergeJavaRepositoryImplFile(path, featureName, columnList)
        else -> createRepositoryImplFile(path, featureName, columnList)
    }
}

fun createRepositoryImplFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var repositoryImplImplFileText = "import rx.Completable\n" +
            "import rx.Single\n\n" +
            "class ${featureName}RepositoryImpl(\n" +
            "\tprivate val ${featureName.toLowerCase()}Dao:  ${featureName}Dao\n) : ${featureName}Repository{\n\n" +
            "\toverride fun update$featureName(${featureName.toLowerCase()}: $featureName)" +
            " = ${featureName.toLowerCase()}Dao.update$featureName(${featureName.toLowerCase()})" +
            "\n\n\toverride fun get${featureName}ListByPrimaryKeys("
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    filteredColumnList.forEachIndexed { index, it ->
        repositoryImplImplFileText += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            repositoryImplImplFileText += ", "
        }
    }
    repositoryImplImplFileText += ") = " +
            "${featureName.toLowerCase()}Dao.get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        repositoryImplImplFileText += it.name
        if (index != filteredColumnList.size - 1) {
            repositoryImplImplFileText += ", "
        }
    }
    repositoryImplImplFileText += ")\n\n" +
            "\toverride fun get${featureName}List() = " +
            "${featureName.toLowerCase()}Dao.get${featureName}List()\n}"


    File("$path${File.separator}${featureName}RepositoryImpl.kt").writeText(repositoryImplImplFileText)
}

fun mergeJavaRepositoryImplFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val repositoryImplFile = File(path)
    val splittedPath = path.split(File.separator)
    val oldFeatureName = splittedPath[splittedPath.size - 1].split(".")[0]

    val repositoryImplLines =repositoryImplFile.readText()

    val preConstructorString = "\n\tprivate final ${featureName}Dao ${featureName.toLowerCase()}Dao;\n"

    val constructorAssigningString = "\t\tthis.${featureName.toLowerCase()}Dao = ${featureName.toLowerCase()}Dao;"

    val constructorString = "final ${featureName}Dao ${featureName.toLowerCase()}Dao,\n\t\t\t\t\t"

    var newMethodsDaoImplString = "\n\t@Override\n\t public Completable update$featureName($featureName ${featureName.toLowerCase()}){\n\t\t" +
            "return ${featureName}Dao.update$featureName(${featureName.toLowerCase()});\n\t}\n\n" +
            "\t@Override\n\t public Single<$featureName> get${featureName}ListByPrimaryKeys("
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += "${getTypeString(it.type).capitalize()} ${it.name}"
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += ") {\n\t\t" +
            "return ${featureName}Dao.get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += it.name
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += ");\n\t}\n\n" +
            "\t@Override\n\t public Single<$featureName> get${featureName}List() {\n" +
            "\t\treturn ${featureName}Dao.get${featureName}List();\n\t}\n"

    //mergeJavaFile(path, preConstructorString, constructorString, constructorAssigningString, newMethodsDaoImplString)

    var daoImplFileTextStringBuilder = StringBuilder(repositoryImplLines).insert(findIndexOfWordInString(repositoryImplLines, oldFeatureName.removeSuffix("Impl") + " {"), preConstructorString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfConstructorEnd(daoImplFileTextStringBuilder.toString())-1, constructorAssigningString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfWordInStringJava(daoImplFileTextStringBuilder.toString(), oldFeatureName), constructorString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(daoImplFileTextStringBuilder.length - 3, newMethodsDaoImplString)

    repositoryImplFile.writeText(daoImplFileTextStringBuilder.toString())
}

fun mergeKotlinRepositoryImplFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val daoImplFile = File(path)
    val splittedPath = path.split(File.separator)
    val oldFeatureName = splittedPath[splittedPath.size - 1].split(".")[0]

    var daoImplLines = daoImplFile.readText()

    val constructorString = "\n\tprivate val ${featureName.toLowerCase()}DbInsertHelper: DbInsertHelper<$featureName>," +
            "\n\tprivate val ${featureName.toLowerCase()}DbReadHelper: DbReadHelper<List<$featureName>>," +
            "\n\tprivate val ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper: DbReadHelper<List<$featureName>>,\n\t"

    var newMethodsDaoImplString = "\n\toverride fun update$featureName(${featureName.toLowerCase()}: $featureName)" +
            " = Completable.fromCallable { writeToDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbInsertHelper.insert(${featureName.toLowerCase()}, database) } }\n\n" +
            "\toverride fun get${featureName}ListByPrimaryKeys("
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += "): Single<List<$featureName>> =\n" +
            "\t\tSingle.fromCallable { readFromDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper.read(database, "
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += it.name
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += ") } }\n\n" +
            "\toverride fun get${featureName}List() : Single<List<$featureName>> =\n" +
            "\t\tSingle.fromCallable { readFromDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbReadHelper.read(database) } }\n"

    var daoImplFileTextStringBuilder = StringBuilder(daoImplLines).insert(findIndexOfWordInString(daoImplLines, oldFeatureName), constructorString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(daoImplFileTextStringBuilder.length - 1, newMethodsDaoImplString)

    daoImplFile.writeText(daoImplFileTextStringBuilder.toString())

    val daoFilePath = path.split(oldFeatureName)[0] + oldFeatureName.removeSuffix("Impl")
    val daoFile = File("$daoFilePath.kt")

    var daoFileTextStringBuilder = StringBuilder(daoFile.readText())

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

    daoFile.writeText(daoFileTextStringBuilder.toString())
}

fun main(args: Array<String>) {
    mergeJavaDaoFile("C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\nonmain\\TestDaoImpl.java",
            "Student", listOf())
}
