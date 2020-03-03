package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.ProgramFeedAdapter
import isen.CedricLucieFlorent.benfit.Models.ProgramFeed
import kotlinx.android.synthetic.main.activity_program_feed.*
import kotlinx.android.synthetic.main.recycler_view_feed_program.*
import kotlinx.android.synthetic.main.recycler_view_feed_program.view.*

class ProgramFeedActivity : MenuActivity() {
    val follow = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_program_feed, frameLayout)

        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid
        if (currentUserID != null) {
            val myRef = database.getReference("users").child(currentUserID)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (value in dataSnapshot.child("currentPrograms").children) {
                        if(follow.all { it != value.value.toString()}){
                        follow.add(value.value.toString())
                        }
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TAG", "Failed to read value")
                }
            })
        }

        if (currentUserID != null) {
            showProgramFeed(database, recycler_view_list_prog_feed, this, currentUserID)
        }

        recycler_view_list_prog_feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }
    private fun subscribeClicked(programItem : ProgramFeed, currentUserID : String) {
        val myRef = database.getReference("users").child(currentUserID)
        var idProg = programItem.programID
        Log.d("Follow", follow.toString() + "clicklistener")
        if(follow.all { it != idProg}) {
                Log.d("Follow", follow.toString() + "if")
                follow.add(idProg)
            Log.d("Follow", follow.toString() + "ifapres")
            myRef.child("currentPrograms").setValue(follow)
                //btnSubscribeProg.setImageResource(R.drawable.remove)
            }else{
                Log.d("follow", follow.toString() +"else")
                follow.remove(idProg)
            Log.d("Follow", follow.toString() + "elseapres")
            myRef.child("currentPrograms").setValue(follow)
                //btnSubscribeProg.setImageResource(R.drawable.add)
            }
    }


    private fun programLiked(programItem : ProgramFeed, currentUserID: String) {
        val myRef = database.getReference("programs")

        val likes = programItem.likes
        if(likes.all { it != currentUserID }) {
            likes.add(currentUserID)
            myRef.child(programItem.programID).child("likes").setValue(likes)
        }else{
            likes.remove(currentUserID)
            myRef.child(programItem.programID).child("likes").setValue(likes)
        }

    }

    fun showProgramFeed(database : FirebaseDatabase, view : RecyclerView, context: Context, userId: String) {

        val myRef = database.getReference("programs")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val programs: ArrayList<ProgramFeed> = ArrayList()
                for (value in dataSnapshot.children) {
                    val arrayLikes :ArrayList<String> = ArrayList()
                    for (childLike in value.child("likes").children){
                        val likesUserId : String = childLike.value.toString()
                        arrayLikes.add(likesUserId)
                    }

                    val programFeed = ProgramFeed(
                            value.child("programID").value.toString(),
                            value.child("nameProgram").value.toString(),
                            value.child("descProgram").value.toString(),
                            value.child("userID").value.toString(),
                            arrayLikes
                    )
                    programs.add(programFeed)
                }
                programs.reverse()
                recycler_view_list_prog_feed.adapter =
                    ProgramFeedAdapter(
                        programs,
                        { programItem : ProgramFeed -> subscribeClicked(programItem, userId) },
                        { programItem : ProgramFeed -> programLiked(programItem, userId) })
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}