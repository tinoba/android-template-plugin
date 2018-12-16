package main

import java.io.File

fun createOrMergeDaoFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    when {
        path.endsWith(".kt") -> mergeKotlinDaoFile(path, featureName, columnList)
        path.endsWith(".java") -> mergeJavaDaoFile(path, featureName, columnList)
        else -> createDaoFile(path, featureName, columnList)
    }
}

fun createDaoFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var daoImplFileText = "import eu.fiveminutes.rosetta.data.db.BaseRosettaDao\n" +
            "import eu.fiveminutes.rosetta.data.db.helper.RosettaDatabaseHelper\n" +
            "import rx.Single\n" +
            "import rx.Completable\n" +
            "import eu.fiveminutes.rosetta.data.utils.CursorUtils\n\n" +
            "class ${featureName}DaoImpl(\n" +
            "        databaseHelper: RosettaDatabaseHelper,\n" +
            "        cursorUtils: CursorUtils,\n" +
            "        private val ${featureName.toLowerCase()}DbInsertHelper: DbInsertHelper<$featureName>,\n" +
            "        private val ${featureName.toLowerCase()}DbReadHelper: DbReadHelper<List<$featureName>>,\n" +
            "        private val ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper: DbReadHelper<List<$featureName>>\n" +
            ") : BaseRosettaDao(databaseHelper, cursorUtils), ${featureName}Dao{\n\n" +
            "\toverride fun update$featureName(${featureName.toLowerCase()}: $featureName)" +
            " = Completable.fromCallable { writeToDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbInsertHelper.insert(${featureName.toLowerCase()}, database) } }\n\n" +
            "\toverride fun get${featureName}ListByPrimaryKeys("
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    filteredColumnList.forEachIndexed { index, it ->
        daoImplFileText += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            daoImplFileText += ", "
        }
    }
    daoImplFileText += "): Single<List<$featureName>> =\n" +
            "\t\tSingle.fromCallable { readFromDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper.read(database, "
    filteredColumnList.forEachIndexed { index, it ->
        daoImplFileText += it.name
        if (index != filteredColumnList.size - 1) {
            daoImplFileText += ", "
        }
    }
    daoImplFileText += ") } }\n\n" +
            "\toverride fun get${featureName}List() : Single<List<$featureName>> =\n" +
            "\t\tSingle.fromCallable { readFromDatabase(databaseHelper) { " +
            "database -> ${featureName.toLowerCase()}DbReadHelper.read(database) } }\n}"

    var daoFileText = "import rx.Completable\n" +
            "import rx.Single\n" +
            "\n" +
            "interface ${featureName}Dao {\n\n" +
            "    fun update$featureName(${featureName.toLowerCase()}: $featureName): Completable\n" +
            "\n" +
            "    fun get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        daoFileText += "${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            daoFileText += ", "
        }
    }
    daoFileText += "): Single<List<$featureName>>\n" +
            "\n" +
            "    fun get${featureName}List(): Single<List<$featureName>>\n}"

    File("$path${File.separator}${featureName}DaoImpl.kt").writeText(daoImplFileText)
    File("$path${File.separator}${featureName}Dao.kt").writeText(daoFileText)

}

