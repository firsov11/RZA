package com.firsov.rza.ui.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.firsov.rza.ui.screens.DocumentScreen
import com.firsov.rza.ui.screens.FileListScreen
import com.firsov.rza.viewmodel.DocxViewModel

@Composable
fun DocxNavGraph() {
    val navController = rememberNavController()
    val vm: DocxViewModel = hiltViewModel()

    NavHost(navController, startDestination = "files") {

        composable("files") {
            FileListScreen(
                vm = vm,
                onOpen = { name ->
                    navController.navigate("doc/$name")
                }
            )
        }

        composable(
            "doc/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStack ->
            val name = backStack.arguments?.getString("name")!!
            DocumentScreen(
                vm = vm,
                filename = name,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
