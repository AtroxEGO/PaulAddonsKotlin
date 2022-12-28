package me.atroxego.pauladdons.utils

import PaulAddons
import gg.essential.api.utils.Multithreading
import me.atroxego.pauladdons.features.betterlootshare.ESP.logger
import java.awt.*
import java.net.URI
import javax.swing.JButton
import javax.swing.JFrame
import javax.swing.JLabel
import javax.swing.border.EmptyBorder

object UpdateManager {
public fun checkUpdate(){
    logger.info("Checking for updates...")
    Multithreading.runAsync {
        val version = HttpUtils.sendGet("https://raw.githubusercontent.com/AtroxEGO/PaulAddons/master/version.txt", null)?.toDouble() ?: return@runAsync
        if (PaulAddons.VERSION.toDouble() < version) {
//            drawFrame(version)
        } else logger.info("Latest version!")
    }
}


    private fun drawFrame(version: Double) {
        // Create a new JFrame
        val frame = JFrame()

// Set the properties of the frame
        frame.title = "Paul Addons"
        frame.size = Dimension(300,160)
        frame.layout = null
        frame.contentPane.layout = FlowLayout()
        frame.isResizable = false
        frame.isAlwaysOnTop = true
        frame.modalExclusionType = Dialog.ModalExclusionType.APPLICATION_EXCLUDE

// Create a new JLabel
        val text = "<html><b>Paul Addons new version found!</b><br>&nbsp;&nbsp;&nbsp;Current Version: " + PaulAddons.VERSION + "<br>&nbsp;&nbsp;&nbsp;Latest Version: "+ version +"</html>"
        val label = JLabel(text)
        label.border = EmptyBorder(10,5,5,5)
        label.font = Font("Posterama", Font.PLAIN, 16)
//        label.foreground = Color.WHITE
// Add the label to the frame
        frame.add(label)

// Create a new JButton
        val button = JButton("Download Latest Version")
        button.background = Color.WHITE
// Set the action for the button
        button.addActionListener {
            val url = "https://github.com/AtroxEGO/PaulAddons/releases"
            Desktop.getDesktop().browse(URI(url))
            frame.dispose()
        }

// Add the button to the frame
        frame.add(button)

// Set the location of the frame
        frame.setLocationRelativeTo(null)
//        frame.contentPane.background = Color.BLUE

// Make the frame visible
        frame.isVisible = true
    }
}