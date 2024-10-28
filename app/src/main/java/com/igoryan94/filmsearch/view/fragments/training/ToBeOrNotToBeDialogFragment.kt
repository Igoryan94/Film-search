package com.igoryan94.filmsearch.view.fragments.training

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.igoryan94.filmsearch.R

class ToBeOrNotToBeDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_fragment_to_be_or_not_to_be, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnOther by lazy { view.findViewById<Button>(R.id.btn_other) }
        val btnN by lazy { view.findViewById<Button>(R.id.btn_n) }
        val btnY by lazy { view.findViewById<Button>(R.id.btn_y) }
        val textHeader by lazy { view.findViewById<TextView>(R.id.text_header) }

        //Привязываем кнопки
        btnY.setOnClickListener {
            Toast.makeText(view.context, "Yes", Toast.LENGTH_SHORT).show()
        }
        btnN.setOnClickListener {
            Toast.makeText(view.context, "No", Toast.LENGTH_SHORT).show()
        }
        btnOther.setOnClickListener {
            Toast.makeText(view.context, "Other", Toast.LENGTH_SHORT).show()
        }
    }

    //Этот выполняется, когда диалог закрывается
//    override fun onDismiss(dialog: DialogInterface) {
//        super.onDismiss(dialog)
//    }

    //Этот метод срабатывает когда мы отменяем диалог кнопкой назад или нажатием вне области
//    override fun onCancel(dialog: DialogInterface) {
//        super.onCancel(dialog)
//    }
}
