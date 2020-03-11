package isen.cedriclucieflorent.benfit

import android.os.Bundle

class GradeInfoActivity : MenuActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_grade_info, frameLayout)
    }
}
