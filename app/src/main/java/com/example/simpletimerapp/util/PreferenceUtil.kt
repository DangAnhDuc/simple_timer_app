package com.example.simpletimerapp.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.simpletimerapp.util.Constants.Companion.PREVIOUS_REMAINING_TIMER_LENGTH_SECONDS_ID
import com.example.simpletimerapp.util.Constants.Companion.PREVIOUS_TIMER_LENGTH_SECONDS_ID
import com.example.simpletimerapp.util.Constants.Companion.TIMER_LENGTH_SECONDS_ID
import com.example.simpletimerapp.util.Constants.Companion.TIMER_STATE_ID
import com.example.simpletimerapp.view.MainActivity

@Suppress("DEPRECATION")
class PreferenceUtil {
    companion object {

        //Get & set value of newest time picked
        fun getTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(TIMER_LENGTH_SECONDS_ID, 15)
        }

        fun setTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        //Get & set value of previous left time picked
        fun getPreviousTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 15)
        }

        fun setPreviousTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        //Get & set value of previous left time remaining
        fun getPreviousRemainingTimerLengthSeconds(context: Context): Long {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            return preferences.getLong(PREVIOUS_REMAINING_TIMER_LENGTH_SECONDS_ID, 15)
        }

        fun setPreviousRemainingTimerLengthSeconds(seconds: Long, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            editor.putLong(PREVIOUS_REMAINING_TIMER_LENGTH_SECONDS_ID, seconds)
            editor.apply()
        }

        fun getTimerState(context: Context): MainActivity.TimerState {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            val ordinal = preferences.getInt(TIMER_STATE_ID, 0)
            return MainActivity.TimerState.values()[ordinal]
        }

        fun setTimerState(state: MainActivity.TimerState, context: Context) {
            val editor = PreferenceManager.getDefaultSharedPreferences(context).edit()
            val ordinal = state.ordinal
            editor.putInt(TIMER_STATE_ID, ordinal)
            editor.apply()
        }
    }
}