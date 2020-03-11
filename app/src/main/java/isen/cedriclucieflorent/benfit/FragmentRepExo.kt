package isen.cedriclucieflorent.benfit

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.NumberPicker

class FragmentRepExo : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_rep, container, false)
    }

    override fun onStart() {
        super.onStart()
        val numberPicker = view?.findViewById<NumberPicker>(R.id.numberPickerRep)
        if (numberPicker != null) {
            numberPicker.minValue = 0
            numberPicker.maxValue = 500
            numberPicker.wrapSelectorWheel = true
            numberPicker.setOnValueChangedListener { _, _, _ ->

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