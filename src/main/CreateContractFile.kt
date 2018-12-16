package main

import java.io.File

fun createContractFile(path: String, featureName: String, columnList: List<DatabaseColumn>) {

    var fileText = "import android.provider.BaseColumns;\n" +
            "\n" +
            "public final class ${featureName}DatabaseContract {\n" +
            "\n" +
            "    private static final String CREATE_TABLE = \"CREATE TABLE \";\n" +
            "    private static final String DROP_TABLE_IF_EXISTS = \"DROP TABLE IF EXISTS \";\n" +
            "    private static final String ALTER_TABLE = \"ALTER TABLE \";\n" +
            "    private static final String ADD_COLUMN = \" ADD COLUMN \";\n" +
            "    private static final String OPEN_PARENTHESIS = \"(\";\n" +
            "    private static final String CLOSED_PARENTHESIS = \")\";\n" +
            "\n" +
            "    public static final String TYPE_PRIMARY_KEY_AUTOINCREMENT = \" INTEGER PRIMARY KEY AUTOINCREMENT\";\n" +
            "\n" +
            "    private static final String SEPARATOR = \",\";\n" +
            "\n" +
            "    private static final String TYPE_TEXT = \" TEXT\";\n" +
            "    private static final String TYPE_INTEGER = \" INTEGER\";\n" +
            "    private static final String TYPE_REAL = \" REAL\";\n" +
            "    private static final String TYPE_NUMERIC = \" NUMERIC\";\n" +
            "    private static final String TYPE_BOOLEAN = \" TYPE_INTEGER\";\n" +
            "\n" +
            "    private static final String FOREIGN_KEY = \"FOREIGN KEY(\";\n" +
            "    private static final String REFERENCES = \") REFERENCES \";\n" +
            "\n" +
            "    private ${featureName}DatabaseContract() {\n" +
            "\n" +
            "    }\n" +
            "    public static final class ${featureName}Table implements BaseColumns {\n" +
            "\n" +
            "        public static final String TABLE_NAME = \"$featureName\";\n"

    columnList.forEach {
        fileText += "\t\tpublic static final String COLUMN_${it.name.toUpperCase()} = \"${it.name}\";\n"
        fileText += "\t\tpublic static final String COLUMN_${it.name.toUpperCase()}_TYPE = TYPE_${it.type};\n\n"
    }

    fileText += "\t\tpublic static final String SQL_CREATE_TABLE = \n" +
            "                        CREATE_TABLE + TABLE_NAME + \" (\" +\n" +
            "                        _ID + TYPE_PRIMARY_KEY_AUTOINCREMENT + SEPARATOR +\n"

    columnList.forEach {
        fileText += "                        COLUMN_${it.name.toUpperCase()} + COLUMN_${it.name.toUpperCase()}_TYPE +\n"
    }

    fileText += "                        \" );\";\n" +
            "\n" +
            "        public static final String SQL_DROP_TABLE =\n" +
            "                DROP_TABLE_IF_EXISTS + TABLE_NAME + \";\";\n" +
            "    }\n}"

    File("$path${File.separator}${featureName}DatabaseContract.java").writeText(fileText)
}