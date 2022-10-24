package com.example.runningcareapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import okhttp3.*
import org.json.JSONObject
import kotlin.concurrent.thread


class LoginActivity : AppCompatActivity() {
    lateinit var handler:Handler



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        handler = object:Handler(Looper.myLooper()!!){
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when(msg.what){
                    1 -> {var intent = Intent(applicationContext, WebViewActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

        val login_Id:EditText = findViewById(R.id.login_Id)
        val login_Pass:EditText = findViewById(R.id.login_Pass)

        val login_btn:AppCompatButton = findViewById(R.id.login_btn)
        val register_btn:AppCompatButton = findViewById(R.id.register_btn)

        login_btn.setOnClickListener {
            val intent = Intent(applicationContext, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

//        login_btn.setOnClickListener {
//            thread {
//                val jsonobj = JSONObject()
//                jsonobj.put("user_id", login_Id.text)
//                jsonobj.put("user_password", login_Pass.text)
//                val url="http://(server_uri)/login"
//
//                //okhttp3라이브러리의 okHttpClient객체를 이용해서 작업
//                val client = OkHttpClient()
//
//                //json데이터를 이용하여 request를 저장
//                val jsondata = jsonobj.toString()
//
//                //서버에 요청을 담당하는 객체
//                //request객체를 만들어주는 객체를 생성
//                val builder = Request.Builder()
//                //Builder객체에 request할 주소(네트워크상의 주소) 세팅
//                builder.url(url)
//                //요청메시지 만들고 요청메시지의 타입이 json이라고 설정
//                builder.post(RequestBody.create(MediaType.parse("application/json"), jsondata))
//                //Builder객체를 이용해서 request객체 만들기
//                val myrequest:Request = builder.build()
//                //생성한 request객체를 이용해서 웹에 request하기 - request결과로 response객체가 리턴
//                val response:Response = client.newCall(myrequest).execute()
//
//                //response에서 메시지 꺼내서 로그 출력하기
//                val result:String? = response.body()?.string()
//                Log.d("http", result!!)
//
//                val loginchk = result.replace('"', ' ').trim()
//                var msg = Message()
//                if(loginchk == "ok"){
//                    msg.what = 1
//                }else {
//                    msg.what = 0
//                }
//                Log.d("http", "${msg.what}=====")
//                handler.sendMessage(msg)
//            }
//        }

        register_btn.setOnClickListener {
            val intent = Intent(applicationContext, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    //dispatchTouchEvent : 터치스크린 이벤트를 처리, 오버라이드시 윈도우에 dispatched되기 전에 터치스크린 이벤트를 가로챌 수 있다.
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        //현재 포커스 되어있는 view
        //focus가 없다면 null return
        val view = currentFocus

        //action_down: 화면 눌렀을 때, action_up: 화면에서 손가락을 뗄 때, action_move: 화면을 누르고 움직일 때
        //현재 focus값이 있고, 터치가 올라오거나, 내려갔을 때
        if(view != null
            && (ev!!.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE)
            && view is EditText){
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