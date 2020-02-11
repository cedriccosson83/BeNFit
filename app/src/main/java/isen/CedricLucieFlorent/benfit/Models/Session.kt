package isen.CedricLucieFlorent.benfit.Models

class Session {
    var id: String = ""
    var name: String = ""
    var author: String = ""
    var description: String = ""
    var difficulty: String = ""
    var exercices: ArrayList<SessionExercice> = ArrayList()
    var categories: ArrayList<Category> = ArrayList()
}
