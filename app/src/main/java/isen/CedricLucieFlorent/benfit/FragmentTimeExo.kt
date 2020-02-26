package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_exercice.*

class FragmentTimeExo : Fragment() {
    // TODO: Rename and change types of parameters
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onStart() {
        super.onStart()
        val numberPicker = view?.findViewById<NumberPicker>(R.id.numberPickerMin)
        if (numberPicker != null) {
            numberPicker.minValue = 0
            numberPicker.maxValue = 59
            numberPicker.wrapSelectorWheel = true
            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
            }
        }

        val numberPickerSec = view?.findViewById<NumberPicker>(R.id.numberPickerSec)
        if (numberPickerSec != null) {
            numberPickerSec.minValue = 0
            numberPickerSec.maxValue = 59
            numberPickerSec.wrapSelectorWheel = true
            numberPickerSec.setOnValueChangedListener { picker, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
            }
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}