package com.example.simpletimerapp.util

class Constants {
    companion object {
        const val COUNTDOWN_BROADCAST_ACTION = "COUNTDOWN_BROADCAST_ACTION"
        const val UPDATE_TIME_BROADCAST_ACTION = "UPDATE_TIME_BROADCAST_ACTION"
        const val REQUEST_UPDATE_TIME_LENGTH = "REQUEST_UPDATE_TIME_LENGTH"

        const val TIME_VALUE = "TIME_VALUE"
        const val TIME_REMAINING_SECONDS = "TIME_REMAINING_SECONDS"
        const val NOTIFICATION_CHANEL_ID = "NOTIFICATION_CHANEL_ID"
        const val NOTIFICATION_CHANEL_NAME = "Simple timer app"
        const val TIMER_ID = 1

        const val MAX_SETTING_MINUTES = 60
        const val MIN_SETTING_MINUTES = 0
        const val MAX_SETTING_SECONDS = 60
        const val MIN_SETTING_SECONDS = 0

        const val SECONDS_PER_MINUTE : Int= 60

        const val TIMER_LENGTH_SECONDS_ID = "TIMER_LENGTH_SECONDS_ID"
        const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "PREVIOUS_TIMER_LENGTH_SECONDS_ID"
        const val PREVIOUS_REMAINING_TIMER_LENGTH_SECONDS_ID = "PREVIOUS_REMAINING_TIMER_LENGTH_SECONDS_ID"
        const val TIMER_STATE_ID = "TIMER_STATE_ID"
    }
}