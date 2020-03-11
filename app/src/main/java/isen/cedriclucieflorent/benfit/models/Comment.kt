package isen.cedriclucieflorent.benfit.models

class Comment {
    var userid: String = ""
    var parentid: String = ""
    var date: String? = ""
    var content: String? = ""
    var likes: ArrayList<String> = ArrayList()

    constructor(
        userid: String,
        parentid: String,
        date: String?,
        content: String?,
        likes: ArrayList<String>
    ) {
        this.userid = userid
        this.parentid = parentid
        this.date = date
        this.content = content
        this.likes = likes
    }
}