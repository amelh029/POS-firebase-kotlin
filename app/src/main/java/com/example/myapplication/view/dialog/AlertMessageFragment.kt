package com.example.myapplication.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.databinding.FragmentAlertMessageBinding
import com.example.myapplication.utils.tools.helper.CustomDialogFragment

class AlertMessageFragment : CustomDialogFragment() {

    private lateinit var _binding: FragmentAlertMessageBinding
    var name: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlertMessageBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {
            setFragmentView()
        }
    }

    private fun setFragmentView() {
        _binding.tvAmName.text = name
        _binding.btnAmOk.setOnClickListener { dialog?.dismiss() }
    }
}
