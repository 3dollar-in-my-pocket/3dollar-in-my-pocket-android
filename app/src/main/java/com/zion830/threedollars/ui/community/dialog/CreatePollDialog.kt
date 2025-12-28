package com.zion830.threedollars.ui.community.dialog

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import com.threedollar.common.base.BaseDialogFragment
import com.zion830.threedollars.databinding.DialogCreatePollBinding
import zion830.com.common.base.onSingleClick
import com.zion830.threedollars.core.designsystem.R as DesignSystemR

class CreatePollDialog : BaseDialogFragment<DialogCreatePollBinding>() {

    private var createClick: (String, String, String) -> Unit = { _, _, _ -> }
    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): DialogCreatePollBinding =
        DialogCreatePollBinding.inflate(inflater, container, false)

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "CreatePollDialog", screenName = "create_poll")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, DesignSystemR.style.DialogStyle)
    }
    override fun initViews() {
        pollInfoSetting()
        initButton()
        initEditText()
    }

    private fun initButton() {
        binding.btnCreate.isEnabled = false
        binding.btnCreate.isSelected = false
        binding.btnCreate.onSingleClick {
            val title = binding.editPollName.text.toString()
            val first = binding.editPollFirst.text.toString()
            val second = binding.editPollSecond.text.toString()
            createClick(title, first, second)
            dismiss()
        }
        binding.btnCancel.onSingleClick { dismiss() }
    }

    private fun initEditText() {
        binding.editPollName.addTextChangedListener {
            checkCreateEnable()
            val textLength = it.toString().length
            val colorForN = if (textLength > 0) resources.getColor(DesignSystemR.color.pink, null) else resources.getColor(DesignSystemR.color.gray60, null)
            val spannableString = SpannableString("$textLength/20")

            spannableString.setSpan(
                ForegroundColorSpan(colorForN),
                0,
                spannableString.indexOf("/"),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )

            spannableString.setSpan(
                ForegroundColorSpan(resources.getColor(DesignSystemR.color.gray30, null)),
                spannableString.indexOf("/"),
                spannableString.length,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            binding.twNameCount.text = spannableString

        }
        binding.editPollFirst.addTextChangedListener {
            checkCreateEnable()
        }
        binding.editPollSecond.addTextChangedListener {
            checkCreateEnable()
        }
    }

    private fun pollInfoSetting() {
        val fullText = "* 투표는 3일 동안만 진행돼요\n* 1일 1회만 올릴 수 있어요\n* 부적절한 내용일 경우 임의로 삭제될 수 있어요"
        val spannableString = SpannableString(fullText)

        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(DesignSystemR.color.gray50)),
            0,
            fullText.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val span1Start = fullText.indexOf("3일 동안")
        val span1End = span1Start + "3일 동안".length
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(DesignSystemR.color.gray100,null)),
            span1Start,
            span1End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        val span2Start = fullText.indexOf("1일 1회")
        val span2End = span2Start + "1일 1회".length
        spannableString.setSpan(
            ForegroundColorSpan(resources.getColor(DesignSystemR.color.gray100,null)),
            span2Start,
            span2End,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.twPollInfo.text = spannableString
    }

    private fun checkCreateEnable() {
        val title = binding.editPollName.text.toString().trim()
        val first = binding.editPollFirst.text.toString().trim()
        val second = binding.editPollSecond.text.toString().trim()
        binding.btnCreate.isEnabled = title.isNotEmpty() && first.isNotEmpty() && second.isNotEmpty()
        binding.btnCreate.isSelected = title.isNotEmpty() && first.isNotEmpty() && second.isNotEmpty()
    }

    fun setCreatePoll(createClick: (String, String, String) -> Unit): CreatePollDialog {
        this.createClick = createClick
        return this
    }

}