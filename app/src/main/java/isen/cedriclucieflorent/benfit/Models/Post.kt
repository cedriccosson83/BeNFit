package isen.cedriclucieflorent.benfit.Models

class Post {
    var userid: String = ""
    var postid: String = ""
    var date: String? = ""
    var content: String? = ""
    var likes: ArrayList<String> = ArrayList()
    var postImgUID : String = ""
    var programId: String = ""
    var sessionId: String = ""
    var exoId: String = ""

    constructor(
        userid: String,
        postid: String,
        date: String?,
        content: String?,
        likes: ArrayList<String>,
        postImgUID: String,
        programId: String,
        sessionId: String,
        exoId: String
    ) {
        this.userid = userid
        this.postid = postid
        this.date = date
        this.content = content
        this.likes = likes
        this.postImgUID = postImgUID
        this.programId = programId
        this.sessionId = sessionId
        this.exoId = exoId
    }

    constructor(
        userid: String,
        postid: String,
        date: String?,
        content: String?,
        likes: ArrayList<String>,
        postImgUID: String
    ) {
        this.userid = userid
        this.postid = postid
        this.date = date
        this.content = content
        this.likes = likes
        this.postImgUID = postImgUID
    }
}