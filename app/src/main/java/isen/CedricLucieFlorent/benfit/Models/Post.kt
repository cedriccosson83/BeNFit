package isen.CedricLucieFlorent.benfit.Models

class Post {
    var userid: String = ""
    var postid: String = ""
    var date: String? = ""
    var content: String? = ""
    var likes: ArrayList<String> = ArrayList()

    constructor(
        userid: String,
        postid: String,
        date: String?,
        content: String?,
        likes: ArrayList<String>
    ) {
        this.userid = userid
        this.postid = postid
        this.date = date
        this.content = content
        this.likes = likes
    }
}