package com.zion830.threedollars.ui.home.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.IntentCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.threedollar.common.analytics.LogManager
import com.threedollar.common.analytics.ParameterName
import com.threedollar.common.analytics.ScreenName
import com.threedollar.common.base.BaseFragment
import com.threedollar.common.data.AdAndStoreItem
import com.threedollar.common.data.AdMobItem
import com.threedollar.common.listener.OnItemClickListener
import com.threedollar.common.serverdriven.model.SDLinkModel
import com.threedollar.common.utils.Constants
import com.threedollar.domain.home.data.advertisement.AdvertisementModelV2
import com.threedollar.domain.home.data.store.ContentModel
import com.threedollar.domain.home.data.store.UserStoreModel
import com.zion830.threedollars.DynamicLinkActivity
import com.zion830.threedollars.core.designsystem.R as DesignSystemR
import com.zion830.threedollars.databinding.FragmentHomeListViewBinding
import com.zion830.threedollars.ui.dialog.category.SelectCategoryDialogFragment
import com.zion830.threedollars.ui.home.adapter.AroundStoreListViewRecyclerAdapter
import com.zion830.threedollars.ui.home.ui.compose.HomeFilterChipsRow
import com.zion830.threedollars.ui.home.viewModel.HomeViewModel
import com.zion830.threedollars.ui.storeDetail.boss.ui.BossStoreDetailActivity
import com.zion830.threedollars.ui.storeDetail.user.ui.StoreDetailActivity
import com.zion830.threedollars.utils.showToast
import dagger.hilt.android.AndroidEntryPoint
import base.compose.AppTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import zion830.com.common.base.onSingleClick
import com.threedollar.common.R as CommonR

@AndroidEntryPoint
class HomeListViewFragment : BaseFragment<FragmentHomeListViewBinding, HomeViewModel>() {

    override val viewModel: HomeViewModel by activityViewModels()

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
        initFilterComposeView()
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.listRecyclerView.adapter = adapter
    }

    private fun initFilterComposeView() {
        binding.filterComposeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        binding.filterComposeView.setContent {
            AppTheme {
                val cells = viewModel.filterCells.collectAsStateWithLifecycle().value
                HomeFilterChipsRow(
                    cells = cells,
                    contentPadding = PaddingValues(horizontal = 22.dp),
                    onCategoryClick = {
                        viewModel.sendClickCategoryFilter()
                        showSelectCategoryDialog()
                    },
                    onRadioClick = viewModel::selectRadioOption,
                    onActionClick = viewModel::handleActionLink,
                    onCloseSelectedCategoryClick = viewModel::closeSelectedCategory,
                )
            }
        }
    }

    override fun sendPageView(screen: ScreenName, extraParameters: Map<ParameterName, Any>) {
        LogManager.sendPageView(ScreenName.HOME_LIST, this::class.simpleName.toString())
    }

    private fun initButtons() {
        binding.apply {
            mapViewTextView.onSingleClick { navigateBack() }
            certifiedStoreTextView.onSingleClick { onCertifiedStoreFilterClick() }
        }
    }

    private fun navigateBack() {
        view?.findNavController()?.popBackStack()
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
        viewModel.updateHomeFilterEvent(filterCertifiedStores = isFilterCertifiedStores)
    }

    private fun initFlows() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.CREATED) {
                launch { collectAroundStoreModelsFlow() }
                launch { collectFilterDeepLink() }
                launch { collectServerErrorFlow() }
            }
        }
    }

    private suspend fun collectFilterDeepLink() {
        viewModel.filterDeepLink.collect { link ->
            handleFilterDeepLink(link)
        }
    }

    private fun handleFilterDeepLink(link: SDLinkModel) {
        val url = link.link
        if (url.isBlank()) return
        if (link.type == "APP_SCHEME") {
            startActivity(
                Intent(requireContext(), DynamicLinkActivity::class.java).apply {
                    putExtra("link", url)
                }
            )
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
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
        SelectCategoryDialogFragment
            .newInstance(
                latLng = viewModel.uiState.value.userLocation
            )
            .show(parentFragmentManager, SelectCategoryDialogFragment.TAG)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Constants.SHOW_STORE_BY_CATEGORY) {
            if (resultCode == android.app.Activity.RESULT_OK) {
                val resultData = data
                if (resultData?.getBooleanExtra(StoreDetailActivity.EXTRA_IS_UPDATED, false) == true) {
                    val userStore = IntentCompat.getSerializableExtra(resultData, StoreDetailActivity.EXTRA_USER_STORE, UserStoreModel::class.java)
                    userStore?.let { viewModel.updateStoreItem(it) }
                }
            }
            viewModel.refreshHomeListSectionAfterStoreUpdate()
        }
    }
}
