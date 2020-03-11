package isen.cedriclucieflorent.benfit

data class ShowProgram (
    var programID:String? = "",
    var userID : String = "",
    var nameProgram: String = "",
    var descProgram: String = "",
    var levelProgram: String = "",
    var sessionsProgram : ArrayList<String> = ArrayList(),
    var likes : ArrayList<String> = ArrayList()
)