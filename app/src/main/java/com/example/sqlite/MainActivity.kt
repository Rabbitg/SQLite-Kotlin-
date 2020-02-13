package com.example.sqlite

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sqlite.DBHelper.PostDBHelper
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.card.view.*

class MainActivity : AppCompatActivity() {

    // 데이터베이스에서 읽은 데이터를 맵의 리스트 형태로 가지고 있는 변수
    val dataList = mutableListOf<MutableMap<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // 목록 형태의 UI 이므로 RecyclerView 사용
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = MyAdapter()

        // 우측 하단 버튼이 클릭된 경우
        fab.setOnClickListener {
            // WriteDialog 를 보여준다
            val dialog = WriteDialog()
            // 다이얼로그 완료 리스너 설정
            dialog.listener = { title, post ->
                // 데이터베이스에 저장
                saveData(title,post)
                // RecyclerView 업데이트
                updateRecyclerView()
            }

            // 다이얼로그를 보여준다.
            dialog.show(supportFragmentManager,"dialog")
        }

        // 최초 실행 시에 RecyclerView 를 업데이트
        updateRecyclerView()
    }

    // RecyclerView 의 각 ViewHolder, 각 뷰를 멤버로 가지면 성능상 이득
    inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val titleTextView: TextView
        val postTextView: TextView
        val timeTextView: TextView
        val editButton: ImageButton
        val deleteButton: ImageButton

        init {
            titleTextView = itemView.titleTextView
            postTextView = itemView.postTextView
            timeTextView = itemView.timeTextView
            editButton = itemView.editButton
            deleteButton = itemView.deleteButton
        }
    }

    inner class MyAdapter : RecyclerView.Adapter<MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            return MyViewHolder(
                LayoutInflater.from(this@MainActivity).inflate(R.layout.card, parent, false)
            )
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.titleTextView.text = dataList[position].get("title").toString()
            holder.postTextView.text = dataList[position].get("post").toString()
            holder.timeTextView.text = dataList[position].get("time").toString()

            holder.deleteButton.setOnClickListener {
                removeData(dataList[position].get("id").toString())
                updateRecyclerView()
            }
            holder.editButton.setOnClickListener {
                val dialog = WriteDialog()
                dialog.title = dataList[position].get("title").toString()
                dialog.post = dataList[position].get("post").toString()
                dialog.listener = { title, post ->
                    // 여기서 데이터베이스에 저장
                    editData(dataList[position].get("id").toString(), title, post)
                    updateRecyclerView()
                }
                dialog.show(supportFragmentManager, "dialog")
            }
        }
    }
        // RecyclerView 를 데이터베이스의 데이터와 동기화하여 업데이트
        fun updateRecyclerView(){
            dataList.clear()
            dataList.addAll(readAllData())
            recyclerView.adapter?.notifyDataSetChanged()
        }

        // 데이터베이스에 데이터 저장
        fun saveData(title: String, post: String)
        {
            val sql = "INSERT INTO post (title,post) values('${title}', '${post}')"
            val dbHelper = PostDBHelper(applicationContext)
            dbHelper.writableDatabase.execSQL(sql)
        }

        // post 테이블의 모든 데이터를 읽고 맵의 리스트 형태로 반환
        fun readAllData(): MutableList<MutableMap<String,String>>{
            val dbHelper = PostDBHelper(applicationContext)
            val resultList = mutableListOf<MutableMap<String,String>>()
            val cursor = dbHelper.readableDatabase.rawQuery("SELECT * FROM post", null)
            if(cursor.moveToFirst()){
                do{
                    val map = mutableMapOf<String,String>()
                    map["id"] = cursor.getString(cursor.getColumnIndex("id"))
                    map["title"] = cursor.getString(cursor.getColumnIndex("title"))
                    map["post"] = cursor.getString(cursor.getColumnIndex("post"))
                    map["time"] = cursor.getString(cursor.getColumnIndex("time"))
                    resultList.add(map)

                } while (cursor.moveToNext())
            }
            return resultList
        }

        // 데이터 삭제
        fun removeData(id:String)
        {
            val dbHelper = PostDBHelper(applicationContext)
            val sql = "DELETE FROM post where id = ${id}"

            dbHelper.writableDatabase.execSQL(sql)
        }

        // 데이터 수정
        fun editData(id:String, title:String, post: String){
            val dbHelper = PostDBHelper(applicationContext)
            val sql = "UPDATE post set title = '${title}', post = '${post}', time = CURRENT_TIMESTAMP where id = ${id}"
            dbHelper.writableDatabase.execSQL(sql)
        }
    }

