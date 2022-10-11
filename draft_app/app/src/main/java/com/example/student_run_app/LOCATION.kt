package com.example.student_run_app

class LOCATION(latitude_1: Double = 0.0, longitude_2: Double = 0.0) {
    annotation class IgnoreExtraProperties

    var Latitude = 0.0
    var Longitude = 0.0

    fun LOCATION() {}

    fun get_Latitude(): Double {
        return Latitude
    }

    fun Set_Latitude() {
        Latitude = Latitude
    }

    fun get_Longitude(): Double {
        return Longitude
    }

    fun set_Longitude() {
        Longitude = Longitude
    }

    override fun toString(): String {
        return "Location{" +
                "Latitude='" + Latitude + '\'' +
                ", Longitude='" + Longitude + '\'' +
                '}'
    }
}