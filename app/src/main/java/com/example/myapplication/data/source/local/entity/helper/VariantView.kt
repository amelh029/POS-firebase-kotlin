package com.example.myapplication.data.source.local.entity.helper

import android.widget.RadioGroup
import android.widget.TextView
import com.example.myapplication.data.source.local.entity.room.master.Variant

data class VariantView(
    var variant: Variant,
    var textError: TextView,
    var radioGroup: RadioGroup,
    var options: List<OptionWithRadioButton>
)
