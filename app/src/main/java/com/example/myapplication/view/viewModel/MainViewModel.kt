package com.example.myapplication.view.viewModel

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.example.myapplication.data.source.domain.NewOutcome
import com.example.myapplication.data.source.local.entity.room.master.*
import com.example.myapplication.data.source.repository.*
import com.example.myapplication.utils.config.CashAmounts
import com.example.myapplication.utils.tools.helper.ReportsParameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel (
    private val paymentRepository: PaymentsRepository,
    private val supplierRepository: SuppliersRepository,
    private val customersRepository: CustomersRepository,
    private val outcomesRepository: OutcomesRepository,
    private val storeRepository: StoreRepository,
    private val settingRepository: SettingRepository,
    private val promosRepository: PromosRepository,
    private val newOutcome: NewOutcome
) : ViewModel() {

    companion object : ViewModelFromFactory<MainViewModel>() {
        fun getMainViewModel(activity: FragmentActivity): MainViewModel {
            return buildViewModel(activity, MainViewModel::class.java)
        }
    }

    private val _currentCashInput = MutableStateFlow(Pair(0L, 0L))

    @ExperimentalCoroutinesApi
    val cashSuggestions: Flow<List<Long>?> = _currentCashInput.flatMapLatest { input ->
        flow {
            if (input.first != 0L) {
                val cash = CashAmounts.generateCash(input.second).filter {
                    it.toString().startsWith(input.first.toString(), ignoreCase = true)
                            && it >= input.second
                }
                emit(cash)
            } else {
                emit(null)
            }
        }
    }

    fun addCashInput(cash: Long, total: Long) {
        _currentCashInput.value = Pair(cash, total)
    }

    private val customers = customersRepository.getCustomers()

    fun filterCustomer(keyword: String) = when {
        keyword.isNotEmpty() -> {
            customers
                .map {
                    it.filter { customer ->
                        customer.name.contains(keyword, ignoreCase = true)
                    }
                }
        }

        else -> customers
    }

    fun insertCustomers(data: Customer, onSaved: (id: Long) -> Unit) {
        viewModelScope.launch {
            val id = customersRepository.insertCustomer(data)
            onSaved(id)
        }
    }

    fun insertSupplier(data: Supplier) {
        viewModelScope.launch {
            supplierRepository.insertSupplier(data)
        }
    }

    fun updateSupplier(data: Supplier) {
        viewModelScope.launch {
            supplierRepository.updateSupplier(data)
        }
    }

    fun getPayments(query: SupportSQLiteQuery) = paymentRepository.getPayments(query)

    fun insertPayment(data: Payment) {
        viewModelScope.launch {
            paymentRepository.insertPayment(data)
        }
    }

    fun updatePayment(data: Payment) {
        viewModelScope.launch {
            paymentRepository.updatePayment(data)
        }
    }

    fun getPromos(status: Promo.Status): Flow<List<Promo>> {
        return promosRepository.getPromos(Promo.filter(status))
    }

    fun insertPromo(data: Promo) {
        viewModelScope.launch {
            promosRepository.insertPromo(data)
        }
    }

    fun updatePromo(data: Promo) {
        viewModelScope.launch {
            promosRepository.updatePromo(data)
        }
    }

    fun getOutcome(parameters: ReportsParameter) = outcomesRepository.getOutcomes(parameters)

    fun addNewOutcome(outcome: Outcome) {
        viewModelScope.launch {
            newOutcome(outcome)
        }
    }

    fun getStores() = storeRepository.getStores()

    suspend fun getStore(): Store? {
        val selected = settingRepository.getSelectedStore().first()
        return storeRepository.getStore(selected)
    }

    fun insertStore(store: Store) {
        viewModelScope.launch {
            storeRepository.insertStore(store)
        }
    }

    fun updateStore(store: Store) {
        viewModelScope.launch {
            storeRepository.updateStore(store)
        }
    }

    val selectedStore = settingRepository.getSelectedStore()

    fun selectStore(id: Long) {
        viewModelScope.launch {
            settingRepository.selectStore(id)
        }
    }

    val isDarkModeActive = settingRepository.getIsDarkModeActive()

    fun setDarkMode(isActive: Boolean) {
        viewModelScope.launch {
            settingRepository.setDarkMode(isActive)
        }
    }
}
