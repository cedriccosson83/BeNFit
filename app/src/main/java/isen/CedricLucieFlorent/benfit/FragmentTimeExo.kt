package isen.CedricLucieFlorent.benfit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

class FragmentTimeExo : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_time, container, false)
    }

    override fun onStart() {
        super.onStart()
        val numberPicker = view?.findViewById<NumberPicker>(R.id.numberPickerMin)
        if (numberPicker != null) {
            numberPicker.minValue = 0
            numberPicker.maxValue = 59
            numberPicker.wrapSelectorWheel = true
            numberPicker.setOnValueChangedListener { _, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
            }
        }

        val numberPickerSec = view?.findViewById<NumberPicker>(R.id.numberPickerSec)
        if (numberPickerSec != null) {
            numberPickerSec.minValue = 0
            numberPickerSec.maxValue = 59
            numberPickerSec.wrapSelectorWheel = true
            numberPickerSec.setOnValueChangedListener { _, oldVal, newVal ->
                val text = "Changed from $oldVal to $newVal"
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}