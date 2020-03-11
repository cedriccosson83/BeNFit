package isen.cedriclucieflorent.benfit.models

data class Exercice (
    var id: String = "",
    var name: String = "",
    var idUser: String = "",
    var description: String = "",
    var difficulty: String = "",
    var sport : String = "",
    var pictureUID : String = "",
    var urlYt : String = ""
)
