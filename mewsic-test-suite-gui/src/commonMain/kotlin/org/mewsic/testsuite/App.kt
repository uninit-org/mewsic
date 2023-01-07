package org.mewsic.testsuite

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.Button
import androidx.compose.material.Divider
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
import kotlinx.coroutines.launch
import org.mewsic.commons.io.Net
import org.mewsic.commons.lang.Log
import org.mewsic.testsuite.core.media.M4A
import org.mewsic.testsuite.core.net.getByteArray
import org.mewsic.testsuite.platform.showToast



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val eventLog = remember { mutableStateMapOf<Long, String>() }
    eventLog.put(getTimeMillis(), "M4A Eventlog Begin")
    var m4aIsRead by remember { mutableStateOf(false) }
    val eventHandler = M4A.EventWatcher { eventName, args ->
        eventLog.put(getTimeMillis(), "$eventName: $args")
        if (eventName == "OPEN") {
            m4aIsRead = args["SUCCESS"] as Boolean
        }
    }
    var m4a: M4A? by remember { mutableStateOf(null) }
    val eventScroller = rememberScrollState()

    Log.alsoHandler = Log.AlsoHandler { level, message ->
        eventLog.put(getTimeMillis(), "LOG//$level: $message")
    }
    eventLog.put(getTimeMillis(), "AlsoHandler Patched")


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
        Column(modifier = Modifier.padding(5.dp, 0.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                Button(onClick = {
                    showToast("denial 2311444\ndespair 3223412\ndistortion 6233322")

                }) {
                    Text("Hello World (toast)")
                }
                Divider(modifier = Modifier.size(5.dp, 0.dp))
                var text by remember { mutableStateOf("") }
                TextField(
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Enter text") },
                    placeholder = { Text("Placeholder") },
                    modifier = Modifier.padding(5.dp, 0.dp)
                )
                Divider(modifier = Modifier.size(5.dp, 0.dp))
                Button(onClick = {
                    showToast(text)
                }) {
                    Text("Show text (toast)")
                }
            }
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                //! M4A read test
                val options = CONSTANTS.M4A_TEST_FILE_URIS.keys
                var expanded by remember { mutableStateOf(false) }
                var selectedOption by remember { mutableStateOf(options.first()) }


                Column {
                    Text(
                        "M4A Read Test",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Row {
                        Column {
                            composeMemorable(m4a) {
                                if (it == null)
                                    Button(onClick = {
                                        CoroutineScope(Dispatchers.Net).launch {
                                            val url = CONSTANTS.M4A_TEST_FILE_URIS[selectedOption]!!
                                            eventLog.put(getTimeMillis(), "Loading $url")
                                            val bytes = getByteArray(url)
                                            eventLog.put(getTimeMillis(), "Loaded $url")
                                            val tm4a = M4A(bytes, eventHandler)
                                            throw Exception("test")
                                            eventLog.put(getTimeMillis(), "M4A openedm, outer runs")

                                            tm4a.doRead()
                                            eventLog.put(getTimeMillis(), "M4A read")
                                            m4a = tm4a


                                        }

                                    }) {
                                        Text("Open M4A")
                                    } else
                                    Button(onClick = {
                                        m4a = null
                                        eventLog.clear()
                                    }) {
                                        Text("Close M4A")
                                    }
                            }
                        }

                        Column {  TextField(
                            value = selectedOption,
                            onValueChange = {
                                selectedOption = it
                            },
                            label = { Text("File") },
                            singleLine = true,
                            enabled = !expanded,
                            modifier = Modifier
//                                .weight(1f)
                                .padding(start = 5.dp)
                        ) }

                    }
                    composeMemorable(m4aIsRead) {
                        if (it) {
                            Row {
                                Button(
                                    onClick = {

                                    }
                                ) {
                                    Text("Read Metadata")
                                }
                            }
                        }
                    }
                }
                Column(modifier = Modifier.padding(5.dp, 0.dp).height(300.dp).scrollable(eventScroller, orientation = Orientation.Vertical)) {
                    Text(
                        "Event Log",
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize
                    )
                    Column() {
                        composeMemorable(eventLog) {
//                            for ((time, str) in it.toList().sortedBy {entry -> entry.first }) {
//                                Text("[$time] $str")
//                            }
                            LazyColumn {
                                items(it.toList().sortedBy {entry -> entry.first }.toList()) { (time, str) ->
                                    Text("[$time] $str")
                                }
                            }
                        }

                    }
                }

            }
        }}
    }
}

/**
 * This is a workaround to allow usage of a modified remembered object that gets re-created on every change.
 *
 *
 * This causes the composer to realize that a composable element requires the use of a remembered
 * object even when the remembered object is not a direct parameter of the composable.
 *
 * @param memorable The remembered object to pass to the slot
 * @param slot The composable element to pass the remembered object to
 */
@Composable
fun <T> composeMemorable(memorable: T, slot: @Composable (T) -> Unit) {

    slot(memorable)

}
