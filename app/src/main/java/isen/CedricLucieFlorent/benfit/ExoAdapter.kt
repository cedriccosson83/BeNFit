package isen.CedricLucieFlorent.benfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import isen.CedricLucieFlorent.benfit.Functions.setImageFromFirestore
import isen.CedricLucieFlorent.benfit.Models.Exercice
import kotlinx.android.synthetic.main.recycler_view_exo_cell.view.*
import kotlinx.android.synthetic.main.recycler_view_feed_session.view.*


class ExoAdapter(val exos: ArrayList<Exercice>,  val clickListener: (Exercice) -> Unit): RecyclerView.Adapter<ExoAdapter.ExoViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoAdapter.ExoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_exo_cell, parent, false)
        return ExoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExoViewHolder, position: Int) {
        val exo = exos[position]
        holder.bind(exo,clickListener)
    }

    override fun getItemCount(): Int {
        return exos.count()
    }


    class ExoViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(exo: Exercice, clickListener: (Exercice) -> Unit){
            view.nameExoListExo.text = "${exo.name}"
            view.descExoListExo.text = "${exo.description}"
            view.difficultyExoListExo.text = "Difficulté : ${exo.difficulty}"
            view.sportExoListExo.text = "(${exo.sport})"
            if (exo.pictureUID != "null" && exo.pictureUID != ""){
            setImageFromFirestore(ApplicationContext.applicationContext(), view.imageViewExo, "exos/${exo.id}/${exo.pictureUID}")
            }else{
                view.imageViewExo.setImageResource(R.drawable.ytb)
            }
            view.btn_validate_exo_list.setOnClickListener { clickListener(exo) }
        }
    }



}