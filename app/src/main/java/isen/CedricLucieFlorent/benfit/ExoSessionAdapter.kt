package isen.CedricLucieFlorent.benfit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.SessionExercice
import kotlinx.android.synthetic.main.recycler_view_exo_session.view.*


class ExoSessionAdapter(
        private val exos: ArrayList<SessionExercice>,
        private val deleteListener: (SessionExercice) -> Unit)
        : RecyclerView.Adapter<ExoSessionAdapter.ExoSessionViewHolder>(){


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExoSessionViewHolder {
                val view = LayoutInflater.from(parent.context)
                        .inflate(R.layout.recycler_view_exo_session, parent, false)
                return ExoSessionViewHolder(view)
        }



        override fun onBindViewHolder(holder: ExoSessionViewHolder, position: Int) {
                val exo = exos[position]
                holder.bind(exo,deleteListener)
        }

        override fun getItemCount(): Int {
                return exos.count()
        }


        class ExoSessionViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

                fun bind(exo: SessionExercice, deleteListener: (SessionExercice) -> Unit){
                        val database = FirebaseDatabase.getInstance()
                        showExo(database, exo.exoID,  view.nameExoSession, view.imageViewExoSession)
                        view.btnDeleteExoSession.setOnClickListener { deleteListener(exo) }
                        view.textRepExoSession.setOnClickListener { showPopUpDetails(database,
                                it.context, exo) }
                }
        }



}