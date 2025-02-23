package com.example.doancoso3.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import android.database.sqlite.SQLiteOpenHelper
import com.example.doancoso3.model.Product

class CopyDbHelper(private val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)  {
    companion object{

        private val DB_NAME="doancoso3.db"
        private const val TABLE_FAVORITES = "favorites"

        private const val DB_VERSION = 1

    }
    override fun onCreate(db: SQLiteDatabase) {
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    }
    fun openDatabase() : SQLiteDatabase{
        val dbFile= context.getDatabasePath(DB_NAME)
        val file = File(dbFile.toString())
        if (!file.exists()) {
            copyDatabase(dbFile)
        }
        return SQLiteDatabase.openDatabase(dbFile.path,null,SQLiteDatabase.OPEN_READWRITE)
    }
    private fun copyDatabase(dbFile: File) {
        val inputStream = context.assets.open(DB_NAME)
        val outputStream = FileOutputStream(dbFile)
        val buffer = ByteArray(1024)
        var length: Int
        while (inputStream.read(buffer).also { length = it } > 0) {
            outputStream.write(buffer, 0, length)
        }
        outputStream.flush()
        outputStream.close()
        inputStream.close()
    }
    fun getUserDb(): UserDb {
        val db = openDatabase()
        return UserDb(db)
    }
    fun getProductDb(): ProductDb {
        val db = openDatabase()
        return ProductDb(db)
    }
    fun getProductById(id: Int): Product? {
        return getProductDb().getProductById(id)
    }
    fun addFavorite(product: Product) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("UserId", 1) // Thay bằng UserId thực tế nếu có
            put("TenSP", product.TenSP)
            put("GiaTien", product.GiaTien)
            put("HinhAnh", product.HinhAnh)
        }
        db.insert(TABLE_FAVORITES, null, values)
        db.close()
    }
    fun removeFavorite(product: Product) {
        val db = writableDatabase
        db.delete(TABLE_FAVORITES, "TenSP = ?", arrayOf(product.TenSP))
        db.close()
    }

    fun getFavorites(): List<Product> { // ✅ Trả về List<Product>
        val db = readableDatabase
        val list = mutableListOf<Product>()
        val cursor = db.rawQuery("SELECT id, TenSP, GiaTien, HinhAnh FROM $TABLE_FAVORITES", null)

        if (cursor.moveToFirst()) {
            do {
                val product = Product(
                    ID = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    DanhMuc = "",
                    TenSP = cursor.getString(cursor.getColumnIndexOrThrow("TenSP")),
                    GiaTien = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaTien")),
                    HinhAnh = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh")),
                    HinhAnh1 = null,
                    HinhAnh2 = null,
                    HinhAnh3 = null,
                    MoTa = ""
                )
                list.add(product)  // ✅ Chỉ thêm Product, không phải CartItem
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return list
    }
}