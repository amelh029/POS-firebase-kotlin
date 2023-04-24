package com.example.myapplication.view.store

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.example.myapplication.compose.GeneralMenusView
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.MasterMenus
import com.example.myapplication.view.ui.ModalContent
import com.example.myapplication.view.ui.StoreMenus
import com.example.myapplication.view.viewModel.OrderViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
fun MainStoreMenu(
    orderViewModel: OrderViewModel,
    currentDate: String,
    onGeneralMenuClicked: (menu: GeneralMenus) -> Unit,
    onMasterMenuClicked: (menu: MasterMenus) -> Unit,
    onStoreMenuClicked: (menu: StoreMenus) -> Unit
) {
    var modalContent by remember {
        mutableStateOf(ModalContent.MASTERS)
    }
    val modalState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = modalState,
        sheetShape = RoundedCornerShape(
            topStart = 8.dp,
            topEnd = 8.dp
        ),
        sheetContent = {
            when (modalContent) {
                ModalContent.MASTERS -> {
                    MasterMenusView(onMenuClicked = onMasterMenuClicked)
                }

                ModalContent.GENERAL_MENUS -> GeneralMenusView(
                    orderViewModel = orderViewModel,
                    date = currentDate,
                    onClicked = {
                        if (it == GeneralMenus.STORE) {
                            scope.launch {
                                modalState.hide()
                            }
                        } else {
                            onGeneralMenuClicked(it)
                        }
                    }
                )

                else -> {
                    // Do nothing
                }
            }
        },
        content = {
            StoreMenus(
                onGeneralMenuClicked = {
                    scope.launch {
                        modalContent = ModalContent.GENERAL_MENUS
                        modalState.show()
                    }
                },
                onStoreMenuClicked = {
                    if (it == StoreMenus.MASTERS) {
                        scope.launch {
                            modalContent = ModalContent.MASTERS
                            modalState.show()
                        }
                    } else {
                        onStoreMenuClicked(it)
                    }
                }
            )
        }
    )
}
