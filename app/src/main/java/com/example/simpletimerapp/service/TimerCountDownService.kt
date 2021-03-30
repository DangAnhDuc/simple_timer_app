package com.example.simpletimerapp.service

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simpletimerapp.application.MyApplication
import com.example.simpletimerapp.util.Constants
import com.example.simpletimerapp.util.NotificationUtil
import com.example.simpletimerapp.util.NotificationUtil.Companion.createRunningNotification
import com.example.simpletimerapp.util.PreferenceUtil
import com.example.simpletimerapp.view.MainActivity


class TimerCountDownService : Service() {
    private lateinit var countDownTimer: CountDownTimer
    override fun onCreate() {
        super.onCreate()
        NotificationUtil.createAppNotificationChanel(applicationContext)
        startForeground(110, createRunningNotification(applicationContext))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val timeRemainingSeconds = intent?.getLongExtra(Constants.TIME_VALUE, 0)?.times(1000)
        val intentUpdateTime = Intent()
        intentUpdateTime.action = Constants.COUNTDOWN_BROADCAST_ACTION
        countDownTimer = object : CountDownTimer(timeRemainingSeconds!!, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                intentUpdateTime.putExtra(
                    Constants.TIME_REMAINING_SECONDS,
                    millisUntilFinished / 1000
                )
                LocalBroadcastManager.getInstance(applicationContext)
                    .sendBroadcast(intentUpdateTime)
            }

            override fun onFinish() {
                stopSelf()
                //send notification when count down finished when app in background
                if (MyApplication.inBackground) {
                    NotificationUtil.showFinishNotification(applicationContext)
                    PreferenceUtil.setTimerState(
                        MainActivity.TimerState.STOPPED,
                        applicationContext
                    )
                }
            }
        }.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {
            Log.d("ERROR", e.message!!)
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}