package isen.CedricLucieFlorent.benfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isen.CedricLucieFlorent.benfit.Models.Exercice
import kotlinx.android.synthetic.main.recycler_view_exo_session.view.*


class ExoSessionAdapter(val exos: ArrayList<Exercice>, val clickListener: (Exercice) -> Unit): RecyclerView.Adapter<ExoSessionAdapter.ExoSessionViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoSessionAdapter.ExoSessionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_exo_session, parent, false)
        return ExoSessionAdapter.ExoSessionViewHolder(view)
    }



    override fun onBindViewHolder(holder: ExoSessionAdapter.ExoSessionViewHolder, position: Int) {
        val exo = exos[position]
        holder.bind(exo,clickListener)
    }

    override fun getItemCount(): Int {
        return exos.count()
    }


    class ExoSessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(exo: Exercice, clickListener: (Exercice) -> Unit){
            view.nameExoSession.text = "${exo.name}"
            view.descExoSession.text = "${exo.description}"
            view.difficultyExoSession.text = "Difficulté : ${exo.difficulty}"
            view.sportExoSession.text = "(${exo.sport})"

        }
    }



}