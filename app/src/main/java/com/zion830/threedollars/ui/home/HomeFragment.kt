package com.zion830.threedollars.ui.home

import android.content.Intent
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearSnapHelper
import com.zion830.threedollars.Constants
import com.zion830.threedollars.R
import com.zion830.threedollars.databinding.FragmentHomeBinding
import com.zion830.threedollars.repository.model.MenuType
import com.zion830.threedollars.repository.model.response.AllStoreResponseItem
import com.zion830.threedollars.ui.addstore.NearStoreNaverMapFragment
import com.zion830.threedollars.ui.home.adapter.NearStoreRecyclerAdapter
import com.zion830.threedollars.ui.store_detail.StoreByMenuActivity
import com.zion830.threedollars.ui.store_detail.StoreDetailActivity
import zion830.com.common.base.BaseFragment
import zion830.com.common.listener.OnItemClickListener
import zion830.com.common.listener.OnSnapPositionChangeListener
import zion830.com.common.listener.SnapOnScrollListener


class HomeFragment : BaseFragment<FragmentHomeBinding, HomeViewModel>(R.layout.fragment_home) {

    override val viewModel: HomeViewModel by activityViewModels()

    private lateinit var adapter: NearStoreRecyclerAdapter

    private lateinit var naverMapFragment: NearStoreNaverMapFragment

    override fun initView() {
        naverMapFragment = NearStoreNaverMapFragment()
        childFragmentManager.beginTransaction().replace(R.id.container, naverMapFragment).commit()

        viewModel.nearStoreInfo.observe(viewLifecycleOwner) { store ->
            adapter.submitList(store)
        }

        // 주변 음식점 리스트
        adapter = NearStoreRecyclerAdapter(object : OnItemClickListener<AllStoreResponseItem> {
            override fun onClick(item: AllStoreResponseItem) {
                val intent = StoreDetailActivity.getIntent(requireContext(), item.id)
                startActivityForResult(intent, Constants.SHOW_STORE_BY_CATEGORY)
            }
        })

        binding.rvStore.adapter = adapter
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvStore)
        binding.rvStore.addOnScrollListener(
            SnapOnScrollListener(
                snapHelper,
                onSnapPositionChangeListener = object : OnSnapPositionChangeListener {
                    override fun onSnapPositionChange(position: Int) {
                        if (position >= 0) {
                            adapter.focusedIndex = position
                            adapter.notifyDataSetChanged()
                            naverMapFragment.moveCameraWithAnim(adapter.getItemLocation(position))
                        }
                    }
                })
        )

        // 상단 버튼바
        binding.btnMenu1.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.BUNGEOPPANG.key))
        }
        binding.btnMenu2.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.TAKOYAKI.key))
        }
        binding.btnMenu3.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.GYERANPPANG.key))
        }
        binding.btnMenu4.setOnClickListener {
            startActivity(StoreByMenuActivity.getIntent(requireContext(), MenuType.HOTTEOK.key))
        }

        // 3천원 글자 두껍게 바꾸기
        binding.tvMsg2.text = buildSpannedString {
            append(getString(R.string.if_you_have_money1))
            bold { append(getString(R.string.if_you_have_money2)) }
            append(getString(R.string.if_you_have_money3))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        naverMapFragment.onActivityResult(requestCode, resultCode, data)
    }
}