fun mergeJavaDaoFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    val daoImplFile = File(path)
    val splittedPath = path.split(File.separator)
    val oldFeatureName = splittedPath[splittedPath.size - 1].split(".")[0]

    val daoImplLines = daoImplFile.readText()

    val preConstructorString = "\n\tprivate final ${featureName}DbInsertHelper<$featureName> ${featureName.toLowerCase()}DbInsertHelper;" +
            "\n\tprivate final ${featureName}DbReadHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadHelper;" +
            "\n\tprivate final ${featureName}DbReadByPrimaryKeysHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper;\n"

    val constructorAssigningString = "\n\tthis.${featureName.toLowerCase()}DbInsertHelper = ${featureName.toLowerCase()}DbInsertHelper;" +
            "\n\tthis.${featureName.toLowerCase()}DbReadHelper = ${featureName.toLowerCase()}DbReadHelper;" +
            "\n\tthis.${featureName.toLowerCase()}DbReadByPrimaryKeysHelper = ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper;"

    val constructorString = "final ${featureName}DbInsertHelper<$featureName> ${featureName.toLowerCase()}DbInsertHelper," +
            "\n\t\t\t\tfinal ${featureName}DbReadHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadHelper," +
            "\n\t\t\t\tfinal ${featureName}DbReadByPrimaryKeysHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper,\n\t\t\t\t"

    var newMethodsDaoImplString = "\n\t@Override\n\t public Completable update$featureName($featureName ${featureName.toLowerCase()}){\n\t\t" +
            "return Completable.fromCallable(() -> writeToDatabase(databaseHelper, " +
            "database -> ${featureName.toLowerCase()}DbInsertHelper.insert(${featureName.toLowerCase()}, database)));\n\t}\n\n" +
            "\t@Override\n\t public Single<$featureName> get${featureName}ListByPrimaryKeys("
    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += "${getTypeString(it.type).capitalize()} ${it.name}"
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += ") {\n\t\t" +
            "return Single.fromCallable(() -> readFromDatabase(databaseHelper, " +
            "database -> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper.read(database, "
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoImplString += it.name
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoImplString += ", "
        }
    }
    newMethodsDaoImplString += ")));\n\t}\n\n" +
            "\t@Override\n\t public Single<$featureName> get${featureName}List() {\n" +
            "\t\treturn Single.fromCallable(() -> readFromDatabase(databaseHelper, " +
            "database -> ${featureName.toLowerCase()}DbReadHelper.read(database)));\n\t}\n"

    //mergeJavaFile(path, preConstructorString, constructorString, constructorAssigningString, newMethodsDaoImplString)

    var daoImplFileTextStringBuilder = StringBuilder(daoImplLines).insert(findIndexOfWordInString(daoImplLines, oldFeatureName.removeSuffix("Impl") + " {"), preConstructorString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfConstructorEnd(daoImplFileTextStringBuilder.toString())-1, constructorAssigningString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfWordInStringJava(daoImplFileTextStringBuilder.toString(), oldFeatureName), constructorString)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(daoImplFileTextStringBuilder.length - 1, newMethodsDaoImplString)

    daoImplFile.writeText(daoImplFileTextStringBuilder.toString())

    val daoFilePath = path.split(oldFeatureName)[0] + oldFeatureName.removeSuffix("Impl")
    val daoFile = File("$daoFilePath.java")

    var daoFileTextStringBuilder = StringBuilder(daoFile.readText())

    var newMethodsDaoString = "\n    Completable update$featureName($featureName ${featureName.toLowerCase()});\n" +
            "\n" +
            "    Single<List<$featureName>> get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        newMethodsDaoString += "${getTypeString(it.type).capitalize()} ${it.name}"
        if (index != filteredColumnList.size - 1) {
            newMethodsDaoString += ", "
        }
    }
    newMethodsDaoString += ");\n" +
            "\n" +
            "    Single<List<$featureName>> get${featureName}List();\n"

    daoFileTextStringBuilder = daoFileTextStringBuilder.insert(daoFileTextStringBuilder.length - 1, newMethodsDaoString)

    println(daoFileTextStringBuilder.toString())
    daoFile.writeText(daoFileTextStringBuilder.toString())
}

fun mergeKotlinDaoFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
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

fun findIndexOfConstructorEnd(string: String): Int {
    var i = 0
    var count = 0
    for (c in string) {
        if (c == '{') {
            if (count == 0) {
                count++
            } else {
                return i + 3
            }
        }
        i++
    }

    return 0
}

fun findIndexOfWordInString(string: String, word: String): Int {
    var i = 0
    var currentWordPosition = 0
    for (c in string) {
        if (c == word[currentWordPosition]) {
            currentWordPosition++
        } else {
            currentWordPosition = 0
        }
        if (currentWordPosition == word.length - 1) {
            return i + 3
        }
        i++
    }

    return 0
}

fun findIndexOfWordInStringJava(string: String, word: String): Int {
    var i = 0
    var count = 0
    var currentWordPosition = 0
    for (c in string) {
        if (c == word[currentWordPosition]) {
            currentWordPosition++
        } else {
            currentWordPosition = 0
        }
        if (currentWordPosition == word.length - 1) {
            if (count == 0) {
                count++
                currentWordPosition = 0
            } else {
                return i + 3
            }
        }
        i++
    }

    return 0
}

fun main(args: Array<String>) {
    mergeJavaDaoFile("C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\nonmain\\TestDaoImpl.java",
            "Student", listOf())
}
