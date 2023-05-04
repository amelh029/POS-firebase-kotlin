package com.example.myapplication.utils.tools.helper

import androidx.fragment.app.FragmentManager
import com.example.myapplication.view.dialog.AlertMessageFragment

class ShowDialogMessage(private var frm: FragmentManager?) {

    var fragment: AlertMessageFragment = AlertMessageFragment()
    fun show(){
        if(frm!= null){
            fragment.show(frm!!, "Message")
        }
    }

    fun setName(name: String?): ShowDialogMessage{
        fragment.name = name
        return this
    }
}