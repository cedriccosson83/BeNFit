package isen.cedriclucieflorent.benfit.models

data class ShowExerciceSession (
        var id: String = "",
        var name :String = "",
        var idUser :String ="",
        var difficulty : String ="",
        var description: String="",
        var urlYt: String="",
        var pictureUID: String="",
        var sport: String=""
)