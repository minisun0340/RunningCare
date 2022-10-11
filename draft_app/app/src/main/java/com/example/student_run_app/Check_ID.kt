package com.example.student_run_app

class Check_ID {
    var Latitude = 0.0
    var Longitude = 0.0
    var Time: String? = null

    fun Check_ID() {
        //Defalut constructor required for calls to DataSnapshot. getValue(Post.class)
    }

    fun Check_ID(Time: String?, Latitude: Double, Longitude: Double) {
        this.Time = Time
        this.Latitude = Latitude
        this.Longitude = Longitude
    }

    override fun toString(): String {
        return "Location{" +
                "Latitude' " + Latitude + '\'' +
                ", Longitude' " + Longitude + '\'' +
                ", Time' " + Time + '\'' +
                '}'
    }
}