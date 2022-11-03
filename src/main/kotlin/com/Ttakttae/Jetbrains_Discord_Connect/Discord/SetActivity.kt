package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import java.io.File
import java.time.Instant

import com.Ttakttae.Jetbrains_Discord_Connect.data.Data

class SetActivity {
    fun updateActivity() {
        Core.init(File("C:\\github.com\\Ttakttae\\Jetbrains-Discord-Connect\\discord_game_sdk_v2\\lib\\x86_64\\discord_game_sdk.dll"))
        CreateParams().use { params ->
            params.clientID = 1036849907954368563L
            params.flags = CreateParams.getDefaultFlags()
            val data = Data()
            var editorData: Array<String>

            val startTime = Instant.now()

            Core(params).use { core ->
                // Run callbacks forever
                while (true) {
                    Activity().use { activity ->
                        editorData = data.getData()

                        activity.details = editorData[0]
                        activity.state = editorData[1]
                        activity.type = ActivityType.PLAYING

                        activity.timestamps().start = startTime

                        activity.assets().largeImage = "intellij_idea_logo"
                        activity.assets().largeText = "IntelliJ IDEA Ultimate"
//                        activity.assets().smallImage = "python"
//                        activity.assets().smallText = "Python"

//                        activity.party().size().maxSize = 10
//                        activity.party().size().currentSize = 1
//                        activity.party().id = "Party!"
//
//                        activity.secrets().joinSecret = "Join!"

                        core.activityManager().updateActivity(activity)
                    }

                    core.runCallbacks()
                    try {
                        // Sleep a bit to save CPU
                        Thread.sleep(16)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }
}