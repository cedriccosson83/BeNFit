package isen.CedricLucieFlorent.benfit

import isen.CedricLucieFlorent.benfit.Models.Session

data class Program (
        var programID:String? = "",
        var userID : String = "",
        var nameProgram: String = "",
        var descProgram: String = "",
        var levelProgram: String = "",
        var sessionsProgram : ArrayList<Session> = ArrayList()
){}