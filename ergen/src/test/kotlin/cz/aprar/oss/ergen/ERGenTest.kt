package cz.aprar.oss.ergen

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.io.StringWriter

class ERGenTest {
    @Test
    fun `database object generates correct output`() {
        val database = Database(
            mapOf(
                "table1" to setOf(
                    TableColumn("col1", "text", true, false),
                    TableColumn("col4", "int", false, true),
                    TableColumn("col2", "uuid", true, false),
                    TableColumn("col3", "varchar", false, true),
                ),
                "table3" to setOf(
                    TableColumn("colA", "text", true, false),
                    TableColumn("colB", "int", false, true),
                    TableColumn("colC", "uuid", true, false),
                    TableColumn("colD", "varchar", false, true),
                ),
                "table2" to setOf(
                    TableColumn("col1", "text", true, false),
                    TableColumn("col4", "int", false, true),
                    TableColumn("col2", "uuid", true, false),
                    TableColumn("col3", "varchar", false, true),
                ),
            ),
            setOf(
                ForeignKey("table2", "table3"),
                ForeignKey("table1", "table3"),
            )
        )

        val writer = StringWriter()
        database.output(writer)

        val expected = """@startuml
'https://plantuml.com/component-diagram

skinparam linetype ortho

entity table1 {
\t* col3 : varchar
\t* col4 : int
\t--
\tcol1 : text
\tcol2 : uuid
}

entity table2 {
\t* col3 : varchar
\t* col4 : int
\t--
\tcol1 : text
\tcol2 : uuid
}

entity table3 {
\t* colB : int
\t* colD : varchar
\t--
\tcolA : text
\tcolC : uuid
}

table1 |o..o{ table3
table2 |o..o{ table3

@enduml

""".trimIndent().replace("\\t", "\t")

        assertEquals(expected, writer.toString())
    }
}
