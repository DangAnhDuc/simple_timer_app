package com.example.simpletimerapp.view

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Window
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.simpletimerapp.R
import com.example.simpletimerapp.util.Constants
import com.example.simpletimerapp.util.Constants.Companion.MAX_SETTING_MINUTES
import com.example.simpletimerapp.util.Constants.Companion.MAX_SETTING_SECONDS
import com.example.simpletimerapp.util.Constants.Companion.MIN_SETTING_MINUTES
import com.example.simpletimerapp.util.Constants.Companion.MIN_SETTING_SECONDS
import com.example.simpletimerapp.util.Constants.Companion.SECONDS_PER_MINUTE
import com.example.simpletimerapp.util.PreferenceUtil
import kotlinx.android.synthetic.main.time_picker_dialog_layout.*


class TimePickerDialog(context: Context) : Dialog(context) {
    init {
        setCancelable(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.time_picker_dialog_layout)
        minutes_picker.maxValue = MAX_SETTING_MINUTES
        minutes_picker.minValue = MIN_SETTING_MINUTES

        seconds_picker.maxValue = MAX_SETTING_SECONDS
        seconds_picker.minValue = MIN_SETTING_SECONDS

        minutes_picker.wrapSelectorWheel = false
        seconds_picker.wrapSelectorWheel = false

        btn_cancel.setOnClickListener {
            dismiss()
        }
        btn_set.setOnClickListener {
            val minutes = minutes_picker.value
            val seconds = seconds_picker.value
            val timeLength = minutes * SECONDS_PER_MINUTE + seconds
            PreferenceUtil.setTimerLengthSeconds(timeLength.toLong(), context)
            dismiss()
            sendUpdateTimeLength()
        }
    }

    private fun sendUpdateTimeLength() {
        val intent = Intent()
        intent.action = Constants.UPDATE_TIME_BROADCAST_ACTION
        intent.putExtra(Constants.REQUEST_UPDATE_TIME_LENGTH, true)
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent)
    }
}