package com.example.myapplication.view.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.myapplication.compose.GeneralMenuButtonView
import com.example.myapplication.compose.GeneralMenusView
import com.example.myapplication.view.ui.GeneralMenus
import com.example.myapplication.view.ui.ModalContent
import com.example.myapplication.view.ui.SettingMenus
import com.example.myapplication.view.viewModel.MainViewModel
import com.example.myapplication.view.viewModel.OrderViewModel
import kotlinx.coroutines.launch


@Composable
@ExperimentalMaterialApi
fun SettingsMainMenu(
    orderViewModel: OrderViewModel,
    mainViewModel: MainViewModel,
    currentDate: String,
    onGeneralMenuClicked: (menu: GeneralMenus) -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    var modalContent by remember {
        mutableStateOf(ModalContent.GENERAL_MENUS)
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
                ModalContent.GENERAL_MENUS -> GeneralMenusView(
                    orderViewModel = orderViewModel,
                    date = currentDate,
                    onClicked = {
                        if (it == GeneralMenus.SETTING) {
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
            SettingsMenus(
                mainViewModel = mainViewModel,
                onGeneralMenuClicked = {
                    scope.launch {
                        modalContent = ModalContent.GENERAL_MENUS
                        modalState.show()
                    }
                },
                onDarkModeChange = onDarkModeChange
            )
        }
    )
}

@Composable
fun SettingsMenus(
    mainViewModel: MainViewModel,
    onGeneralMenuClicked: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.background)
    ) {
        LazyColumn(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
        ) {
            items(SettingMenus.values()) {
                when (it) {
                    SettingMenus.THEME -> ThemeSettingMenu(mainViewModel, onDarkModeChange)
                }
            }
        }
        GeneralMenuButtonView(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            onMenuClicked = onGeneralMenuClicked
        )
    }
}

@Composable
fun ThemeSettingMenu(
    mainViewModel: MainViewModel,
    onDarkModeChange: (Boolean) -> Unit
) {

    val isDarkMode = mainViewModel.isDarkModeActive.collectAsState(initial = false)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colors.surface
            )
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(16.dp),
            text = stringResource(id = SettingMenus.THEME.title),
            style = MaterialTheme.typography.body2.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colors.onSurface
        )
        Switch(
            checked = isDarkMode.value,
            onCheckedChange = {
                mainViewModel.setDarkMode(it)
                onDarkModeChange(it)
            }
        )
    }
    Spacer(modifier = Modifier.height(4.dp))
}
