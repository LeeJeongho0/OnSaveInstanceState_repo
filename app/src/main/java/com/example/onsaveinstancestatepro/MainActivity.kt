package com.example.onsaveinstancestatepro

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.onsaveinstancestatepro.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    lateinit var dataList:MutableList<String>
    lateinit var myAdapter: MyAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. 번들에 데이타가 있으면 가져와서 기존객체참조변수에 저장하고, 없으면 무시한다.
        if(savedInstanceState != null){
            dataList = savedInstanceState.getStringArrayList("dataList")!!.toMutableList()
        } else {
            dataList = mutableListOf<String>()
        }


        //2. 인텐트를 돌려받을
        val activityResultLauncher : ActivityResultLauncher<Intent> = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            var result = it.data?.getStringExtra("result")
            if(result != null && !result.equals("")){
                dataList.add(result)
            }
            myAdapter.notifyDataSetChanged()
        }

        // 3. MyAdapter와 리사이클러뷰 연결,보여줄 리스트 모양결정
        val layoutManager = LinearLayoutManager(this)
        binding.mainRecyclerView.layoutManager = layoutManager
        myAdapter = MyAdapter(dataList)
        binding.mainRecyclerView.adapter = myAdapter
        binding.mainRecyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))


        // 4. floating tab 클릭하면 인텐트 요청
        binding.mainFab.setOnClickListener {
            val intent = Intent(this, AddActivity::class.java)
            activityResultLauncher.launch(intent)
        }

        // 5. sharedPreference 저장하는 방법
        binding.btnShared.setOnClickListener {
            val sharedPreference = getSharedPreferences("dataList", Context.MODE_PRIVATE)
            val editor = sharedPreference!!.edit()
            val gson = Gson()
            val json : String = gson.toJson(dataList)
            editor.putString("oneMessage",json)
            editor.commit()
            Toast.makeText(this,"sharedPreference 저장",Toast.LENGTH_SHORT).show()
        }
        // 6. sharedPreference 저장한 데이터 불러오는 방법
        binding.btnRevert.setOnClickListener {
            var sharedPreferences = getSharedPreferences("dataList", Context.MODE_PRIVATE)
            val data = sharedPreferences.getString("oneMessage",null)
            val type: Type = object : TypeToken<java.util.ArrayList<String>?>() {}.type
            val gson = Gson()
            dataList = gson.fromJson<Any>(data, type) as ArrayList<String>

            val layoutManager = LinearLayoutManager(this)
            myAdapter = MyAdapter(dataList)
            binding.mainRecyclerView.layoutManager = layoutManager
            binding.mainRecyclerView.adapter = myAdapter
            binding.mainRecyclerView.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))

            Toast.makeText(this,"sharedPreference 복구",Toast.LENGTH_SHORT).show()
        }
    }


    // 7. 화면을 회전할 경우에 기존에 내용을 번들에다 저장한다.
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("dataList",ArrayList(dataList))
    }
}