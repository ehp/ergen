package cz.aprar.oss.ergen

import java.io.Writer

data class TableColumn(
    val name: String,
    val type: String,
    val nullable: Boolean,
    val primaryKey: Boolean,
) {
    fun output(writer: Writer) {
        writer.write("\t")
        if (!nullable) {
            writer.write("* ")
        }
        writer.write("$name : $type\n")
    }
}

data class ForeignKey(
    val pkTable: String,
    val fkTable: String,
) {
    fun output(writer: Writer) {
        writer.write("$pkTable |o..o{ $fkTable\n")
    }
}

data class Database(
    val tables: Map<String, Set<TableColumn>>,
    val foreignKeys: Set<ForeignKey>,
) {
    fun output(writer: Writer) {
        writer.write("@startuml\n'https://plantuml.com/component-diagram\n\n")
        writer.write("skinparam linetype ortho\n\n")

        tables.entries
            .filter { it.value.isNotEmpty() }
            .sortedBy { it.key }
            .forEach { outputTable(writer, it.key, it.value) }

        foreignKeys.sortedBy { "${it.pkTable}/${it.fkTable}" }.forEach {
            it.output(writer)
        }

        writer.write("\n@enduml\n")
    }

    fun outputTable(writer: Writer, tableName: String, columns: Set<TableColumn>) {
        val (pk, rest) = columns.partition { it.primaryKey }

        writer.write("entity $tableName {\n")
        pk.sortedBy { it.name }.forEach {
            it.output(writer)
        }
        writer.write("\t--\n")
        rest.sortedBy { it.name }.forEach {
            it.output(writer)
        }
        writer.write("}\n\n")
    }
}
