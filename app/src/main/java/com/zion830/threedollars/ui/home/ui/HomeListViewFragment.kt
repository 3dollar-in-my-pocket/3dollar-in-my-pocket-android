package com.zion830.threedollars.ui.home.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.home.domain.data.advertisement.AdvertisementModelV2
import com.home.domain.data.store.ContentModel
import com.home.domain.request.FilterConditionsTypeModel
import com.home.presentation.data.HomeSortType
import com.home.presentation.data.HomeStoreType
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.Constants.CLICK_STORE
import com.threedollar.common.utils.SharedPrefUtils
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.EventTracker
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeListViewBinding
import com.zion830.threedollars.ui.dialog.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreListViewRecyclerAdapter
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.NaverMapUtils.DEFAULT_DISTANCE_M
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HomeListViewFragment : BaseFragment<FragmentHomeListViewBinding, HomeViewModel>() {

    @Inject
    lateinit var sharedPrefUtils: SharedPrefUtils

    override val viewModel: HomeViewModel by activityViewModels()

    private var homeStoreType: HomeStoreType = HomeStoreType.ALL
    private var homeSortType: HomeSortType = HomeSortType.DISTANCE_ASC
    private var filterConditionsType: List<FilterConditionsTypeModel> = listOf()

    private var isFilterCertifiedStores = false

    private val adapter: AroundStoreListViewRecyclerAdapter by lazy {
        AroundStoreListViewRecyclerAdapter(object : OnItemClickListener<ContentModel> {
            override fun onClick(item: ContentModel) {
                val bundle = Bundle().apply {
                    putString("screen", "home_list")
                    putString("store_id", item.storeModel.storeId)
                    putString("type", item.storeModel.storeType)
                }
                EventTracker.logEvent(CLICK_STORE, bundle)
                if (item.storeModel.storeType == Constants.BOSS_STORE) {
                    val intent =
                        BossStoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                } else {
                    val intent =
                        StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), false)
                    startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
                }
            }
        }, object : OnItemClickListener<AdvertisementModelV2> {
            override fun onClick(item: AdvertisementModelV2) {
                val bundle = Bundle().apply {
                    putString("screen", "home_list")
                    putString("advertisement_id", item.advertisementId.toString())
                }
                EventTracker.logEvent(Constants.CLICK_AD_BANNER, bundle)
                if (item.link.type == "APP_SCHEME") {
                    startActivity(
                        Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                            putExtra("link", item.link.url)
                        },
                    )
                } else {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(item.link.url)))
                }
            }
        })
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeListViewBinding =
        FragmentHomeListViewBinding.inflate(inflater, container, false)

    override fun initView() {
        initAdmob()
        initFlow()
        initButton()
        binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()
        binding.listRecyclerView.adapter = adapter

    }

    private fun initAdmob() {
        val latLng = viewModel.currentLocationFlow.value
        if (latLng.isValid) {
            viewModel.getAdvertisementList(latLng = latLng)
        }
        val adRequest = AdRequest.Builder().build()
        binding.admob.loadAd(adRequest)
    }

    override fun initFirebaseAnalytics() {
        setFirebaseAnalyticsLogEvent(className = "HomeListViewFragment", screenName = "home_list")
    }

    private fun initButton() {
        binding.mapViewTextView.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.allMenuTextView.setOnClickListener {
            val bundle = Bundle().apply {
                putString("screen", "home_list")
            }
            EventTracker.logEvent(Constants.CLICK_CATEGORY_FILTER, bundle)
            showSelectCategoryDialog()
        }

        binding.filterTextView.setOnClickListener {
            homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) {
                HomeSortType.LATEST
            } else {
                HomeSortType.DISTANCE_ASC
            }
            val bundle = Bundle().apply {
                putString("screen", "home_list")
                putString("type", homeSortType.name)
            }
            EventTracker.logEvent(Constants.CLICK_SORTING, bundle)
            viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
        }

        binding.filterConditionsTextView.setOnClickListener {
            sharedPrefUtils.setIsClickFilterConditions()
            binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()
            filterConditionsType = if (filterConditionsType.isEmpty()) {
                listOf(FilterConditionsTypeModel.RECENT_ACTIVITY)
            } else {
                listOf()
            }
            val bundle = Bundle().apply {
                putString("screen", "home_list")
                putBoolean("value", filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY))
            }
            EventTracker.logEvent(Constants.CLICK_RECENT_ACTIVITY_FILTER, bundle)

            viewModel.updateHomeFilterEvent(filterConditionsType = filterConditionsType)
        }

        binding.bossFilterTextView.setOnClickListener {
            homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
            val bundle = Bundle().apply {
                putString("screen", "home_list")
                putString("value", if (homeStoreType == HomeStoreType.BOSS_STORE) "on" else "off")
            }
            EventTracker.logEvent(Constants.CLICK_BOSS_FILTER, bundle)
            viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
        }

        binding.certifiedStoreTextView.setOnClickListener {
            isFilterCertifiedStores = !isFilterCertifiedStores
            val bundle = Bundle().apply {
                putString("screen", "home_list")
                putString("value", if (isFilterCertifiedStores) "true" else "false")
            }
            EventTracker.logEvent(Constants.CLICK_ONLY_VISIT, bundle)
            val drawableStart = ContextCompat.getDrawable(
                requireContext(),
                if (isFilterCertifiedStores) R.drawable.ic_certification_check_on else R.drawable.ic_certification_check_off
            )
            binding.certifiedStoreTextView.setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
        }
    }

    private fun initFlow() {
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
                        val resultList = mutableListOf<AdAndStoreItem>()
                        resultList.addAll(adAndStoreItems)
                        viewModel.advertisementListModel.value?.apply {
                            resultList.add(1, this)
                        }
                        adapter.submitList(resultList)
                        delay(200L)
                        binding.listRecyclerView.scrollToPosition(0)
                    }
                }
                launch {
                    viewModel.homeFilterEvent.collect {
                        getNearStore()

                        binding.run {
                            if (it.filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)) {
                                filterConditionsTextView.setTextColor(resources.getColor(R.color.pink, null))
                                filterConditionsTextView.setBackgroundResource(R.drawable.rect_radius10_pink100_stroke_pink)
                            } else {
                                filterConditionsTextView.setTextColor(resources.getColor(R.color.gray40, null))
                                filterConditionsTextView.setBackgroundResource(R.drawable.rect_white_radius10_stroke_gray30)
                            }
                            if (it.homeStoreType == HomeStoreType.BOSS_STORE) {
                                bossFilterTextView.setTextColor(resources.getColor(R.color.pink, null))
                                bossFilterTextView.setBackgroundResource(R.drawable.rect_radius10_pink100_stroke_pink)
                            } else {
                                bossFilterTextView.setTextColor(resources.getColor(R.color.gray40, null))
                                bossFilterTextView.setBackgroundResource(R.drawable.rect_white_radius10_stroke_gray30)
                            }
                            filterTextView.text = if (it.homeSortType == HomeSortType.DISTANCE_ASC) {
                                getString(R.string.fragment_home_filter_distance)
                            } else {
                                getString(R.string.fragment_home_filter_latest)
                            }
                            certifiedStoreTextView.isVisible = it.homeStoreType != HomeStoreType.BOSS_STORE
                        }
                    }
                }
                launch {
                    viewModel.serverError.collect {
                        it?.let {
                            showToast(it)
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
            viewModel.requestHomeItem(
                location = it,
                distanceM = viewModel.currentDistanceM.value ?: DEFAULT_DISTANCE_M,
                filterCertifiedStores = isFilterCertifiedStores
            )
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