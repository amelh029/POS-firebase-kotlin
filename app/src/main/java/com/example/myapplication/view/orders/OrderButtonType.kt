package com.example.myapplication.view.orders

import androidx.annotation.DrawableRes
import com.example.myapplication.R

enum class OrderButtonType(
    @DrawableRes val icon: Int
) {
    PRINT(R.drawable.ic_printshop),
    QUEUE(R.drawable.ic_queue_24),
    PAYMENT(R.drawable.ic_payments_24),
    DONE(R.drawable.ic_done_all),
    CANCEL(R.drawable.ic_cancel_24),
    PUT_BACK(R.drawable.ic_put_back_24),
}
