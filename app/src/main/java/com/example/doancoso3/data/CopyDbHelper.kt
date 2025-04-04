package com.example.doancoso3.data
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.doancoso3.model.Product

class CopyDbHelper(private val context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION)  {
    companion object{

        private val DB_NAME="doancoso3.db"
        private const val TABLE_FAVORITES = "favorites"
        const val TABLE_CART = "Cart"
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
        try {
            val inputStream = context.assets.open(DB_NAME)
            val outputStream = FileOutputStream(dbFile)
            val buffer = ByteArray(1024)
            var length: Int
            var totalBytes = 0
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
                totalBytes += length
            }
            outputStream.flush()
            outputStream.close()
            inputStream.close()

            // Kiểm tra xem table Orders có tồn tại không
            val db = SQLiteDatabase.openDatabase(dbFile.path, null, SQLiteDatabase.OPEN_READONLY)
            db.close()
        } catch (e: Exception) {

        }
    }

    fun getProductDb(): ProductDb {
        val db = openDatabase()
        return ProductDb(db)
    }

    fun getOrderDb(): OrderDb {
        val db = openDatabase()
        return OrderDb(db)
    }
    fun getProductById(id: Int): Product? {
        return getProductDb().getProductById(id)
    }
    fun addFavorite(product: Product, userId: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("UserId", userId) // Thay bằng UserId thực tế nếu có
            put("TenSP", product.TenSP)
            put("GiaTien", product.GiaTien)
            put("HinhAnh", product.HinhAnh)
        }
        db.insert(TABLE_FAVORITES, null, values)
        db.close()
    }


    fun getFavorites(userId: Int): List<Product> {
        Log.d("getFavorites", "Hàm getFavorites được gọi với userId: $userId")

        val db = openDatabase()
        val list = mutableListOf<Product>()
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_FAVORITES WHERE UserId = ?",
            arrayOf(userId.toString())
        )

        Log.d("getFavorites", "Số dòng trong cursor: ${cursor.count}")

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
                list.add(product)
                Log.d("getFavorites", "Thêm sản phẩm: ID=${product.ID}, Tên=${product.TenSP}, Giá=${product.GiaTien}")
            } while (cursor.moveToNext())
        } else {
            Log.d("getFavorites", "Không tìm thấy sản phẩm yêu thích nào cho userId: $userId")
        }

        cursor.close()
        db.close()

        Log.d("getFavorites", "Tổng số sản phẩm yêu thích: ${list.size}")

        return list
    }

    fun getUserDb(context: Context): UserDb {
        val db = openDatabase()
        return UserDb(context, db)
    }
    fun userAddressDb(): UserAddressDb {
        val db = openDatabase()
        return UserAddressDb(db)
    }
    fun removeFavorite(product: Product, userId: Int) {
        val db = writableDatabase
        db.delete(TABLE_FAVORITES,  "TenSP = ? AND UserId = ?", arrayOf(product.TenSP, userId.toString()))
        db.close()
    }

}