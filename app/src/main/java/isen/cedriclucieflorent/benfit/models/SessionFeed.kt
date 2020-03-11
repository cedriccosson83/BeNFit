package isen.cedriclucieflorent.benfit.models

class SessionFeed {
        var sessionID: String = ""
        var nameSessionFeed :String = ""
        var descrSessionFeed: String = ""
        var nameSession :String = ""
        var descrSession: String = ""
        var userID :String =""
        var roundSession :String =""
        var levelSession :String =""
        var likes: ArrayList<String> =ArrayList()
        var imgURI : String = ""

        constructor(
                sessionID: String = "",
                nameSessionFeed :String = "",
                descrSessionFeed: String = "",
                userID :String ="",
                roundSession :String ="",
                levelSession :String ="",
                likes: ArrayList<String> =ArrayList(),
                imgURI: String) {

                this.sessionID = sessionID
                this.nameSessionFeed = nameSessionFeed
                this.descrSessionFeed = descrSessionFeed
                this.userID = userID
                this.roundSession = roundSession
                this.levelSession = levelSession
                this.likes = likes
                this.imgURI = imgURI
        }

        constructor(
                sessionID: String = "",
                nameSession :String = "",
                descrSession: String = "",
                userID :String =""
        ) {
                this.sessionID = sessionID
                this.nameSession = nameSession
                this.descrSession = descrSession
                this.userID = userID
        }
}