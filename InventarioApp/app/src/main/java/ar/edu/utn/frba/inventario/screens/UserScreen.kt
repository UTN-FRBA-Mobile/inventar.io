package ar.edu.utn.frba.inventario.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import ar.edu.utn.frba.inventario.api.model.self.UserResponse
import ar.edu.utn.frba.inventario.utils.Screen
import ar.edu.utn.frba.inventario.viewmodels.UserScreenViewModel

@Composable
fun UserScreen(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel = hiltViewModel()
) {
    UserBodyContent(navController, userScreenViewModel)
}

@Composable
fun UserBodyContent(
    navController: NavController,
    userScreenViewModel: UserScreenViewModel
) {
    val user by userScreenViewModel.user.collectAsState()

    LaunchedEffect(Unit) {
        userScreenViewModel.getUser()
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("User")
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { userScreenViewModel.getUser() }) {
            Text("Test get user")
        }
        Button(onClick = {
            userScreenViewModel.doLogout()
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true } // clears entire back stack
                launchSingleTop = true
            }
        }) {
            Text("Logout")
        }
        Text("parte de santi")
        if (user == null) {
            CircularProgressIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(50.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    /*Image(
                        painter = painterResource(id = user.imageRes),
                        contentDescription = "Imagen del usuario",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                    )*/
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = user.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "@${user.username}",
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    }

                }
                Card (
                    modifier = Modifier
                        .padding(20.dp)

                ){
                    /*Column (
                        modifier = Modifier
                            .padding(20.dp)
                    ){
                        Text(text = "Ubicaciones disponibles", fontSize = 20.sp, fontWeight = FontWeight.Medium)
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(user.locations.size){ index ->
                                Text(
                                    text = "• ${user.locations.get(index)}",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )
                            }
                        }
                    }*/


                }
            }
        }

    }
}

@Preview
@Composable
fun UserScreenPreview(){
    UserScreen(navController = rememberNavController())
}