package org.mewsic.testsuite

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import org.mewsic.testsuite.platform.showToast

@Composable
fun App() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "/* mewsic dev test suite */",
                        fontSize = MaterialTheme.typography.displayMedium.fontSize
                    )
                }
            )
        }

    ) { paddingValues ->

        Box(modifier = Modifier.padding(paddingValues)) {
        Column(modifier = Modifier.padding(5.dp, 0.dp)) {
            Row(horizontalArrangement = Arrangement.Center) {
                Button(onClick = {
                    showToast("hello world")

                }) {
                    Text("Hello World popup")
                }
            }
            Row {

            }
        }}
    }
}
