package com.zion830.threedollars.ui.mypage.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.repository.model.v2.response.FAQ


@BindingAdapter("setDeleteAccountBtnVisibility")
fun TextView.setDeleteAccountBtnVisibility(faq: FAQ) {
    visibility = if (faq.question.startsWith("회원 탈퇴") && faq.answer.contains("회원탈퇴하기")) {
        View.VISIBLE
    } else {
        View.GONE
    }
}