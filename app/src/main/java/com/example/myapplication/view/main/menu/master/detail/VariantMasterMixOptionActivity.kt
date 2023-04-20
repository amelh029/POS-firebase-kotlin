package com.example.myapplication.view.main.menu.master.detail

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.example.myapplication.ActivityMessage
import com.example.myapplication.adapters.viewPager.ViewPagerAdapter
import com.example.myapplication.data.source.local.entity.room.master.Category
import com.example.myapplication.data.source.local.entity.room.master.Product
import com.example.myapplication.data.source.local.entity.room.master.Variant
import com.example.myapplication.databinding.ActivityMasterVariantMixOptionBinding
import com.example.myapplication.utils.tools.helper.FragmentWithTitle
import com.example.myapplication.view.viewModel.ProductViewModel
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class VariantMasterMixOptionActivity : ActivityMessage() {

    private lateinit var _binding: ActivityMasterVariantMixOptionBinding
    private lateinit var adapter: ViewPagerAdapter
    private lateinit var viewModel: ProductViewModel

    private var product: Product? = null
    private var variant: Variant? = null

    companion object {
        const val PRODUCT = "product"
        const val VARIANT = "variant"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMasterVariantMixOptionBinding.inflate(layoutInflater)
        setContentView(_binding.root)

        viewModel = ProductViewModel.getMainViewModel(this)

        product = intent.getSerializableExtra(PRODUCT) as Product?
        variant = intent.getSerializableExtra(VARIANT) as Variant?

        adapter = ViewPagerAdapter(this)
        _binding.vpVmo.adapter = adapter

        setPageAdapter()

        _binding.btnVmoBack.setOnClickListener { onBackPressed() }
    }

    private fun setPageAdapter() {
        lifecycleScope.launch {
            val query = Category.getFilter(Category.ACTIVE)
            viewModel.getCategories(query)
                .collect {
                    if (it.isNotEmpty()) {
                        val fragments: ArrayList<FragmentWithTitle> = ArrayList()
                        for (ctg in it) {
                            if (ctg.isStock) {
                                val fragment = ProductMasterMixVariantFragment(variant, ctg)
                                fragments.add(FragmentWithTitle(ctg.name, fragment))
                            }
                        }
                        setData(fragments)
                    }
                }
        }
    }

    private fun setData(fragments: ArrayList<FragmentWithTitle>) {
        adapter.setData(fragments)
        _binding.vpVmo.offscreenPageLimit = adapter.itemCount
        TabLayoutMediator(_binding.tabVmo, _binding.vpVmo) { tab, position ->
            tab.text = fragments[position].title
            _binding.vpVmo.setCurrentItem(tab.position, true)
        }.attach()
    }
}
