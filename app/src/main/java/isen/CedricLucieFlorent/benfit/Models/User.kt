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
}