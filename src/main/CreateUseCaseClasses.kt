package main

import java.io.File

fun createUseCaseFiles(path: String, featureName: String, columnList: List<DatabaseColumn>, methodTypes: MethodTypes) {

    if (methodTypes.shouldUpdate) {
        createUpdateUseCaseFile(path, featureName)
    }
    if (methodTypes.shouldReadAll) {
        createReadAllUseCaseFile(path, featureName)
    }
    if (methodTypes.shouldReadByPrimaryKeys) {
        createReadByPrimaryKeysUseCaseFile(path, featureName, columnList)
    }
}

fun createUpdateUseCaseFile(path: String, featureName: String) {
    val fileText = "import eu.fiveminutes.rosetta.domain.interactor.CompletableUseCaseWithParameter\n" +
            "\n" +
            "class Update${featureName}UseCase(" +
            "\n\tprivate val ${featureName.toLowerCase()}Repository : ${featureName}Repository" +
            "\n) : CompletableUseCaseWithParameter<$featureName> {\n\n" +
            "    override fun execute(${featureName.toLowerCase()}: $featureName) = " +
            "${featureName.toLowerCase()}Repository.update$featureName(${featureName.toLowerCase()})\n}"

    File("$path${File.separator}Update${featureName}UseCase.kt").writeText(fileText)
}

fun createReadAllUseCaseFile(path: String, featureName: String) {
    val fileText = "import eu.fiveminutes.rosetta.domain.interactor.SingleUseCase\n" +
            "\n" +
            "class Read${featureName}ListUseCase(" +
            "\n\tprivate val ${featureName.toLowerCase()}Repository : ${featureName}Repository" +
            "\n) : SingleUseCase<List<$featureName>> {\n\n" +
            "    override fun execute() = " +
            "${featureName.toLowerCase()}Repository.get${featureName}List()\n}"

    File("$path${File.separator}Read${featureName}ListUseCase.kt").writeText(fileText)
}

fun createReadByPrimaryKeysUseCaseFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {

    val filteredColumnList = columnList.filter { it.isPrimaryKey }
    var fileText = "import eu.fiveminutes.rosetta.domain.interactor.SingleUseCseWithParameter\n" +
            "import ReadStudentListByPrimaryKeysUseCase.Request\n\n" +
            "class Read${featureName}ListByPrimaryKeysUseCase(" +
            "\n\tprivate val ${featureName.toLowerCase()}Repository : ${featureName}Repository" +
            "\n) : SingleUseCaseWithParameter<Request, List<$featureName>> {\n\n" +
            "    override fun execute(request: Request) = " +
            "${featureName.toLowerCase()}Repository.get${featureName}ListByPrimaryKeys("
    filteredColumnList.forEachIndexed { index, it ->
        fileText += "request.${it.name}"
        if (index != filteredColumnList.size - 1) {
            fileText += ", "
        }
    }
    fileText += ")\n\n\tdata class Request("

    filteredColumnList.forEachIndexed { index, it ->
        fileText += "val ${it.name}: ${getTypeString(it.type).capitalize()}"
        if (index != filteredColumnList.size - 1) {
            fileText += ", "
        }
    }

    fileText += ")\n}"


    File("$path${File.separator}Read${featureName}ListByPrimaryKeysUseCase.kt").writeText(fileText)
}
