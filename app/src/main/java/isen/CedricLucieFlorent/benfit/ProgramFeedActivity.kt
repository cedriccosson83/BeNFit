package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
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

class ProgramFeedActivity : MenuActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_program_feed, frameLayout)
        auth = FirebaseAuth.getInstance()

        val id = auth.currentUser?.uid
        if (id != null) {
            showProgramFeed(database, recycler_view_list_prog_feed, this, id)
        }

        recycler_view_list_prog_feed.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    }
    private fun subscribeClicked(programItem : ProgramFeed) {
        /**val intent = Intent(this, NotifActivity::class.java)
        val id = auth.currentUser?.uid
        intent.putExtra("userId", id)
        startActivity(intent)*/

        // A DEFINIR PLUS TARD
    }

    private fun programLiked(programItem : ProgramFeed) {
        auth = FirebaseAuth.getInstance()
        val currentUserID = auth.currentUser?.uid

        val myRef = database.getReference("programs")

        val likes = programItem.likes
        if(likes.all { it != currentUserID }) {
            likes.add(currentUserID ?: "")
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
                        arrayLikes.add(userId)
                    }

                    val programFeed = ProgramFeed(
                            value.child("programID").value.toString(),
                            value.child("nameProgram").value.toString(),
                            value.child("descProgram").value.toString(),
                            value.child("userID").value.toString(),
                            arrayLikes
                    )

                    if (userId == programFeed.userID) {
                        programs.add(programFeed)
                        Log.d("like", "${programs}")

                    }
                }
                programs.reverse()
                recycler_view_list_prog_feed.adapter = ProgramFeedAdapter(programs,{ programsItem : ProgramFeed -> subscribeClicked(programsItem) },{ programItem : ProgramFeed -> programLiked(programItem) })
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}