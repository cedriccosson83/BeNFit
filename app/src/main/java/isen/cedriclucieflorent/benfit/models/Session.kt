package isen.cedriclucieflorent.benfit.models

data class Session (
        var sessionID:String? = "",
        var userID : String = "",
        var nameSession: String = "",
        var descSession: String = "",
        var levelSession: String = "",
        var exosSession : ArrayList<SessionExercice> = ArrayList(),
        var roundSession  : Int = 0,
        var pictureUID : String
)
