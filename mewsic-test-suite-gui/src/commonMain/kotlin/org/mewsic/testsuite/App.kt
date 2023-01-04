package org.mewsic.testsuite

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.unit.dp
import io.ktor.util.date.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.mewsic.commons.io.Net
import org.mewsic.testsuite.core.media.M4A
import org.mewsic.testsuite.core.net.getByteArray
import org.mewsic.testsuite.platform.showToast



@OptIn(ExperimentalMaterial3Api::class)
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
                //! M4A read test
                val options = CONSTANTS.M4A_TEST_FILE_URIS.keys
                var eventLog: MutableMap<Long, String> by remember { mutableMapOf() }
                eventLog[getTimeMillis()] = "Test started"

                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf(options.first()) }
                var eventHandler = M4A.EventWatcher { eventName, args ->
                    eventLog[getTimeMillis()] = "$eventName: $args"
                }
                var m4a: M4A? by remember { mutableStateOf(null) }

                Column {
                    Text(
                        "M4A Read Test",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Row {

                        Button(onClick = {
                            CoroutineScope(Dispatchers.Net).async {
                                m4a = M4A(getByteArray(CONSTANTS.M4A_TEST_FILE_URIS[selectedOption]?: CONSTANTS.M4A_TEST_FILE_URIS.firstNotNullOf { it.value }), eventHandler)
                            }

                        }) {
                            Text("Open M4A")
                        }
                        TextField(
                            value = selectedOption,
                            onValueChange = {
                                selectedOption = it
                            },
                            label = { Text("File") },
                            singleLine = true,
                            enabled = !expanded,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 5.dp)
                        )
                        Button(onClick = {
                            m4a = null
                        }) {
                            Text("Close M4A")
                        }
                    }
                }
                Column {
                    Text(
                        "Event Log",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Column(modifier = Modifier.padding(5.dp, 0.dp).height(500.dp).scrollable(rememberScrollState(0), orientation = Orientation.Vertical)) {
                        for ((time, event) in eventLog) {
                            Text("[$time] $event")
                        }
                    }
                }

            }
        }}
    }
}
