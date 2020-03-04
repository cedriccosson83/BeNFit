package isen.CedricLucieFlorent.benfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.SessionExercice
import kotlinx.android.synthetic.main.recycler_view_exo_session.view.*


class ExoSessionAdapter(val exos: ArrayList<SessionExercice>, val deleteListener: (SessionExercice) -> Unit, val exoListener: (SessionExercice) -> Unit): RecyclerView.Adapter<ExoSessionAdapter.ExoSessionViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoSessionAdapter.ExoSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_exo_session, parent, false)
        return ExoSessionAdapter.ExoSessionViewHolder(view)
    }



    override fun onBindViewHolder(holder: ExoSessionAdapter.ExoSessionViewHolder, position: Int) {
        val exo = exos[position]
        holder.bind(exo,deleteListener, exoListener)
    }

    override fun getItemCount(): Int {
        return exos.count()
    }


    class ExoSessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(exo: SessionExercice, deleteListener: (SessionExercice) -> Unit, exoListener: (SessionExercice) -> Unit){
            //view.nameExoSession.text = "${exo.exoSessionID}"
            val database = FirebaseDatabase.getInstance()
            showExo(database, exo.exoID,  view.nameExoSession)
            //showRepExosSession(database, exo.exoSessionID, view.textRepExoSession)
            view.btnDeleteExoSession.setOnClickListener { deleteListener(exo) }
            view.textRepExoSession.setOnClickListener { exoListener(exo) }
        }
    }



}