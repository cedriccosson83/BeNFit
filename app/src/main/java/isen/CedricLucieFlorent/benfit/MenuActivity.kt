package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log

import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle

import android.view.MenuItem

import com.google.android.material.navigation.NavigationView

import androidx.drawerlayout.widget.DrawerLayout

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

import android.view.Menu
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.nav_header_base.*

open class MenuActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener
{
    lateinit var context: Context
    lateinit var toolbar: Toolbar
    lateinit var img_menuOption: ImageView
    lateinit var imgHomeBTN: ImageButton
    lateinit var frameLayout: FrameLayout
    lateinit var auth: FirebaseAuth
    val database = FirebaseDatabase.getInstance()


    private var drawer: DrawerLayout? = null

    private lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        auth = FirebaseAuth.getInstance()
        context = this
        initView()
        frameLayout = findViewById(R.id.container)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun initView() {
        toolbar = findViewById(R.id.toolbar)
        img_menuOption = findViewById(R.id.img_menuOption)
        drawer = findViewById(R.id.drawer_layout)
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        //val headerview = navigationView.getHeaderView(0)
        toggle = ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer!!.addDrawerListener(toggle)
        toggle.isDrawerIndicatorEnabled = false
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)
        updateMenuInfos(auth.currentUser?.uid ?: "")
        img_menuOption.setOnClickListener { drawer!!.openDrawer(GravityCompat.START) }


    }
    private fun updateMenuInfos(userId: String) {
        if (userId == "") return
        val myRef = database.getReference("users")
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (value in dataSnapshot.children) {
                    val id = value.child("userid").value.toString()
                    val mail = value.child("email").value.toString()
                    val fname = value.child("firstname").value.toString()
                    val lname = value.child("lastname").value.toString()
                    val pictUID = value.child("pictureUID").value.toString()

                    if (id == userId) {
                        nav_name.text = "$fname $lname"
                        nav_mail.text = mail
                        setImageFromFirestore(context, nav_picture, "users/$userId/$pictUID")
                        myHomeBTN.setOnClickListener{
                            intent = Intent(context, HomeActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.w("post", "Failed to read value.", error.toException())
            }

        })
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.base, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId


        return if (id == R.id.action_settings) {
            true
        } else super.onOptionsItemSelected(item)

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        val intent: Intent
        if (id == R.id.nav_programs) {
            intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)
            finish()
        } else if (id == R.id.nav_sessions) {
            intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_write_program) {
            intent = Intent(this, ProgramActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_write_session) {
            intent = Intent(this, SessionActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_feed) {
            intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_write) {
            intent = Intent(this, WritePostActivity::class.java)
            startActivity(intent)
            finish()
        }else if (id == R.id.nav_profile) {
            intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
            finish()
        }

        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    companion object {
        private val INTENT_REQUEST_CODE = 200
    }
}
