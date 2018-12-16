package main

import java.io.File

fun createReadByPrimaryKeysFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {
    var fileText = "import java.util.List;\n\n" +
            "public final class ${featureName}DbReadByPrimaryKeysHelper implements DbReadHelper<List<$featureName>> {" +
            "\n\n\tprivate final CursorUtils cursorUtils;\n" +
            "\n\tpublic ${featureName}DbReadByPrimaryKeysHelper(final CursorUtils cursorUtils) {\n" +
            "\t\tthis.cursorUtils = cursorUtils;\n" +
            "\t}" +
            "\n\n\t@Override" +
            "\n\tpublic List<$featureName> read(final SQLiteDatabase database, final String... params) {"
    columnList.filter { it.isPrimaryKey }
            .forEachIndexed { index, it ->
                fileText+= "\n\t\tfinal String ${it.name} = params[$index];"
            }
    fileText += "\n\t\treturn get${featureName}List(database"
    columnList.filter { it.isPrimaryKey }
            .forEach {
                fileText+= ", ${it.name}"
            }
    fileText+=");\n\t}\n\n" +
            "    private List<$featureName> get${featureName}List(final SQLiteDatabase database"
    columnList.filter { it.isPrimaryKey }
            .forEach {
                fileText += ", final String ${it.name}"
            }
    fileText += ") {\n" +
            "        final Cursor cursor = cursorUtils.getCursorForTableAndKeys(database, ${featureName}Table.TABLE_NAME"
    columnList.filter { it.isPrimaryKey }
            .forEach {
                fileText += " ,${featureName}Table.COLUMN_${it.name.toUpperCase()}, ${it.name}"
            }

    fileText += ");\n" +
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

    fileText += "\n\t\treturn new $featureName("

    columnList.forEachIndexed { index, databaseColumn ->
        fileText += databaseColumn.name
        if (index != columnList.size - 1) {
            fileText += ", "
        }
    }

    fileText += ");\n\t}\n}"

    File("$path${File.separator}${featureName}DbReadByPrimaryKeysHelper.java").writeText(fileText)
}