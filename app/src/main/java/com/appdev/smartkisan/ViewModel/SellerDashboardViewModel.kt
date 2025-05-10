package com.appdev.smartkisan.ViewModel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.SellerDashboardState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.Utils.SessionManagement
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.data.statusCard
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SellerDashboardViewModel @Inject constructor(
    private val repository: Repository,
    val sessionManagement: SessionManagement
) : ViewModel() {

    private val _uiState = MutableStateFlow(SellerDashboardState())
    val uiState: StateFlow<SellerDashboardState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun setUserData() {
        _uiState.value = _uiState.value.copy(userName = getUserName(), userImage = getProfileImage())
    }


    fun loadDashboardData() {
        fetchProducts()
        generateStatusCards()
    }

    fun getUserName():String{
        return sessionManagement.getUserName() ?:""
    }
    fun getProfileImage():String{
        return sessionManagement.getUserImage() ?:""
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.getRecentProducts().collect { result ->
                when (result) {
                    is ResultState.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is ResultState.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                products = result.data,
                                errorMessage = null
                            )
                        }
                        updateAnalytics(result.data)
                    }
                    is ResultState.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = result.msg.localizedMessage ?: "Failed to load products"
                            )
                        }
                    }
                }
            }
        }
    }

    private fun updateAnalytics(products: List<Product>) {
        val totalProducts = products.size.toLong()

        // Update status cards with real data
        _uiState.update { currentState ->
            val updatedCards = currentState.statusCards.map { card ->
                when (card.name) {
                    "Products" -> card.copy(counter = totalProducts)
                    else -> card
                }
            }
            currentState.copy(statusCards = updatedCards)
        }
    }

    private fun generateStatusCards() {
        val statusCards = listOf(
            statusCard(name = "Products", counter = 0L)
        )

        _uiState.update { it.copy(statusCards = statusCards) }
    }
}