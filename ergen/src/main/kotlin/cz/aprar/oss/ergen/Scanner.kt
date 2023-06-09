package cz.aprar.oss.ergen

import java.sql.Connection

class Scanner(val conn: Connection) {
    fun scan(): Database {
        val tableNames = scanTables()

        return Database(
            tableNames.associateWith { scanColumns(it) },
            tableNames.flatMap { scanForeignKeys(it) }.toSet()
        )
    }

    fun scanTables(): Set<String> =
        conn.metaData.getTables(null, "public", null, null).use { tableResultSet ->
            val data = mutableSetOf<String>()
            while (tableResultSet.next()) {
                data.add(tableResultSet.getString("TABLE_NAME"))
            }
            data
        }

    fun scanColumns(tableName: String): Set<TableColumn> {
        val primaryKeys = conn.metaData.getPrimaryKeys(null, null, tableName).use { pkResultSet ->
            val data = mutableSetOf<String>()
            while (pkResultSet.next()) {
                data.add(pkResultSet.getString("COLUMN_NAME"))
            }
            data
        }

        return conn.metaData.getColumns(null, null, tableName, null).use { columnResultSet ->
            val data = mutableSetOf<TableColumn>()
            while (columnResultSet.next()) {
                val columnName = columnResultSet.getString("COLUMN_NAME")
                data.add(TableColumn(
                    columnName,
                    columnResultSet.getString("TYPE_NAME"),
                    columnResultSet.getBoolean("NULLABLE"),
                    primaryKeys.contains(columnName)
                ))
            }
            data
        }
    }

    fun scanForeignKeys(tableName: String): Set<ForeignKey> =
        conn.metaData.getImportedKeys(null, null, tableName).use { fkResultSet ->
            val data = mutableSetOf<ForeignKey>()
            while (fkResultSet.next()) {
                data.add(ForeignKey(
                    fkResultSet.getString("PKTABLE_NAME"),
                    fkResultSet.getString("FKTABLE_NAME")
                ))
            }
            data
        }
}
