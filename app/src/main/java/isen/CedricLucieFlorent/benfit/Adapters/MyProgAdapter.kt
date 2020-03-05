package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.ProgramFollow
import isen.CedricLucieFlorent.benfit.R
import isen.CedricLucieFlorent.benfit.showNumberLikes
import kotlinx.android.synthetic.main.recycler_view_my_programs.view.*

class MyProgAdapter (private val programs: ArrayList<ProgramFollow>)
    : RecyclerView.Adapter<MyProgAdapter.MyProgramViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_my_programs, parent, false)
        return MyProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class MyProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(program: ProgramFollow) {
            view.nameMyProgs.text = program.nameProgramFollow
            view.descMyProg.text = program.descrProgramFollow
            showNumberLikes(database, "programs/${program.programID}/likes", view.nbLikesMyProgs )
        }
    }


}