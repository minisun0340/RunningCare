package com.example.student_run_app

import android.content.Intent
import android.content.SharedPreferences
import android.media.AudioManager
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class SoundActivity : AppCompatActivity() {
    var sp: SoundPool? = null
    var soundID1:Int = 0; var soundID2:Int = 0; var soundID3:Int = 0; var soundID4:Int = 0;
    var soundID5:Int = 0; var soundID6:Int = 0; var soundID7:Int = 0; var soundID8:Int = 0; var soundID9:Int = 0;
    var radioButton: RadioButton? = null
    var button:RadioButton? = null

    /* SoundActivity에서 저장한 값 가져오기 */
    //val sIntent : Intent = getIntent() //intent로 할 경우 Accidental override 발생 -> sIntent로 수정
    private val SoundValueText: EditText? = null
    private val SoundValue: String? = null

    /* 설정 값 변경 시 필요 */
    private val appData: SharedPreferences? = null

    private fun showMessage(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    /* 테스트용으로 하는 것 */
    private val LOGIN_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sound)

        /* sound 설정 */sp = SoundPool(5, AudioManager.STREAM_MUSIC, 0)
        soundID1 = sp!!.load(this, R.raw.original_alarm, 1)
        soundID2 = sp!!.load(this, R.raw.emergency, 1)
        soundID3 = sp!!.load(this, R.raw.fire, 1)
        soundID4 = sp!!.load(this, R.raw.bell, 1)
        soundID5 = sp!!.load(this, R.raw.siren, 1)
        soundID6 = sp!!.load(this, R.raw.virus, 1)
        soundID8 = sp!!.load(this, R.raw.man, 1)
        soundID9 = sp!!.load(this, R.raw.woman, 1)

        /* 라디오 버튼 선택 시 */
        val button = findViewById<View>(R.id.ButtonID1) as RadioButton
        val button2 = findViewById<View>(R.id.ButtonID2) as RadioButton
        val button3 = findViewById<View>(R.id.ButtonID3) as RadioButton
        val button4 = findViewById<View>(R.id.ButtonID4) as RadioButton
        val button5 = findViewById<View>(R.id.ButtonID5) as RadioButton
        val button6 = findViewById<View>(R.id.ButtonID6) as RadioButton
        val button8 = findViewById<View>(R.id.ButtonID8) as RadioButton
        val button9 = findViewById<View>(R.id.ButtonID9) as RadioButton

        /* 연습용 */
        var inputText: String

        /* 기본음 버튼 클릭시 효과음 발생 */button.setOnClickListener {
            sp!!.play(
                soundID1,
                1f,
                1f,
                0,
                0,
                1f
            ) /* 작성 */
        }

        /* 일반1번 버튼 클릭시 효과 음 발생 */button2.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반2번 버튼 클릭시 효과음 발생 */button3.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반3번 버튼 클릭시 효과음 발생 */button4.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반4번 버튼 클릭시 효과음 발생 */button5.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반5번 버튼 클릭시 효과음 발생 */button6.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반8번 버튼 클릭시 효과음 발생 */button8.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 1f, 1f, 0, 0, 1f) /* 작성 */
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
        }

        /* 일반9번 버튼 클릭시 효과음 발생 */button9.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 1f, 1f, 0, 0, 1f) /* 작성 */
        }

        /* 녹음 버튼 클릭시 효과음 발생 */
        val record = findViewById<View>(R.id.ButtonID7) as Button
        record.setOnClickListener {
            sp!!.play(soundID1, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID2, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID3, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID4, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID5, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID6, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID8, 0f, 0f, 0, 0, 0f)
            sp!!.play(soundID9, 0f, 0f, 0, 0, 0f)
            startActivity(Intent(applicationContext, RecordActivity::class.java))
        }

        /* 레이아웃에서 라디오 버튼은 라디오 그룹안에 생성 */
        val rg = findViewById<View>(R.id.radioGroup) as RadioGroup
        /* 설정 버튼 선택 시 */
        val setting = findViewById<View>(R.id.ButtonSetting) as Button
        setting.setOnClickListener(object : View.OnClickListener {
            private val editText: EditText? = null
            override fun onClick(view: View) {
                val id = rg.checkedRadioButtonId //getCheckedRadioButtonId()의 리턴 값은 선택된 RadioButton
                val rb = findViewById<View>(id) as RadioButton
                val sf = getSharedPreferences("sFile", MODE_PRIVATE)
                val intent = Intent(this@SoundActivity, MainActivity::class.java)
                when (rb.text.toString()) {
                    "기본음" -> {
                        button.isChecked = true
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "기본음")
                        intent.putExtra("SoundValue", "기본음")
                        editor.commit()
                    }
                    "일반 1" -> {
                        button.isChecked = false
                        button2.isChecked = true
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "일반 1")
                        intent.putExtra("SoundValue", "일반 1")
                        editor.commit()
                    }
                    "일반 2" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = true
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "일반 2")
                        intent.putExtra("SoundValue", "일반 2")
                        editor.commit()
                    }
                    "일반 3" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = true
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "일반 3")
                        intent.putExtra("SoundValue", "일반 3")
                        editor.commit()
                    }
                    "일반 4" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = true
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "일반 4")
                        intent.putExtra("SoundValue", "일반 4")
                        editor.commit()
                    }
                    "일반 5" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = true
                        button8.isChecked = false
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "일반 5")
                        intent.putExtra("SoundValue", "일반 5")
                        editor.commit()
                    }
                    "남자 목소리" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = true
                        button9.isChecked = false
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "남자 목소리")
                        intent.putExtra("SoundValue", "남자 목소리")
                        editor.commit()
                    }
                    "여자 목소리" -> {
                        button.isChecked = false
                        button2.isChecked = false
                        button3.isChecked = false
                        button4.isChecked = false
                        button5.isChecked = false
                        button6.isChecked = false
                        button8.isChecked = false
                        button9.isChecked = true
                        val editor = sf.edit()
                        editor.putString("Sound_Value", "여자 목소리")
                        intent.putExtra("SoundValue", "여자 목소리")
                        editor.commit()
                    }
                }
                startActivity(intent)
            }
        })
    }
}