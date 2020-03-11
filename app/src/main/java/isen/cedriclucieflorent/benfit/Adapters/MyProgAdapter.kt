package isen.cedriclucieflorent.benfit.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.cedriclucieflorent.benfit.Functions.setImageFromFirestore
import isen.cedriclucieflorent.benfit.Functions.showNumberLikes
import isen.cedriclucieflorent.benfit.Models.ProgramFollow
import isen.cedriclucieflorent.benfit.R

import kotlinx.android.synthetic.main.recycler_view_my_programs.view.*

class MyProgAdapter (
        private val programs: ArrayList<ProgramFollow>,
        private val clickListenerProgram: (ProgramFollow) -> Unit)
    : RecyclerView.Adapter<MyProgAdapter.MyProgramViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyProgramViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_view_my_programs, parent, false)
        return MyProgramViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyProgramViewHolder, position: Int) {
        val program = programs[position]
        holder.bind(program, clickListenerProgram)
    }

    override fun getItemCount(): Int {
        return programs.count()
    }

    class MyProgramViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        lateinit var auth: FirebaseAuth
        val database = FirebaseDatabase.getInstance()

        fun bind(program: ProgramFollow, clickListenerProgram: (ProgramFollow) -> Unit) {
            view.nameMyProgs.text = program.nameProgramFollow
            view.descMyProg.text = program.descrProgramFollow
            view.parentMyProg.setOnClickListener{ clickListenerProgram(program)}
            val img = program.imageURI
            setImageFromFirestore(
                view.imgViewMyProgs,
                "programs/${program.programID}/${img}")
            showNumberLikes(database, "programs/${program.programID}/likes", view.nbLikesMyProgs )
        }
    }


}