package isen.cedriclucieflorent.benfit.models

data class SessionProgram (
        var sessionProgID: String = "",
        var sessionID: String = "",
        var nameSessionProgram :String = "",
        var descrSessionProgram: String = "",
        var levelSessionProgram: String = "",
        var userID :String ="",
        var imgURI : String
)