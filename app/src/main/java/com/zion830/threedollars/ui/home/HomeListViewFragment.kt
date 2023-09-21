package com.zion830.threedollars.ui.home

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.home.domain.data.store.ContentModel
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.listener.OnItemClickListener
import com.zion830.threedollars.GlobalApplication
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeListViewBinding
import com.zion830.threedollars.ui.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreListViewRecyclerAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeListViewFragment : BaseFragment<FragmentHomeListViewBinding, HomeViewModel>() {
    override val viewModel: HomeViewModel by activityViewModels()

    private var homeStoreType: HomeStoreType = HomeStoreType.ALL
    private var homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC
    private var isFilterCertifiedStores = false

    private val adapter: AroundStoreListViewRecyclerAdapter by lazy {
        AroundStoreListViewRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
                // TODO: 상세화면으로 이동
            }
        })
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeListViewBinding =
        FragmentHomeListViewBinding.inflate(inflater, container, false)

    override fun initView() {
        initViewModel()
        binding.listRecyclerView.adapter = adapter

        binding.mapViewTextView.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.allMenuTextView.setOnClickListener {
            showSelectCategoryDialog()
        }

        binding.filterTextView.setOnClickListener {
            homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) {
                HomeSortType.LATEST
            } else {
                HomeSortType.DISTANCE_ASC
            }
            viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
        }

        binding.bossFilterTextView.setOnClickListener {
            homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
            viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
        }

        binding.certifiedStoreTextView.setOnClickListener {
            isFilterCertifiedStores = !isFilterCertifiedStores
            val drawableStart = ContextCompat.getDrawable(requireContext(), if(isFilterCertifiedStores) R.drawable.ic_certification_check_on else R.drawable.ic_certification_check_off)
            binding.certifiedStoreTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
            getNearStore()
        }
    }

    private fun initViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch {
                    viewModel.selectCategory.collect {
                        val text = if (it.categoryId.isEmpty()) getString(R.string.fragment_home_all_menu) else it.name
                        val textColor = if (it.categoryId.isEmpty()) R.color.gray70 else R.color.pink
                        val background =
                            if (it.categoryId.isEmpty()) R.drawable.rect_white_radius10_stroke_gray30 else R.drawable.rect_white_radius10_stroke_black_fill_black

                        binding.run {
                            allMenuTextView.text = text
                            allMenuTextView.setTextColor(resources.getColor(textColor, null))
                            allMenuTextView.setBackgroundResource(background)
                            if (it.imageUrl.isEmpty()) {
                                allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(
                                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_category), null, null, null
                                )
                            } else {
                                loadImageUriIntoDrawable(it.imageUrl.toUri()) { drawable ->
                                    allMenuTextView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
                                }
                            }
                        }
                        getNearStore()
                    }
                }

                launch {
                    viewModel.aroundStoreModels.collect { adAndStoreItems ->
                        binding.listTitleTextView.text = viewModel.selectCategory.value.description.ifEmpty {
                            getString(R.string.fragment_home_all_menu)
                        }

                        adapter.submitList(adAndStoreItems.filterIsInstance<ContentModel>())
                        delay(200L)
                        binding.listRecyclerView.scrollToPosition(0)
                    }
                }
                launch {
                    viewModel.homeFilterEvent.collect {
                        getNearStore()

                        val textColor = resources.getColor(if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.color.gray70 else R.color.gray40, null)
                        val drawableStart = ContextCompat.getDrawable(
                            requireContext(),
                            if (it.homeStoreType == HomeStoreType.BOSS_STORE) R.drawable.ic_check_gray_16 else R.drawable.ic_uncheck
                        )
                        binding.run {
                            bossFilterTextView.setTextColor(textColor)
                            bossFilterTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
                            filterTextView.text = if (it.homeSortType == HomeSortType.DISTANCE_ASC) {
                                getString(R.string.fragment_home_filter_latest)
                            } else {
                                getString(R.string.fragment_home_filter_distance)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun showSelectCategoryDialog() {
        val dialog = SelectCategoryDialogFragment()
        dialog.show(parentFragmentManager, "")
    }

    private fun getNearStore() {
        viewModel.currentLocation.value?.let {
            viewModel.requestHomeItem(location = it, filterCertifiedStores = isFilterCertifiedStores)
        }
    }

    private fun loadImageUriIntoDrawable(imageUri: Uri, callback: (Drawable?) -> Unit) {
        Glide.with(requireContext())
            .load(imageUri)
            .override(64)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    callback(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    callback(null)
                }
            })
    }
}