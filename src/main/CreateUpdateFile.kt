package main

import java.io.File

fun createUpdateFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var shouldGenerateBooleanConverted = false
    var fileText = "" +
            "import android.database.sqlite.SQLiteDatabase;\n" +
            "import android.database.sqlite.SQLiteStatement;\n" +
            "\n" +
            "import eu.fiveminutes.rosetta.data.db.definition.StoriesProgressDatabaseContract.StoryProgressTable;\n" +
            "import eu.fiveminutes.rosetta.domain.model.stories.StoryProgress;\n" +
            "\n" +
            "import static eu.fiveminutes.rosetta.data.db.DatabaseConstants.INSERT_OR_REPLACE_INTO;\n" +
            "\n" +
            "public final class ${featureName}DbUpdateHelper implements DbInsertHelper<$featureName> {\n" +
            "\n" +
            "    @Override\n" +
            "    public boolean insert(final $featureName item, final SQLiteDatabase database, final String... params) {\n" +
            "        if(item == null) {\n" +
            "            return false;\n" +
            "        }\n" +
            "        return update${featureName}Internal(item, database);\n" +
            "    }\n" +
            "\n" +
            "    private boolean update${featureName}Internal(final $featureName item, final SQLiteDatabase database) {\n" +
            "        try {\n" +
            "            final String sql = INSERT_OR_REPLACE_INTO + ${featureName}Table.TABLE_NAME + \" VALUES (?"
    for (i in 1 until columnList.size) {
        fileText += ",?"
    }
    fileText += ")\";\n" +
            "            final SQLiteStatement statement = database.compileStatement(sql);\n" +
            "\n" +
            "            statement.clearBindings();\n"
    columnList.forEachIndexed { index, it ->
        if (getTypeString(it.type) == "boolean") {
            fileText += "\t\t\tstatement.bindLong(${index.inc()}, booleanToLong(item.${it.name}()));\n"
            shouldGenerateBooleanConverted = true
        } else {
            fileText += "\t\t\tstatement.bind${getStatementType(it.type)}(${index.inc()}, item.${it.name}());\n"
        }
    }
    fileText += "\n" +
            "            statement.executeInsert();\n" +
            "        } catch (Exception ex) {\n" +
            "            ex.printStackTrace();\n" +
            "            return false;\n" +
            "        }\n" +
            "\n" +
            "        return true;\n" +
            "    }\n"
    if (shouldGenerateBooleanConverted) {
        fileText += "\n" +
                "    private long booleanToLong(final boolean flag) {\n" +
                "        return flag ? 1 : 0;\n" +
                "    }\n" +
                "}"
    } else {
        fileText += "}"
    }

    File("$path${File.separator}${featureName}DbUpdateHelper.java").writeText(fileText)
}

fun getStatementType(type: String): String {
    return when (type) {
        "TEXT" -> "String"
        "BOOLEAN" -> "Long"
        "INTEGER" -> "Long"
        "REAL" -> "Double"
        "NUMERIC" -> "Double"
        else -> ""
    }
}