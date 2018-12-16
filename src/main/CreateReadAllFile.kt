package main

import java.io.File

fun createReadAllFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var fileText = "import java.util.List;\n\n" +
            "public final class ${featureName}DbReadHelper implements DbReadHelper<List<$featureName>> {" +
            "\n\n\tprivate final CursorUtils cursorUtils;\n" +
            "\n\tpublic ${featureName}DbReadHelper(final CursorUtils cursorUtils) {\n" +
            "\t\tthis.cursorUtils = cursorUtils;\n" +
            "\t}" +
            "\n\n\t@Override" +
            "\n\tpublic List<$featureName> read(final SQLiteDatabase database, final String... params) {" +
            "\n\t\treturn get${featureName}List(database);\n\t}\n\n" +
            "    private List<$featureName> get${featureName}List(final SQLiteDatabase database) {\n" +
            "        final Cursor cursor = cursorUtils.getCursorForTable(database, ${featureName}Table.TABLE_NAME);\n" +
            "    \n" +
            "        final List<$featureName> ${featureName.toLowerCase()}List = new ArrayList<>();\n" +
            "        while (cursor != null && cursor.moveToNext()) {\n" +
            "            final $featureName ${featureName.toLowerCase()} = get$featureName(cursor);\n" +
            "            ${featureName.toLowerCase()}List.add(${featureName.toLowerCase()});\n" +
            "        }\n" +
            "        cursorUtils.closeCursor(cursor);\n" +
            "        return ${featureName.toLowerCase()}List;\n" +
            "    }\n\n" +
            "    private $featureName get$featureName(final Cursor cursor) {\n"
    columnList.forEach {
        fileText += "\t\tfinal ${getTypeString(it.type)} ${it.name} = " +
                "cursorUtils.get${getTypeString(it.type).capitalize()}OrDefault(cursor, " +
                "${featureName}Table.COLUMN_${it.name.toUpperCase()}, ${getTypeDefaultValue(it.type)});\n"
    }

    fileText+="\n\t\treturn new $featureName("

    columnList.forEachIndexed { index, databaseColumn ->
        fileText+=databaseColumn.name
        if (index != columnList.size-1) {
            fileText+=", "
        }
    }

    fileText+=");\n\t}\n}"

    File("$path${File.separator}${featureName}DbReadHelper.java").writeText(fileText)
}

fun getTypeDefaultValue(type: String): String {
    return when (type) {
        "TEXT" -> "\"\""
        "BOOLEAN" -> "false"
        "INTEGER" -> "0"
        "REAL" -> "0.0"
        "NUMERIC" -> "0.0"
        else -> "\"\""
    }
}

