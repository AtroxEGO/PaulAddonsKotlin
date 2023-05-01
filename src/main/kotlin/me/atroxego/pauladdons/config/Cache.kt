package me.atroxego.pauladdons.config

object Cache {
    @JvmField
    var currentDay = 0
    var currentHour = 0
    var currentMinute = 0
    var currentTime = ""
    var lastLocation = ""
    @JvmField
    var lastIP = ""
    @JvmField
    var lastName = ""
    @JvmField
    var attempts = 0
}