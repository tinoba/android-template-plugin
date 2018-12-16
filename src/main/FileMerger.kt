package main

import java.io.File

fun main(args: Array<String>) {
    // mergeFile("bok", "C:\\Users\\Tino\\IdeaProjects\\TemplateGenerator\\src\\main\\Meho.java")
}


fun mergeJavaFile(featureName:String,
                  path: String,
                  parameters: String,
                  constructorParameters: String,
                  assigningParameters: String,
                  methods: String,
                  columnList: List<DatabaseColumn>) {

    val preConstructorString = "\n\tprivate final ${featureName}DbInsertHelper<$featureName> ${featureName.toLowerCase()}DbInsertHelper;" +
            "\n\tprivate final ${featureName}DbReadHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadHelper;" +
            "\n\tprivate final ${featureName}DbReadByPrimaryKeysHelper<List<$featureName>> ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper;\n"

    val constructorAssigningString = "\n\tthis.${featureName.toLowerCase()}DbInsertHelper = ${featureName.toLowerCase()}DbInsertHelper;" +
            "\n\tthis.${featureName.toLowerCase()}DbReadHelper = ${featureName.toLowerCase()}DbReadHelper;" +
            "\n\tthis.${featureName.toLowerCase()}DbReadByPrimaryKeysHelper = ${featureName.toLowerCase()}DbReadByPrimaryKeysHelper;\n\t"

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

    val file = File(path)
    val splittedPath = path.split("\\")
    val oldFeatureName = splittedPath[splittedPath.size - 1].split(".")[0]

    val daoImplLines = file.readText()


    var daoImplFileTextStringBuilder = StringBuilder(daoImplLines).insert(findIndexOfWordInString(daoImplLines, oldFeatureName.removeSuffix("Impl") + " {"), parameters)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfConstructorEnd(daoImplFileTextStringBuilder.toString()), assigningParameters)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(findIndexOfWordInStringJava(daoImplFileTextStringBuilder.toString(), oldFeatureName), constructorParameters)
    daoImplFileTextStringBuilder = daoImplFileTextStringBuilder.insert(daoImplFileTextStringBuilder.length - 1, methods)

    file.writeText(daoImplFileTextStringBuilder.toString())
}
