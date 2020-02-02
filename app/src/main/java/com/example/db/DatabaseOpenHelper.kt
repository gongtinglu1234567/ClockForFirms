package com.example.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

class DatabaseOpenHelper private constructor(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    init {
        instance = this
    }

    companion object {
        private var instance: DatabaseOpenHelper? = null

        @Synchronized
        fun getInstance(ctx: Context) = instance ?: DatabaseOpenHelper(ctx.applicationContext)
    }

    override fun onCreate(db: SQLiteDatabase) {
        // 在这里创建表
        //第一个参数是表名
        //第二个参数为true时，创建前会检查表是否存在
        //后面的参数是Pair类型的vararg，是表的列名和类型。（vararg在java中也有，是一种在函数中传入很多相同类型的参数）
        db.createTable(
            Device.TABLE_NAME, true,
            Device.DEVICE_ID to INTEGER + PRIMARY_KEY + UNIQUE,
            Device.DEVICE_REMAININF_TIME to INTEGER,
            Device.DEVICE_STATUS to INTEGER
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(Device.TABLE_NAME, true)
//        db.dropTable(PersonTable.TABLE_NAME, true)
        onCreate(db)
    }
}

// Context的访问属性
val Context.database: DatabaseOpenHelper
    get() = DatabaseOpenHelper.getInstance(this)
