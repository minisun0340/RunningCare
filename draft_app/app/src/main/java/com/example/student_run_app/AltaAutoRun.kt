package com.example.student_run_app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AltaAutoRun : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") {
            val it = Intent(context, MainActivity::class.java)
            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(it)
        }
    }
}