package com.example.myapplication.view.main.menu.master.bottom

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.data.source.local.entity.room.master.Supplier
import com.example.myapplication.databinding.FragmentSupplierMasterBinding
import com.example.myapplication.utils.tools.BottomSheetView
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.MainViewModel.Companion.getMainViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SupplierMasterFragment (
    private val supplier: Supplier?
) : BottomSheetDialogFragment() {

    constructor(): this(null)

    private lateinit var _binding: FragmentSupplierMasterBinding
    private lateinit var viewModel: MainViewModel

    private val name: String
        get() = _binding.edtSmName.text.toString().trim()

    private val address: String
        get() = _binding.edtSmAddress.text.toString().trim()

    private val phone: String
        get() = _binding.edtSmPhone.text.toString().trim()

    private val desc: String
        get() = _binding.edtSmDesc.text.toString().trim()

    @SuppressLint("SuspiciousIndentation")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentSupplierMasterBinding.inflate(inflater, container, false)
        return _binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val bottomSheetDialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        return BottomSheetView.setBottom(bottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null){

            viewModel = getMainViewModel(requireActivity())

            if (supplier != null){
                setData()
            }else{
                _binding.btnSmSave.setOnClickListener {
                    if (isCheck){
                        saveData(getSupplier())
                    }
                }
            }

            _binding.btnSmCancel.setOnClickListener { dialog?.dismiss() }

        }
    }

    private fun setData(){
        if (supplier != null){
            _binding.edtSmName.setText(supplier.name)
            _binding.edtSmAddress.setText(supplier.address)
            _binding.edtSmDesc.setText(supplier.desc)
            _binding.edtSmPhone.setText(supplier.phone)
            _binding.btnSmSave.setOnClickListener {
                if (isCheck){
                    updateData(getSupplier())
                }
            }
        }
    }

    private val isCheck: Boolean
        get() {
            return when {
                name.isEmpty() -> {
                    _binding.edtSmName.error = "Can not be empty"
                    false
                }
                address.isEmpty() -> {
                    _binding.edtSmAddress.error = "Can not be empty"
                    false
                }
                phone.isEmpty() -> {
                    _binding.edtSmPhone.error = "Can not be empty"
                    false
                }
                phone.toInt() == 0 -> {
                    _binding.edtSmPhone.error = "Can't be 0"
                    false
                }
                desc.isEmpty() -> {
                    _binding.edtSmDesc.error = "Can not be empty"
                    false
                }
                else -> {
                    true
                }
            }
        }

    private fun saveData(supplier: Supplier){
        viewModel.insertSupplier(supplier)
        dialog?.dismiss()
    }

    private fun updateData(supplier: Supplier){
        viewModel.updateSupplier(supplier)
        dialog?.dismiss()
    }

    private fun getSupplier(): Supplier {
        return if (supplier != null){
            Supplier(supplier.id, name, phone, address, desc)
        }else{
            Supplier(name, phone, address, desc)
        }
    }
}