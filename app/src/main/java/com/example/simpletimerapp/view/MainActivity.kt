package com.example.simpletimerapp.view

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simpletimerapp.R
import com.example.simpletimerapp.application.MyApplication
import com.example.simpletimerapp.service.TimerCountDownService
import com.example.simpletimerapp.util.Constants
import com.example.simpletimerapp.util.Constants.Companion.SECONDS_PER_MINUTE
import com.example.simpletimerapp.util.NotificationUtil
import com.example.simpletimerapp.util.PreferenceUtil
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    enum class TimerState {
        STOPPED, PAUSED, RUNNING
    }

    companion object {
        var timerState = TimerState.STOPPED
    }

    private lateinit var intentService: Intent
    private var timerLengthSeconds: Long = 0
    private var timeRemainingSeconds: Long = 0

    private val countDownBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            timeRemainingSeconds = intent!!.getLongExtra(Constants.TIME_REMAINING_SECONDS, 0)
            if (timeRemainingSeconds != 0L) {
                updateCountdownUI()
            } else {
                finishCountDown()
            }
        }
    }

    private val updateTimeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (timerState == TimerState.STOPPED) {
                initTimer()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.app_toolbar_title)

        updateButtonsUI()
        btn_start_pause.setOnClickListener {
            if (timerState == TimerState.STOPPED || timerState == TimerState.PAUSED) {
                startRunningTimer()
                timerState = TimerState.RUNNING
                updateButtonsUI()
            } else {
                this.stopService(Intent(this, TimerCountDownService::class.java))
                timerState = TimerState.PAUSED
                updateButtonsUI()
            }
        }

        btn_stop.setOnClickListener {
            this.stopService(Intent(this, TimerCountDownService::class.java))
            finishCountDown()
        }

        val countDownFilter = IntentFilter()
        countDownFilter.addAction(Constants.COUNTDOWN_BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(countDownBroadcastReceiver, countDownFilter)

        val updateTimeLengthFilter = IntentFilter()
        updateTimeLengthFilter.addAction(Constants.UPDATE_TIME_BROADCAST_ACTION)
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(updateTimeBroadcastReceiver, updateTimeLengthFilter)
    }

    override fun onResume() {
        super.onResume()
        timerState = PreferenceUtil.getTimerState(this)
        updateButtonsUI()
        //init timer and UI based on last leave information
        if (timerState == TimerState.STOPPED) {
            initTimer()
        } else if (timerState == TimerState.PAUSED) {
            setupRemainingTimer()
            updateButtonsUI()
        }
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()
        //Save information of current timer
        PreferenceUtil.setTimerState(timerState, this)
        PreferenceUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PreferenceUtil.setPreviousRemainingTimerLengthSeconds(timeRemainingSeconds, this)
    }


    override fun onDestroy() {
        if (timerState != TimerState.RUNNING) {
            try {
                LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(countDownBroadcastReceiver)
                LocalBroadcastManager.getInstance(this)
                    .unregisterReceiver(updateTimeBroadcastReceiver)
            } catch (e: Exception) {
                Log.d("ERROR", e.message!!)
            }
        }
        super.onDestroy()
    }

    private fun initTimer() {
        //init timer when new time length set or first time
        setupNewCountDownTime()
        updateButtonsUI()
        updateCountdownUI()
    }

    private fun finishCountDown() {
        timerState = TimerState.STOPPED
        setupNewCountDownTime()
        updateButtonsUI()
        updateCountdownUI()
        if (!MyApplication.inBackground) {
            showAlertDialog()
        }
    }

    private fun startRunningTimer() {
        //start foreground service to run timer
        intentService = Intent(this, TimerCountDownService::class.java)
        intentService.putExtra(Constants.TIME_VALUE, timeRemainingSeconds)
        ContextCompat.startForegroundService(this, intentService)
    }

    private fun setupNewCountDownTime() {
        timerLengthSeconds = PreferenceUtil.getTimerLengthSeconds(this)
        PreferenceUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        if (timerLengthSeconds == 0L) {
            btn_start_pause.isEnabled = false
            Toast.makeText(this, getString(R.string.pick_time_error_msg), Toast.LENGTH_SHORT).show()
        } else {
            btn_start_pause.isEnabled = true
        }
        progress_countdown.max = timerLengthSeconds.toInt()
        progress_countdown.progress = timerLengthSeconds.toInt()
        timeRemainingSeconds = timerLengthSeconds
    }

    @SuppressLint("SetTextI18n")
    private fun setupRemainingTimer() {
        timerLengthSeconds = PreferenceUtil.getPreviousTimerLengthSeconds(this)
        timeRemainingSeconds = PreferenceUtil.getPreviousRemainingTimerLengthSeconds(this)
        progress_countdown.max = timerLengthSeconds.toInt()
        progress_countdown.progress = timeRemainingSeconds.toInt()
        val timeLeftMinutes = timeRemainingSeconds / SECONDS_PER_MINUTE
        val timeLeftSeconds = timeRemainingSeconds - timeLeftMinutes * SECONDS_PER_MINUTE
        val secondsStr = timeLeftSeconds.toString()
        tv_timer_countdown.text =
            "$timeLeftMinutes:${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
        updateButtonsUI()
    }

    @SuppressLint("SetTextI18n")
    private fun updateCountdownUI() {
        val timeLeftMinutes = timeRemainingSeconds / SECONDS_PER_MINUTE
        val timeLeftSeconds = timeRemainingSeconds - timeLeftMinutes * SECONDS_PER_MINUTE
        val secondsStr = timeLeftSeconds.toString()
        tv_timer_countdown.text =
            "$timeLeftMinutes:${if (secondsStr.length == 2) secondsStr else "0$secondsStr"}"
        progress_countdown.max = PreferenceUtil.getPreviousTimerLengthSeconds(this).toInt()
        progress_countdown.progress = timeRemainingSeconds.toInt()
    }

    private fun updateButtonsUI() {
        when (timerState) {
            TimerState.RUNNING -> {
                btn_start_pause.text = getString(R.string.txt_pause)
                btn_start_pause.icon = ContextCompat.getDrawable(this, R.drawable.ic_pause)
                btn_stop.isEnabled = true
            }
            TimerState.PAUSED -> {
                btn_start_pause.text = getString(R.string.txt_start)
                btn_start_pause.icon = ContextCompat.getDrawable(this, R.drawable.ic_play)
                btn_stop.isEnabled = true
            }
            TimerState.STOPPED -> {
                btn_start_pause.text = getString(R.string.txt_start)
                btn_start_pause.icon = ContextCompat.getDrawable(this, R.drawable.ic_play)
                btn_stop.isEnabled = false
            }

        }
    }

    private fun showAlertDialog() {
        try {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.timer_dialog_title))
            builder.setMessage(getString(R.string.timer_dialog_msg))
            builder.setPositiveButton(getString(R.string.txt_ok)) { _, _ ->
            }
            builder.show()
            notifyUser()
        } catch (e: Exception) {
            Log.d("ERROR", e.message!!)
        }
    }

    @Suppress("DEPRECATION")
    private fun notifyUser() {
        val vibrator: Vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(500)
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringTone = RingtoneManager.getRingtone(applicationContext, notification)
            ringTone.play()
        } catch (e: Exception) {
            Log.d("ERROR", e.message!!)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.timer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.timer_setting -> {
                TimePickerDialog(this).show()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}