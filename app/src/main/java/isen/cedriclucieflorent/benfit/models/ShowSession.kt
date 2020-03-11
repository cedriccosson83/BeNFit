package isen.cedriclucieflorent.benfit

data class ShowSession (
        var sessionID:String? = "",
        var userID : String = "",
        var nameSession: String = "",
        var descSession: String = "",
        var nbrRound: String = "",
        var levelSession: String = "",
        var exosSession : ArrayList<String> = ArrayList(),
        var likes : ArrayList<String> = ArrayList(),
        var imgURI : String
)