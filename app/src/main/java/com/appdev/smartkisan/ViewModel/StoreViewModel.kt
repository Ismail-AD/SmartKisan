package com.appdev.smartkisan.ViewModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appdev.smartkisan.Actions.SellerStoreActions
import com.appdev.smartkisan.Repository.Repository
import com.appdev.smartkisan.States.ProductState
import com.appdev.smartkisan.States.UserAuthState
import com.appdev.smartkisan.Utils.ResultState
import com.appdev.smartkisan.data.Product
import com.appdev.smartkisan.data.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StoreViewModel @Inject constructor(
    val repository: Repository,
) : ViewModel() {

    var productState by mutableStateOf(ProductState())
        private set


    fun onAction(action: SellerStoreActions) {
        when (action) {
            SellerStoreActions.GoBack -> {
            }

            is SellerStoreActions.ProductNameUpdated -> {
                productState = productState.copy(productName = action.productName)
            }

            is SellerStoreActions.PriceUpdated -> {
                productState = productState.copy(price = action.price)
            }

            is SellerStoreActions.DescriptionUpdated -> {
                productState = productState.copy(description = action.description)
            }

            is SellerStoreActions.WeightUpdated -> {
                productState = productState.copy(weight = action.weight)
            }

            is SellerStoreActions.MeasurementUpdated -> {
                productState = productState.copy(measurement = action.measurement)
            }

            is SellerStoreActions.SelectedImageUri -> {
                val currentUris = productState.imageUris.toMutableList()
                if (action.index < currentUris.size) {
                    currentUris[action.index] = action.uri
                } else {
                    currentUris.add(action.uri)
                }
                productState = productState.copy(imageUris = currentUris)
            }

            is SellerStoreActions.QuantityUpdated -> {
                productState = productState.copy(quantity = action.quantity)

            }

            is SellerStoreActions.AddToStore -> {
                viewModelScope.launch {
                    repository.addProduct(
                        Product(
                            name = productState.productName,
                            price = productState.price,
                            description = productState.description,
                            quantity = productState.quantity,
                            weightOrVolume = productState.weight,
                            unit = productState.measurement
                        ),
                        imageByteArrays = action.listOfImageByteArrays,
                        imageUris = productState.imageUris
                    ).collect { result ->
                        productState = when (result) {
                            is ResultState.Failure -> productState.copy(
                                errorMessage = result.msg.localizedMessage,
                                isLoading = false
                            )

                            ResultState.Loading -> productState.copy(isLoading = true)
                            is ResultState.Success -> {
                                productState.copy(uploaded = true, isLoading = false)
                            }
                        }
                    }
                }
            }
        }
    }
}