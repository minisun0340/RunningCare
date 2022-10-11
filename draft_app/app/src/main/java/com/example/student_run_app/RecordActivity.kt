package com.example.student_run_app

import android.content.ContentValues
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RecordActivity : AppCompatActivity() {
    var recorder // 녹음 할려면 필요로 함
            : MediaRecorder? = null
    var mediaPlayer: MediaPlayer? = null

    var fileName: String? = null
    var position = 0 // 현재 재생한 위치

    //final private static String RECORDED_FILE = "/storage/emulated/0/VoiceRecorder/recorded.mp4";

    //final private static String RECORDED_FILE = "/storage/emulated/0/VoiceRecorder/recorded.mp4";
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.record)

        //fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        fileName = "storage/emulated/legacy/recorded.mp4"

        // Environment.getExternalStorageDirectory()는 SD카드 접근 (SD카드 폴더 위치) <- 파일을 만듬
        //File file = new File(Environment.getExternalStorageDirectory(),"recorded.mp4");  // 파일 위치명과 만든 파일 명
        //fileName = file.getAbsolutePath(); // 저장할 "파일명"을 가져오는 것
        //Log.d("RecordActivity","저장할 파일 명 "+fileName);

        // 녹음 버튼 선택 시
        findViewById<View>(R.id.record).setOnClickListener { // 녹음 시작
            if (recorder != null) {
                recorder!!.stop()
                recorder!!.release()
                recorder = null
            }
            recorder = MediaRecorder()
            recorder!!.setAudioSource(MediaRecorder.AudioSource.MIC) // 오디오 입력 지정(마이크)
            recorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP) // 출력 형식 지정
            //마이크로 들어오는 음성데이터는 용량이 크기 때문에 압축이 필요
            recorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB) // 음질을 결정
            recorder!!.setOutputFile(fileName) // 음성 데이터를 저장할 파일 지정
            try {
                Toast.makeText(applicationContext, "녹음을 시작합니다.", Toast.LENGTH_LONG).show()
                recorder!!.start()
                recorder!!.prepare()
            } catch (ex: Exception) {
                Log.e("SampleAudioRecorder", "Exception : ", ex)
            }

            /*
               if(recorder == null){
                   recorder = new MediaRecorder();
                   recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                   recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                   recorder.setOutputFile(fileName);
                   recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

                   try {
                       recorder.prepare();
                   }catch (IOException e){

                   }

                   recorder.start();
               }else {
                   recorder.stop();
                   recorder.release();
                   recorder = null;
               }

                */
        }

        // 녹음 중지 선택 시
        findViewById<View>(R.id.recordstop).setOnClickListener(View.OnClickListener { // 녹음 중지
            if (recorder == null) return@OnClickListener
            recorder!!.stop()
            recorder!!.release()
            recorder = null
            Toast.makeText(applicationContext, "녹음이 중지되었습니다.", Toast.LENGTH_LONG).show()
        })

        // 녹음 재생 선택 시
        findViewById<View>(R.id.play).setOnClickListener {
            // 재생
            if (mediaPlayer != null) { // 사용하기 전에
                mediaPlayer!!.release() // 리소스 해제
                mediaPlayer = null
            }
            Toast.makeText(applicationContext, "녹음된 파일을 재생합니다.", Toast.LENGTH_LONG).show()
            try {
                mediaPlayer = MediaPlayer()

                // mediaPlayer.setDataSource(RECORDED_FILE);
                mediaPlayer!!.prepare()
                mediaPlayer!!.start()
            } catch (e: Exception) {
                Log.e("SampleAudioRecorder", "Audio play failed.", e)
            }
        }

        // 녹음 재생 된 것 일시 정지 선택 시
        findViewById<View>(R.id.pause).setOnClickListener(View.OnClickListener { // 일시 정지
            if (mediaPlayer == null) return@OnClickListener
            Toast.makeText(applicationContext, "재생이 중지되었습니다.", Toast.LENGTH_LONG).show()
            mediaPlayer!!.stop()
            mediaPlayer!!.release()
            mediaPlayer = null
        })

        // 중지된 녹음 재시작 선택 시
        findViewById<View>(R.id.restart).setOnClickListener {
            // 다시 시작
            if (mediaPlayer != null && !mediaPlayer!!.isPlaying) {
                mediaPlayer!!.seekTo(position) // 시작 되는 위치
                mediaPlayer!!.start() // 시작
                Toast.makeText(applicationContext, "다시 시작", Toast.LENGTH_LONG).show()
            }
        }

        // 재생 되고 있는 녹음 정지 선택 시
        findViewById<View>(R.id.playstop).setOnClickListener {
            // 정지
            if (mediaPlayer != null && mediaPlayer!!.isPlaying) {
                mediaPlayer!!.stop() // 정지
                Toast.makeText(applicationContext, "정지", Toast.LENGTH_LONG).show()
            }
        }
    }

    // 녹음한 파일 저장
    fun save(): Uri? {
        val values = ContentValues(10)
        values.put(MediaStore.MediaColumns.TITLE, "Recorded")
        values.put(MediaStore.Audio.Media.ALBUM, "Audio_Album")
        values.put(MediaStore.Audio.Media.ARTIST, "Ton")
        values.put(MediaStore.Audio.Media.DISPLAY_NAME, "Recorded Audio")
        values.put(MediaStore.Audio.Media.IS_RINGTONE, 1)
        values.put(MediaStore.Audio.Media.IS_MUSIC, 1)
        values.put(MediaStore.Audio.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp4") // 미디어 파일의 포맷
        values.put(MediaStore.Audio.Media.DATA, fileName) // 저장된 녹음 파일

        // ContentValues 객체를 추가할 때, 음성 파일에 대한 내용 제공자 URI 사용
        return contentResolver.insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, values)
    }
}