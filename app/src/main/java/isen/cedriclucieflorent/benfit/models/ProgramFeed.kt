package isen.cedriclucieflorent.benfit.models

data class ProgramFeed (
        var programID: String = "",
        var nameProgramFeed :String = "",
        var descrProgramFeed: String = "",
        var userID :String ="",
        var levelProgram : String="",
        var likes: ArrayList<String> = ArrayList(),
        var sessions: ArrayList<String> = ArrayList(),
        var imgURI : String
)