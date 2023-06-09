package cz.aprar.oss.ergen

import java.io.OutputStreamWriter
import java.io.Writer
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class ERGen {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            // Configure database connection
            val url = "jdbc:postgresql://localhost/database"
            val props = Properties()
            props.setProperty("user", "user")
            props.setProperty("password", "password")

            // Configure output
            val writer: Writer = OutputStreamWriter(System.err)

            // End of configuration

            val conn: Connection = DriverManager.getConnection(url, props)
            writer.use {
                Scanner(conn).scan().output(it)
            }
        }
    }
}
