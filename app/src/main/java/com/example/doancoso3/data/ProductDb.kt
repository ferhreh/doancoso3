package com.example.doancoso3.data

import android.database.sqlite.SQLiteDatabase
import com.example.doancoso3.model.Product

class ProductDb(private val db: SQLiteDatabase) {

    fun getProducts(): List<Product> {
        val products = mutableListOf<Product>()
        val cursor = db.rawQuery("SELECT * FROM Product", null)

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex("ID")
                val danhMucIndex = cursor.getColumnIndex("DanhMuc")
                val tenSPIndex = cursor.getColumnIndex("TenSP")
                val moTaIndex = cursor.getColumnIndex("MoTa")
                val imageNameIndex = cursor.getColumnIndex("HinhAnh")
                val imageNameIndex1 = cursor.getColumnIndex("HinhAnh1")
                val imageNameIndex2 = cursor.getColumnIndex("HinhAnh2")
                val imageNameIndex3 = cursor.getColumnIndex("HinhAnh3")
                val giaTienIndex = cursor.getColumnIndex("GiaTien")

                if (idIndex != -1 && danhMucIndex != -1 && tenSPIndex != -1 && moTaIndex != -1 &&
                    imageNameIndex != -1 && imageNameIndex1 != -1 && imageNameIndex2 != -1 && imageNameIndex3 != -1 &&
                    giaTienIndex != -1) {

                    val id = cursor.getInt(idIndex)
                    val danhMuc = cursor.getString(danhMucIndex)
                    val tenSP = cursor.getString(tenSPIndex)
                    val moTa = cursor.getString(moTaIndex)
                    val imageName = cursor.getString(imageNameIndex)
                    val imageName1 = cursor.getString(imageNameIndex1) ?: "1" // Gán "1" nếu null
                    val imageName2 = cursor.getString(imageNameIndex2) ?: "1" // Gán "1" nếu null
                    val imageName3 = cursor.getString(imageNameIndex3) ?: "1" // Gán "1" nếu null
                    val giaTien = cursor.getDouble(giaTienIndex)

                    products.add(Product(id, danhMuc, tenSP, moTa, imageName, imageName1, imageName2, imageName3, giaTien))
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
        return products
    }
    fun getProductById(productId: Int): Product? {
        val cursor = db.rawQuery("SELECT * FROM Product WHERE ID = ?", arrayOf(productId.toString()))

        return if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("ID"))
            val danhMuc = cursor.getString(cursor.getColumnIndexOrThrow("DanhMuc"))
            val tenSP = cursor.getString(cursor.getColumnIndexOrThrow("TenSP"))
            val moTa = cursor.getString(cursor.getColumnIndexOrThrow("MoTa"))
            val imageName = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh"))
            val imageName1 = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh1")) ?: "1"
            val imageName2 = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh2")) ?: "1"
            val imageName3 = cursor.getString(cursor.getColumnIndexOrThrow("HinhAnh3")) ?: "1"
            val giaTien = cursor.getDouble(cursor.getColumnIndexOrThrow("GiaTien"))

            cursor.close()
            Product(id, danhMuc, tenSP, moTa, imageName, imageName1, imageName2, imageName3, giaTien)
        } else {
            cursor.close()
            null
        }
    }

}