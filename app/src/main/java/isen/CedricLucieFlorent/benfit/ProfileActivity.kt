package isen.CedricLucieFlorent.benfit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import isen.CedricLucieFlorent.benfit.Adapters.MyProgAdapter
import isen.CedricLucieFlorent.benfit.Adapters.ProgramFollowAdapter
import isen.CedricLucieFlorent.benfit.Models.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.ArrayList

class ProfileActivity : MenuActivity() {

    lateinit var userId: String
    val follow = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutInflater.inflate(R.layout.activity_profile, frameLayout)
        overridePendingTransition(R.anim.zoom_enter, 0)
        auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: ""
        context = this
        programRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        if (userId != null) {
            val myRef = database.getReference("users").child(userId)

            myRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (value in dataSnapshot.child("currentPrograms").children) {
                        follow.add(value.key.toString())
                    }
                }
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("TAG", "Failed to read value")
                }
            })
        }

        val intent = intent
        val userFromIntent = intent.getStringExtra("userId")?: ""
        if (intent != null)
            if (userId != userFromIntent && userFromIntent != "") {
                showUser(userFromIntent)
                settingsButton.visibility = View.INVISIBLE
            }
            else {
                showUser(userId)
            }
        showMyPrograms()

        settingsButton.setOnClickListener {
            startActivity(Intent(this, ModifyProfile::class.java))
        }

        myProgramButton.setOnClickListener{
            showMyPrograms()
        }

        subscribeProgramButton.setOnClickListener{
            showSubPrograms()
        }
    }

        private fun showUser(userId: String) {
        val myRef = database.getReference("users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var user: User?
                for (value in dataSnapshot.children) {
                    user = User(
                        value.child("userid").value.toString(),
                        value.child("email").value.toString(),
                        value.child("firstname").value.toString(),
                        value.child("lastname").value.toString(),
                        value.child("birthdate").value.toString(),
                        ArrayList(value.child("sports").children.map { Sport(it.child("name").value.toString(), arrayListOf()) }),
                        value.child("weight").value.toString(),
                        value.child("pictureUID").value.toString()
                    )
                    if (user.userid == userId) {
                        val imagePath = user.pictureUID
                        var sportsaff = ""
                        for (sport in user.sports){
                            sportsaff += "${sport.getSportName()}" + " "
                        }
                        fullNameTextView.text = "${user.firstname} ${user.lastname}"
                        descriptionTextView.text = "Date de naissance : ${user.birthdate.toString()}" + "\nEmail : ${user.email} " + "\nPoids : ${user.weight} kg"  + "\nSport(s) pratiqué(s) : ${sportsaff}"
                        setImageFromFirestore(context, ProfilImage, "users/$userId/$imagePath")

                        ProfilImage.setOnClickListener {
                            val fullScreenIntent = Intent(context, FullScreenImageView::class.java)
                            fullScreenIntent.putExtra("url", "users/$userId/$imagePath")
                            startActivity(fullScreenIntent)
                        }
                    }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }
    private fun showMyPrograms(){
        val myRef = database.getReference("programs")

        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val programs: ArrayList<ProgramFollow> = ArrayList()
                for (value in dataSnapshot.children) {
                    if (userId == value.child("userID").value.toString()) {
                        val arrayLikes: ArrayList<String> = ArrayList()
                        for (childLike in value.child("likes").children) {
                            val likesUserId: String = childLike.value.toString()
                            arrayLikes.add(likesUserId)
                        }

                        val programFollow = ProgramFollow(
                                value.child("programID").value.toString(),
                                value.child("nameProgram").value.toString(),
                                value.child("descProgram").value.toString(),
                                value.child("userID").value.toString(),
                                arrayLikes,
                                ArrayList()
                        )
                        programs.add(programFollow)
                    }
                }
                programs.reverse()
                programRecyclerView.adapter = MyProgAdapter(programs,
                    { programItem : ProgramFollow -> redirectToProgram(context, programItem.programID, "MyProg")})
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }

    private fun showSubPrograms(){
        val myRef = database.getReference("programs")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val programs: ArrayList<ProgramFollow> = ArrayList()
                for (value in dataSnapshot.children) {
                    for (fo in follow){
                        if (fo == value.child("programID").value.toString()) {
                            val arrayLikes: ArrayList<String> = ArrayList()
                            /*for (childLike in value.child("likes").children) {
                                val likesUserId: String = childLike.value.toString()
                                arrayLikes.add(likesUserId)
                            }*/

                            val programfollow = ProgramFollow(
                                value.child("programID").value.toString(),
                                value.child("nameProgram").value.toString(),
                                value.child("descProgram").value.toString(),
                                value.child("userID").value.toString(),
                                arrayLikes,
                                ArrayList()
                            )
                        programs.add(programfollow)
                        }
                    }
                }
                programs.reverse()
                programRecyclerView.adapter = ProgramFollowAdapter(programs,
                    {programItem : ProgramFollow -> redirectToProgram(context, programItem.programID, "SubProg")})
            }
            override fun onCancelled(error: DatabaseError) {
                Log.w("session", "Failed to read value.", error.toException())
            }
        })
    }
}
