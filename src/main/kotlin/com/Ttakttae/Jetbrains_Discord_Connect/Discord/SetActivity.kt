package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType

import java.io.File
import java.io.IOException
import java.time.Instant

class SetActivity {
    fun set_activity() {
        Core.init(File("C:\\github.com\\Ttakttae\\Jetbrains-Discord-Connect\\discord_game_sdk_v2\\lib\\x86_64\\discord_game_sdk.dll"))
        CreateParams().use { params ->
            params.clientID = 1036849907954368563L
            params.flags = CreateParams.getDefaultFlags()
            Core(params).use { core ->
                Activity().use { activity ->
                    activity.details = "Testing NEW PLUGIN"
                    activity.state = "Code With Me"
                    activity.type = ActivityType.PLAYING
                    activity.timestamps().start = Instant.now()
                    activity.assets().largeImage = "intellij_idea_logo"

                    activity.party().size().maxSize = 10
                    activity.party().size().currentSize = 1
                    activity.party().id = "Party!"
                    activity.secrets().joinSecret = "Join!"

                    core.activityManager().updateActivity(activity)
                }

                // Run callbacks forever
                while (true) {
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