package isen.cedriclucieflorent.benfit

import isen.cedriclucieflorent.benfit.models.Session

data class Program (
    var programID:String? = "",
    var userID : String = "",
    var nameProgram: String = "",
    var descProgram: String = "",
    var levelProgram: String = "",
    var sessionsProgram : ArrayList<Session> = ArrayList()
)