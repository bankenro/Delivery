package pe.com.globaltics.delivery.Clases

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLite(context: Context, name: String, factory: SQLiteDatabase.CursorFactory?, version: Int) :

    SQLiteOpenHelper(context, name, factory, version) {
    private val databasew = writableDatabase
    private val databaser = readableDatabase

    fun queryData(sql: String) {
        databasew.execSQL(sql)
    }

    fun insertData(id_prod: Int, nombre_prod: String, precio_prod: String,
                   descrip_prod: String, cantidad: Int,imagen: String) {
        val sql = "INSERT INTO carrito VALUES (NULL,?,?,?,?,?,?)"
        val statement = databasew.compileStatement(sql)
        statement.clearBindings()

        statement.bindLong(1, id_prod.toLong())
        statement.bindString(2, nombre_prod)
        statement.bindString(3, precio_prod)
        statement.bindString(4, descrip_prod)
        statement.bindLong(5, cantidad.toLong())
        statement.bindString(6, imagen)
        statement.executeInsert()
    }

    internal fun deleteData(codigo: Int) {
        val sql = "DELETE FROM carrito WHERE id = ?"
        val statement = databasew.compileStatement(sql)
        statement.clearBindings()
        statement.bindLong(1, codigo.toLong())
        statement.executeUpdateDelete()
    }

    fun deleteAllData(){
        val sql = "DELETE FROM carrito"
        val statement = databasew.compileStatement(sql)
        statement.clearBindings()
        statement.executeUpdateDelete()
    }

    fun getData(sql: String): Cursor {
        return databaser.rawQuery(sql, null)
    }

    override fun onCreate(sqLiteDatabase: SQLiteDatabase) {

    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {

    }
}