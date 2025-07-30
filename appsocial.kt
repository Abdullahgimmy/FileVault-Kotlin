package com.example.realtagram

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.realtagram.ui.navigation.AppNavGraph
import com.example.realtagram.ui.theme.RealtagramTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RealtagramTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
package com.example.realtagram.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.realtagram.ui.screens.HomeScreen
import com.example.realtagram.ui.screens.UploadScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { HomeScreen(navController) }
        composable("upload") { UploadScreen(navController) }
    }
}
package com.example.realtagram.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.realtagram.data.DummyPosts

@Composable
fun HomeScreen(navController: NavController) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Realtagram") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("upload") }) {
                Text("+")
            }
        }
    ) {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            items(DummyPosts.posts.size) { index ->
                val post = DummyPosts.posts[index]
                Card(
                    elevation = 4.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column {
                        Image(
                            painter = rememberImagePainter(post.imageUrl),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                        Text(
                            text = post.description,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
package com.example.realtagram.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun UploadScreen(navController: NavController) {
    var description by remember { mutableStateOf("") }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Upload Photo") }) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Photo Description") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // TODO: Handle upload
                    navController.navigateUp()
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Upload")
            }
        }
    }
}
package com.example.realtagram.data

data class Post(val imageUrl: String, val description: String)

object DummyPosts {
    val posts = listOf(
        Post(
            imageUrl = "https://picsum.photos/400/200",
            description = "لحظة غروب جميلة 🌅"
        ),
        Post(
            imageUrl = "https://picsum.photos/400/201",
            description = "ذكريات من الرحلة 🚗"
        )
    )
}
dependencies {
    implementation "androidx.navigation:navigation-compose:2.7.5"
    implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
    implementation "io.coil-kt:coil-compose:2.5.0"
    implementation "androidx.activity:activity-compose:1.9.0"
    implementation "androidx.compose.material:material:1.6.0"
}