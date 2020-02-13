package com.example.sqlite.DBHelper

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

// SQLite 를 사용하기 위한 Helper 클래스
class PostDBHelper(context:Context): SQLiteOpenHelper(context,
    DATABASE_NAME, null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val createSql =
            CREATE_SQL_VER3
        db?.execSQL(createSql)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // onUpgrade() 함수의 존재의의는 데이터베이스의 테이블 구조를 점진적으로 업데이트 하기 위함
        // 과거 버전이 무엇인가에 따라 점진적으로 테이블을 업데이트
        when(oldVersion)
        {
            1-> db?.execSQL("ALTER TABLE post ADD COLUMN post TEXT")
            in 1..2 -> db?.execSQL("ALTER TABLE post ADD COLUMN time TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
        }
    }

    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "post.db"

        // 테이블 생성 쿼리를 버전별로 가지고 있다. 현재 버전은 CREATE_SQL_VER3
        const val CREATE_SQL_VER1 = "CREATE TABLE post(" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT)"
        const val CREATE_SQL_VER2 = "CREATE TABLE post(" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT," +
                "post TEXT)"
        const val CREATE_SQL_VER3 = "CREATE TABLE post(" +
                "id INTEGER PRIMARY KEY," +
                "title TEXT," +
                "post TEXT," +
                "time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)"
    }
}