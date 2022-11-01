package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import com.Ttakttae.Jetbrains_Discord_Connect.Icons.Icons
import java.io.File
import java.io.IOException
import java.time.Instant

class SetActivity {
    fun set_activity() {
        Core.init(File("C:\\github.com\\Ttakttae\\Jetbrains-Discord-Connect\\src\\main\\lib\\x86_64\\discord_game_sdk.dll"))
        CreateParams().use { params ->
            params.clientID = 1036849907954368563L
            params.flags = CreateParams.getDefaultFlags()
            Core(params).use { core ->
                Activity().use { activity ->
                    activity.details = "Running an example"
                    activity.state = "and having fun"
                    activity.type = ActivityType.PLAYING

                    // Setting a start time causes an "elapsed" field to appear
                    activity.timestamps().start = Instant.now()

//                    // We are in a party with 10 out of 100 people.
                    activity.party().size().maxSize = 100
                    activity.party().size().currentSize = 10
//
//                    // Make a "cool" image show up
                    activity.assets().largeImage = "C:\\github.com\\Ttakttae\\Jetbrains-Discord-Connect\\src\\main\\resources\\discord_logo.png"
//
//                    // Setting a join secret and a party ID causes an "Ask to Join" button to appear
                    activity.party().id = "Party!"
                    activity.secrets().joinSecret = "Join!"

                    // Finally, update the current activity to our activity
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