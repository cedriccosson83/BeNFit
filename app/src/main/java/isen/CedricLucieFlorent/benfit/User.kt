package isen.CedricLucieFlorent.benfit

import com.google.firebase.database.IgnoreExtraProperties
import java.util.*
import kotlin.collections.ArrayList

@IgnoreExtraProperties
data class User  (
    var userid: String = "",
    var email: String? = "",
    var firstname: String? = "",
    var lastname: String? = "",
    var birthdate: String? = "",
    var sport: String? = "",
    var weight: Number? = 0
)