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
    var currentPrograms: ArrayList<UserCurrentProgram> = ArrayList()
    var history: ArrayList<UserHistory> = ArrayList()


    constructor(
        userid: String,
        email: String?,
        firstname: String?,
        lastname: String?,
        birthdate: String?,
        lastConn: String?,
        dateCreate: String?,
        sportLevel: String?,
        grade: String?,
        weight: String?,
        sports: ArrayList<Sport>,
        currentPrograms: ArrayList<UserCurrentProgram>,
        history: ArrayList<UserHistory>
    ) {
        this.userid = userid
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.birthdate = birthdate
        this.lastConn = lastConn
        this.dateCreate = dateCreate
        this.sportLevel = sportLevel
        this.grade = grade
        this.weight = weight
        this.sports = sports
        this.currentPrograms = currentPrograms
        this.history = history
    }

    constructor(
        userid: String,
        email: String?,
        firstname: String?,
        lastname: String?,
        birthdate: String?,
        sports: ArrayList<Sport>,
        weight: String?
    ) {
        this.userid = userid
        this.email = email
        this.firstname = firstname
        this.lastname = lastname
        this.birthdate = birthdate
        this.weight = weight
        this.sports = sports
    }


}

