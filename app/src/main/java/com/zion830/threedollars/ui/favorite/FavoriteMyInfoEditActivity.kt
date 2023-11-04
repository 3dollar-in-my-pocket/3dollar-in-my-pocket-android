package com.zion830.threedollars.ui.favorite

import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.ActivityFavoriteMyInfoEditBinding
import com.zion830.threedollars.datasource.model.v2.request.FavoriteInfoRequest
import dagger.hilt.android.AndroidEntryPoint
import zion830.com.common.base.LegacyBaseActivity

@AndroidEntryPoint
class FavoriteMyInfoEditActivity :
    LegacyBaseActivity<ActivityFavoriteMyInfoEditBinding, FavoriteMyInfoEditViewModel>(R.layout.activity_favorite_my_info_edit) {
    override val viewModel: FavoriteMyInfoEditViewModel by viewModels()

    override fun initView() {
        initText()

        showKeyboard()

        changeText()

        binding.backImageView.setOnClickListener { finish() }

        binding.saveButton.setOnClickListener {
            val favoriteInfoRequest = FavoriteInfoRequest(
                introduction = binding.favoriteBodyEditTextView.text.toString(),
                name = binding.favoriteTitleEditTextView.text.toString()
            )
            viewModel.updateFavoriteInfo(favoriteInfoRequest = favoriteInfoRequest)
        }

        viewModel.isSuccess.observe(this) {
            if (it) {
                setResult(RESULT_OK)
                finish()
            }
        }
    }

    private fun changeText() {
        binding.favoriteTitleEditTextView.addTextChangedListener(object : TextWatcher {
            var previousString = ""
            override fun afterTextChanged(p0: Editable?) {
                if (binding.favoriteTitleEditTextView.lineCount > 2) {
                    binding.favoriteTitleEditTextView.setText(previousString)
                    binding.favoriteTitleEditTextView.setSelection(binding.favoriteTitleEditTextView.length())
                }
            }

            override fun beforeTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                previousString = text.toString()
            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.saveButton.isEnabled = true
                binding.favoriteTitleMaxLengthTextView.text = getString(R.string.favorite_title_max, text.length)
            }
        })
        binding.favoriteBodyEditTextView.addTextChangedListener(object : TextWatcher {
            var previousString = ""
            override fun afterTextChanged(p0: Editable?) {
                if (binding.favoriteBodyEditTextView.lineCount > 5) {
                    binding.favoriteBodyEditTextView.setText(previousString)
                    binding.favoriteBodyEditTextView.setSelection(binding.favoriteBodyEditTextView.length())
                }
            }

            override fun beforeTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                previousString = text.toString()
            }

            override fun onTextChanged(text: CharSequence, p1: Int, p2: Int, p3: Int) {
                binding.saveButton.isEnabled = true
                binding.favoriteBodyMaxLengthTextView.text = getString(R.string.favorite_body_max, text.length)
            }
        })
    }

    private fun initText() {
        val title = intent.getStringExtra(TITLE)
        val body = intent.getStringExtra(BODY)
        binding.favoriteTitleEditTextView.setText(title)
        binding.favoriteBodyEditTextView.setText(body)
        binding.favoriteTitleMaxLengthTextView.text = getString(R.string.favorite_title_max, title?.length)
        binding.favoriteBodyMaxLengthTextView.text = getString(R.string.favorite_body_max, body?.length)
    }

    private fun showKeyboard() {
        binding.favoriteTitleEditTextView.clearFocus()
        binding.favoriteTitleEditTextView.requestFocus()
        val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.favoriteTitleEditTextView, InputMethodManager.SHOW_IMPLICIT)
    }

    companion object {
        private const val TITLE = "title"
        private const val BODY = "body"

        fun getIntent(context: Context, title: String, body: String): Intent {
            val intent = Intent(context, FavoriteMyInfoEditActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(BODY, body)
            return intent
        }
    }
}