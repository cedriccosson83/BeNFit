package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.NumberPicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_exercice.*

class ExerciceActivity : AppCompatActivity(),  OnFragmentInteractionListener  {

    var etatFragment : Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercice)



        val firstFragment = FragmentRepExo()
        supportFragmentManager.beginTransaction().add(R.id.fragmentContainer, firstFragment).commit()

        btnRep.setOnClickListener {
            val firstFragment = FragmentRepExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, firstFragment).commit()
        }

        btnTime.setOnClickListener {
            val secondFragment = FragmentTimeExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, secondFragment).commit()
        }


    }

    override fun swipeFragment() {
        if(etatFragment == 0){
            val secondFragment = FragmentTimeExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, secondFragment).commit()
            etatFragment = 1
        }else{
            val firstFragment = FragmentRepExo()
            supportFragmentManager.beginTransaction().replace(R.id.fragmentContainer, firstFragment).commit()
            etatFragment = 0
        }

    }



}
