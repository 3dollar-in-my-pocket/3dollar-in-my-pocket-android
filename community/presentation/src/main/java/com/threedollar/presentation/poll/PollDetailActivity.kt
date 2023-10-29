package com.threedollar.presentation.poll

import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.threedollar.presentation.databinding.ActivityPollDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PollDetailActivity : ComponentActivity() {
    private lateinit var binding: ActivityPollDetailBinding
    private val viewModel: PollDetailViewModel by viewModels()

}