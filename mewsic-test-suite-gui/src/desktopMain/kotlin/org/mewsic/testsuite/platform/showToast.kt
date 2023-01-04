package org.mewsic.testsuite.platform

import kotlinx.coroutines.*
import java.awt.*
import javax.swing.*
import javax.swing.border.EmptyBorder
actual fun showToast(message: String) {
    class Toast : JFrame() {
        private val text = JTextArea().apply {
            background = Color.black
            margin = Insets(10, 10, 10, 10)
            border = EmptyBorder(Insets(10, 10, 10, 10))
            isEditable = false
            isOpaque = false
            isFocusable = false
            foreground = Color.white
            caretPosition = 0
            text = message

        }
        private val tPanel = JPanel()
        init {
            isUndecorated = true
            isAlwaysOnTop = true
            background = Color.black
            contentPane.add(tPanel)
            defaultCloseOperation = EXIT_ON_CLOSE
            setLocationRelativeTo(null)
            tPanel.add(text)
            tPanel.background = Color.black

            pack()
            isVisible = true
        }
    }
    val context = newSingleThreadContext("Toast")
    CoroutineScope(context).launch {
        val t = Toast()
        delay(2000)
        t.isVisible = false
        t.dispose()
        context.close()
    }
}
