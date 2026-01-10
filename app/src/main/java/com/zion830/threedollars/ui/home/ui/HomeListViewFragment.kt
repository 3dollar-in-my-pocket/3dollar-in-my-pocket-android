package com.zion830.threedollars.ui.home.ui

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
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
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.data.AdMobItem
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.utils.Constants
import com.threedollar.common.utils.SharedPrefUtils
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.CategoryModel
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.threedollar.domain.home.request.FilterConditionsTypeModel
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.FragmentHomeListViewBinding
import com.zion830.threedollars.ui.dialog.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.dialog.category.StoreCategoryItem
import com.zion830.threedollars.ui.home.adapter.AroundStoreListViewRecyclerAdapter
import com.zion830.threedollars.ui.home.data.HomeSortType
import com.zion830.threedollars.ui.home.data.HomeStoreType
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import javax.inject.Inject
import com.threedollar.common.R as CommonR

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
        AroundStoreListViewRecyclerAdapter(
            clickListener = getStoreItemClickListener(),
            clickAdListener = getAdvertisementClickListener()
        )
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentHomeListViewBinding =
        FragmentHomeListViewBinding.inflate(inflater, container, false)

    override fun initView() {
        initFlows()
        initButtons()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()
        binding.listRecyclerView.adapter = adapter
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.HOME_LIST, this::class.simpleName.toString())
    }

    private fun initButtons() {
        binding.apply {
            mapViewTextView.onSingleClick { navigateBack() }
            allMenuTextView.onSingleClick { onCategoryFilterClick() }
            filterTextView.onSingleClick { onSortFilterClick() }
            filterConditionsTextView.onSingleClick { onRecentActivityFilterClick() }
            bossFilterTextView.onSingleClick { onBossFilterClick() }
            certifiedStoreTextView.onSingleClick { onCertifiedStoreFilterClick() }
        }
    }
    private fun navigateBack() {
        view?.findNavController()?.popBackStack()
    }

    private fun onCategoryFilterClick() {
        viewModel.sendClickCategoryFilterInList()
        showSelectCategoryDialog()
    }

    private fun onSortFilterClick() {
        homeSortType = if (homeSortType == HomeSortType.DISTANCE_ASC) HomeSortType.LATEST else HomeSortType.DISTANCE_ASC
        viewModel.sendClickSortingInList(homeSortType.name)
        viewModel.updateHomeFilterEvent(homeSortType = homeSortType)
    }

    private fun onRecentActivityFilterClick() {
        sharedPrefUtils.setIsClickFilterConditions()
        binding.filterConditionsSpeechBubbleLayout.isVisible = !sharedPrefUtils.getIsClickFilterConditions()
        filterConditionsType = if (filterConditionsType.isEmpty()) listOf(FilterConditionsTypeModel.RECENT_ACTIVITY) else listOf()
        viewModel.sendClickRecentActivityFilterInList(filterConditionsType.contains(FilterConditionsTypeModel.RECENT_ACTIVITY))
        viewModel.updateHomeFilterEvent(filterConditionsType = filterConditionsType)
    }

    private fun onBossFilterClick() {
        homeStoreType = if (homeStoreType == HomeStoreType.ALL) HomeStoreType.BOSS_STORE else HomeStoreType.ALL
        viewModel.sendClickBossFilterInList(homeStoreType == HomeStoreType.BOSS_STORE)
        viewModel.updateHomeFilterEvent(homeStoreType = homeStoreType)
    }

    private fun onCertifiedStoreFilterClick() {
        isFilterCertifiedStores = !isFilterCertifiedStores
        viewModel.sendClickOnlyVisitInList(isFilterCertifiedStores)
        binding.certifiedStoreTextView.setCompoundDrawablesWithIntrinsicBounds(
            ContextCompat.getDrawable(
                requireContext(),
                if (isFilterCertifiedStores) DesignSystemR.drawable.ic_certification_check_on else DesignSystemR.drawable.ic_certification_check_off
            ), null, null, null
        )
        viewModel.fetchAroundStores()
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch { collectCategoryFlow() }
                launch { collectAroundStoreModelsFlow() }
                launch { collectSortType() }
                launch { collectStoreType() }
                launch { collectFilterConditionsType() }
                launch { collectServerErrorFlow() }
            }
        }
    }

    private suspend fun collectCategoryFlow() {
        viewModel.uiState
            .mapNotNull { it.selectedCategory }
            .collect {
                updateCategoryView(it)
                viewModel.fetchAroundStores()
            }
    }

    private fun updateCategoryView(category: StoreCategoryItem) {
        val (text, textColor, background) = getCategoryViewAttributes(category)
        binding.allMenuTextView.apply {
            this.text = text
            setTextColor(resources.getColor(textColor, null))
            setBackgroundResource(background)
            loadImageUriIntoDrawable(category.imageUrl.toUri()) { drawable ->
                setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
            }
        }
    }

    private fun getCategoryViewAttributes(category: StoreCategoryItem): Triple<String, Int, Int> {
        return if (category.id.isEmpty()) {
            Triple(
                getString(CommonR.string.fragment_home_all_menu),
                DesignSystemR.color.gray70,
                DesignSystemR.drawable.rect_white_radius10_stroke_gray30
            )
        } else {
            Triple(
                category.name,
                DesignSystemR.color.pink,
                DesignSystemR.drawable.rect_white_radius10_stroke_black_fill_black
            )
        }
    }

    private suspend fun collectAroundStoreModelsFlow() {
        viewModel.carouselUpdate.collect { adAndStoreItems ->
            val shouldResetScroll = viewModel.consumeShouldResetScroll()
            binding.listTitleTextView.text =
                viewModel.uiState.value.selectedCategory?.description?.ifEmpty {
                    getString(CommonR.string.fragment_home_all_menu)
                }
            val resultList = mutableListOf<AdAndStoreItem>().apply {
                add(AdMobItem)
                addAll(adAndStoreItems)
                viewModel.advertisementListModel.value?.let { add(2, it) }
            }
            adapter.submitList(resultList)
            if (shouldResetScroll) {
                delay(200L)
                binding.listRecyclerView.scrollToPosition(0)
            }
        }
    }

    private suspend fun collectSortType() {
        viewModel.uiState
            .map { it.homeSortType }
            .collect {
                binding.apply {
                    filterTextView.text = if (it == HomeSortType.DISTANCE_ASC) {
                        getString(CommonR.string.fragment_home_filter_distance)
                    } else {
                        getString(CommonR.string.fragment_home_filter_latest)
                    }
                }
            }
    }

    private suspend fun collectStoreType() {
        viewModel.uiState
            .map { it.homeStoreType }
            .collect {
                binding.apply {
                    bossFilterTextView.apply {
                        setTextColor(resources.getColor(if (it == HomeStoreType.BOSS_STORE) DesignSystemR.color.pink else DesignSystemR.color.gray40, null))
                        setBackgroundResource(if (it == HomeStoreType.BOSS_STORE) DesignSystemR.drawable.rect_radius10_pink100_stroke_pink else DesignSystemR.drawable.rect_white_radius10_stroke_gray30)
                    }

                    certifiedStoreTextView.isVisible = it != HomeStoreType.BOSS_STORE
                }
            }
    }

    private suspend fun collectFilterConditionsType() {
        viewModel.uiState
            .map { it.filterConditionsType }
            .collect {
                binding.apply {
                    filterConditionsTextView.apply {
                        setTextColor(
                            resources.getColor(
                                if (it.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)) DesignSystemR.color.pink else DesignSystemR.color.gray40,
                                null
                            )
                        )
                        setBackgroundResource(if (it.contains(FilterConditionsTypeModel.RECENT_ACTIVITY)) DesignSystemR.drawable.rect_radius10_pink100_stroke_pink else DesignSystemR.drawable.rect_white_radius10_stroke_gray30)
                    }
                }
            }
    }

    private suspend fun collectServerErrorFlow() {
        viewModel.serverError.collect {
            it?.let { showToast(it) }
        }
    }

    private fun getStoreItemClickListener() = object : OnItemClickListener<ContentModel> {
        override fun onClick(item: ContentModel) {
            viewModel.sendClickStoreInList(item.storeModel.storeId, item.storeModel.storeType)
            val intent = if (item.storeModel.storeType == Constants.BOSS_STORE) {
                BossStoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId)
            } else {
                StoreDetailActivity.getIntent(requireContext(), item.storeModel.storeId.toInt(), false)
            }
            startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
        }
    }

    private fun getAdvertisementClickListener() = object : OnItemClickListener<AdvertisementModelV2> {
        override fun onClick(item: AdvertisementModelV2) {
            viewModel.sendClickAdvertisementInList(item.advertisementId.toString())
            val intent = if (item.link.type == "APP_SCHEME") {
                Intent(requireContext(), DynamicLinkActivity::class.java).apply { putExtra("link", item.link.url) }
            } else {
                Intent(Intent.ACTION_VIEW, Uri.parse(item.link.url))
            }
            startActivity(intent)
        }
    }

    private fun showSelectCategoryDialog() {
        SelectCategoryDialogFragment().show(parentFragmentManager, "")
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SHOW_STORE_BY_CATEGORY && resultCode == android.app.Activity.RESULT_OK) {
            val isUpdated = data?.getBooleanExtra(StoreDetailActivity.EXTRA_IS_UPDATED, false) ?: false
            if (isUpdated && data != null) {
                val userStore = IntentCompat.getSerializableExtra(data, StoreDetailActivity.EXTRA_USER_STORE, UserStoreModel::class.java)
                userStore?.let { viewModel.updateStoreItem(it) }
            }
        }
    }
}
