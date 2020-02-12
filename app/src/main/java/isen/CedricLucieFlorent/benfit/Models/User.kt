package isen.CedricLucieFlorent.benfit.Models

import java.util.*
import kotlin.collections.ArrayList

class User {
    var userid: String = ""
    var email: String? = ""
    var firstname: String? = ""
    var lastname: String? = ""
    var birthdate: String? = ""
    var lastConn: String? = null
    var dateCreate: String? = Date().toString()
    var sportLevel: String? = ""
    var grade: String? = ""
    var weight: String? = ""
    var sports: ArrayList<Sport> = ArrayList()
    var temporrary_sports: String? = ""
    var currentPrograms: ArrayList<UserCurrentProgram> = ArrayList()
    var history: ArrayList<UserHistory> = ArrayList()

    constructor(
        userid: String,
        email: String?,
        firstname: String?,
        lastname: String?,
        birthdate: String?,
        temporrary_sports: String?,
        weight: String?
    ) {
        this.userid = userid
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.birthdate = birthdate
        this.temporrary_sports = temporrary_sports
        this.weight = weight
    }
}