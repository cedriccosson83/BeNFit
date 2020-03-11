package isen.cedriclucieflorent.benfit.models

data class ProgramFollow (
        var programID: String = "",
        var nameProgramFollow :String = "",
        var descrProgramFollow: String = "",
        var userID :String ="",
        var likes: ArrayList<String> =ArrayList(),
        var session: ArrayList<Session>,
        var imageURI: String
)