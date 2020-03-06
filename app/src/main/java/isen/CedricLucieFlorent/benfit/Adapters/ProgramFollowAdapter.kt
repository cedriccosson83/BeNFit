package isen.CedricLucieFlorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.ProgramFollow
import isen.CedricLucieFlorent.benfit.R
import kotlinx.android.synthetic.main.recycler_view_followed_programs.view.*

class ProgramFollowAdapter (private val programs: ArrayList<ProgramFollow>, val clickListenerProgram: (ProgramFollow) -> Unit)
    : RecyclerView.Adapter<ProgramFollowAdapter.ProgramViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_view_followed_programs, parent, false)
        return ProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program, clickListenerProgram)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class ProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(program: ProgramFollow, clickListenerProgram: (ProgramFollow) -> Unit) {
            view.nameProgFollow.text = program.nameProgramFollow
            view.descrProgFollow.text = program.descrProgramFollow
            view.nameProgFollow.setOnClickListener{clickListenerProgram(program)}
        }
    }


}