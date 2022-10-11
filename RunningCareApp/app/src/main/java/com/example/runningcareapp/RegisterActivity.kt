package com.example.runningcareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btn_back:ImageButton = findViewById(R.id.register_btn_back)
        btn_back.setOnClickListener {
            Toast.makeText(this, "뒤로가기버튼 눌림", Toast.LENGTH_LONG).show()
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        val btn_register:AppCompatButton = findViewById(R.id.register_ok)
        btn_register.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }



    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //현재 포커스 되어있는 view
        //focus가 없다면 null return
        val view = currentFocus

        //action_down: 화면 눌렀을 때, action_up: 화면에서 손가락을 뗄 때, action_move: 화면을 누르고 움직일 때
        //현재 focus값이 있고, 터치가 올라오거나, 내려갔을 때
        if(view != null
            && (ev!!.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && view is EditText
        ){
            // 현재 editText가 있는 위치를 구한다.
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)

            // 터치의 위치와, view의 상대적 위치, view의 위치를 조합
            // original의 위치 + view의 상대적 마진값 - view의 시작위치 --> 내부인지 외부인지 판별 가능
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]

            // 현재 사용자가 터치하고 있는 곳이 view의 내부인지 외부인지 판별
            // 현재 터치한 곳이 view의 외부라면 키보드를 내린다.
            if(x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom()) (this.getSystemService(
                INPUT_METHOD_SERVICE
            ) as InputMethodManager).hideSoftInputFromWindow(
                this.window.decorView.applicationWindowToken, 0
            )
        }
        return super.dispatchTouchEvent(ev)
    }
}