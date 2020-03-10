package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.ApplicationContext
import isen.CedricLucieFlorent.benfit.Models.ShowExerciceSession
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.setImageFromFirestore
import kotlinx.android.synthetic.main.recycler_view_show_session_exercices.view.*

class ShowExercicesAdapter (val exercices: ArrayList<ShowExerciceSession>, val sessionID: String?,
                            private val clickExercice: (ShowExerciceSession, sessID: String?) -> Unit)
    : RecyclerView.Adapter<ShowExercicesAdapter.ExoViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_show_session_exercices, parent, false)
        return ExoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExoViewHolder, position: Int) {
        val exo = exercices[position]
        holder.bind(exo,sessionID, clickExercice)
    }

    override fun getItemCount(): Int {
        return exercices.count()
    }

    class ExoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(exo: ShowExerciceSession, sessionID: String?,
                 clickSession: (ShowExerciceSession, String?) -> Unit) {
            view.parentViewExerciceShowSession.setOnClickListener {clickSession(exo, sessionID)}
            view.nameExerciceShowSession.text = exo.name
            if (exo.pictureUID != "" && exo.pictureUID != "null") {
                setImageFromFirestore(
                    ApplicationContext.applicationContext(),
                    view.imageViewExerciceShowSession,
                    "exos/${exo.id}/${exo.pictureUID}")
            } else if(exo.urlYt != "" && exo.urlYt != "null") {
                view.imageViewExerciceShowSession.setImageResource(R.drawable.ytb)
            }
            //view.nameExerciceShowSession.setOnClickListener { clickSession(exo, sessionID) }
        }
    }


}