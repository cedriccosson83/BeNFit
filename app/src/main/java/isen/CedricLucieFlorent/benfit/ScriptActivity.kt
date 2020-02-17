package isen.CedricLucieFlorent.benfit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.view.GravityCompat
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import isen.CedricLucieFlorent.benfit.Models.Sport


class ScriptActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val database = FirebaseDatabase.getInstance()

        val ref = database.getReference("sports")

        val array: ArrayList<String> = ArrayList()

        array.add("Accrobranche");
        array.add("Aerobic");
        array.add("Aéromodélisme");
        array.add("Aérostation");
        array.add("Agility");
        array.add("Aikido");
        array.add("Airsoft");
        array.add("Alpinisme");
        array.add("Apnée");
        array.add("Athlétisme");
        array.add("Aviation");
        array.add("Aviron");
        array.add("Babyfoot");
        array.add("Badminton");
        array.add("Baseball");
        array.add("Basketball");
        array.add("Biathlon");
        array.add("Billard");
        array.add("BMX");
        array.add("Bobsleigh");
        array.add("Boccia");
        array.add("Bodyboard");
        array.add("Boomerang");
        array.add("Bowling");
        array.add("Boxe");
        array.add("Bridge");
        array.add("Canoë");
        array.add("Canoë");
        array.add("Canyonisme");
        array.add("Capoeira");
        array.add("Carrom");
        array.add("Catch");
        array.add("Chanbara");
        array.add("Cheerleading");
        array.add("Cirque");
        array.add("Claquettes");
        array.add("Combat");
        array.add("Course");
        array.add("Cricket");
        array.add("Croquet");
        array.add("Crosse");
        array.add("Crossfit");
        array.add("Curling");
        array.add("Cyclisme");
        array.add("Danse");
        array.add("Danse");
        array.add("Deltaplane");
        array.add("Echecs");
        array.add("Equitation");
        array.add("Escalade");
        array.add("Escrime");
        array.add("Fitness");
        array.add("Flag");
        array.add("Fléchettes");
        array.add("Floorball");
        array.add("Football");
        array.add("Footing");
        array.add("Funboard");
        array.add("Futsal");
        array.add("Giraviation");
        array.add("Golf");
        array.add("Gouren");
        array.add("Grappling");
        array.add("Gymnastique");
        array.add("Haltérophilie");
        array.add("Handball");
        array.add("Handisport");
        array.add("Hapkido");
        array.add("Hockey");
        array.add("Iaïdo");
        array.add("Jetski");
        array.add("Jodo");
        array.add("Jorkyball");
        array.add("Joutes");
        array.add("Ju");
        array.add("Judo");
        array.add("Karaté");
        array.add("Karting");
        array.add("Kempo");
        array.add("Kendo");
        array.add("Kenjutsu");
        array.add("Kitesurf");
        array.add("Kobudo");
        array.add("Krav");
        array.add("Kyudo");
        array.add("Lancer");
        array.add("Lancer");
        array.add("Lancer");
        array.add("Luge");
        array.add("Lutte");
        array.add("Marche");
        array.add("Monocycle");
        array.add("Moto");
        array.add("Motoneige");
        array.add("Mountainboard");
        array.add("Musculation");
        array.add("Naginata");
        array.add("Natation");
        array.add("Natation");
        array.add("Ninjitsu");
        array.add("Nunchaku");
        array.add("Omnikin");
        array.add("Padel");
        array.add("Paintball");
        array.add("Pancrace");
        array.add("Parachutisme");
        array.add("Paramoteur");
        array.add("Parapente");
        array.add("Patinage");
        array.add("Pêche");
        array.add("Pentathlon");
        array.add("Pétanque");
        array.add("Peteca");
        array.add("Planche");
        array.add("Plongée");
        array.add("Plongeon");
        array.add("Polo");
        array.add("Qi");
        array.add("Quad");
        array.add("Quilles");
        array.add("Rafting");
        array.add("Ragga");
        array.add("Raid");
        array.add("Rallye");
        array.add("Randonnée");
        array.add("Rock");
        array.add("Roller");
        array.add("Rugby");
        array.add("Salsa");
        array.add("Samba");
        array.add("Sambo");
        array.add("Sarbacane");
        array.add("Sauvetage");
        array.add("Skateboard");
        array.add("Skeleton");
        array.add("Ski");
        array.add("Snowboard");
        array.add("Softball");
        array.add("Spéléologie");
        array.add("Squash");
        array.add("Sumo");
        array.add("Surf");
        array.add("Taekwondo");
        array.add("Tambourin");
        array.add("Tango");
        array.add("Tennis");
        array.add("Tir");
        array.add("Tir");
        array.add("Traîneaux");
        array.add("Trampoline");
        array.add("Triathlon");
        array.add("Trottinette");
        array.add("Tumbling");
        array.add("ULM");
        array.add("Ultimate");
        array.add("Ultimate");
        array.add("Varappe");
        array.add("Vélo");
        array.add("Voile");
        array.add("Volleyball");
        array.add("Voltige");
        array.add("VTT");
        array.add("Wakeboard");
        array.add("Waterpolo");
        array.add("Yoga");
        array.add("Zumba");

        for (str in array) {
            ref.child(str).setValue(Sport(str, ArrayList()))
        }

    }
}
