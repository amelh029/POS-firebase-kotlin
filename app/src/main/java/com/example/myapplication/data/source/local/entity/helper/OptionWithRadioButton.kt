package com.example.myapplication.data.source.local.entity.helper

import android.widget.RadioButton
import com.example.myapplication.data.source.local.entity.room.master.VariantOption

data class OptionWithRadioButton(
    var variantOption: VariantOption,
    var radioButton: RadioButton
)
