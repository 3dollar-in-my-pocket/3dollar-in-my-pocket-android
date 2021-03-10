package com.zion830.threedollars.ui.mypage.adapter

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zion830.threedollars.repository.model.response.FaqByTag
import com.zion830.threedollars.repository.model.response.FaqByTagResponse

@BindingAdapter("bindFaqs", requireAll = true)
fun RecyclerView.bindFaqs(data: FaqByTagResponse?) {
    val tags = data?.groupBy { it.tags.first() } ?: mapOf()
    val adapter = adapter as? FaqRecyclerAdapter?

    adapter?.submitList(tags.map { Faq(it.key, it.value) })
}

@BindingAdapter("setDeleteAccountBtnVisibility")
fun TextView.setDeleteAccountBtnVisibility(faqByTag: FaqByTag) {
    visibility = if (faqByTag.question.startsWith("회원 탈퇴") && faqByTag.answer.contains("회원탈퇴하기")) {
        View.VISIBLE
    } else {
        View.GONE
    }
}