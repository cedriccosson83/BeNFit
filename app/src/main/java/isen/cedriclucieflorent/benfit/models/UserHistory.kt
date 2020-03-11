package isen.cedriclucieflorent.benfit.models

class UserHistory {
  var dateSubscribed: String = ""
  var dateValidated: String = ""
  var programs: ArrayList<UserProgramsInHistory> = ArrayList()
}