package com.example.runningcareapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient

class WebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        webView = findViewById(R.id.webview)
        webView.apply {
            //클릭 시 새창 안뜨게 설정
            webViewClient = WebViewClient()

            //웹뷰에서 팝업창 호출하기 위해
            webChromeClient = object : WebChromeClient(){
                override fun onCreateWindow(
                    view: WebView?,
                    isDialog: Boolean,
                    isUserGesture: Boolean,
                    resultMsg: Message?
                ): Boolean {
                    val newWebView = WebView(this@WebViewActivity).apply{
                        webViewClient = WebViewClient()
                        settings.javaScriptEnabled = true
                    }

                    val dialog = Dialog(this@WebViewActivity).apply{
                        setContentView(newWebView)
                        window!!.attributes.width = ViewGroup.LayoutParams.MATCH_PARENT
                        window!!.attributes.height = ViewGroup.LayoutParams.MATCH_PARENT
                        show()
                    }

                    newWebView.webChromeClient = object : WebChromeClient() {
                        override fun onCloseWindow(window: WebView?) {
                            dialog.dismiss()
                        }
                    }
                    (resultMsg?.obj as WebView.WebViewTransport).webView = newWebView
                    resultMsg.sendToTarget()
                    return true
                }
            }
            settings.javaScriptEnabled = true
            settings.setSupportMultipleWindows(true) //새창 띄우기 허용 여부
            settings.javaScriptCanOpenWindowsAutomatically = true // 자바스크립트 새창 띄우기 허용 여부
            settings.loadWithOverviewMode = true //메타 테크 허용 여부
            settings.useWideViewPort = true //화면 사이즈 맞추기 허용 여부
            settings.setSupportZoom(true) //화면 줌 허용 여부
            settings.builtInZoomControls = true //화면 확대 축소 허용 여부
            settings.cacheMode = WebSettings.LOAD_NO_CACHE //브라우저 캐시 허용 여부
            settings.domStorageEnabled = true //로컬 저장소 허용 여부
            settings.displayZoomControls = true

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                settings.safeBrowsingEnabled = true
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
                settings.mediaPlaybackRequiresUserGesture = false
            }
            settings.allowContentAccess = true
            settings.setGeolocationEnabled(true)

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                settings.allowUniversalAccessFromFileURLs = true
            }
            settings.allowFileAccess = true
            fitsSystemWindows = true

        }
        webView.loadUrl("http://192.168.0.104:8000/login")
    }
}