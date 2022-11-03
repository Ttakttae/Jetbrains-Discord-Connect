package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.ActivityManager
import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import java.io.File
import java.time.Instant

class SetActivity {
    fun updateActivity(applicationID: Long, details: String, state: String, type: ActivityType,
                       largeImage: String, largeText: String, smallImage: String, smallText: String,
                       partyId: String, partyCurrentSize: Int, partyMaxSize: Int,
                       matchSecret: String, joinSecret: String, spectateSecret: String,
                       instance: Boolean) {
        Core.init(File("/Users/hyunwoo/Documents/Coding_Projects/Jetbrains-Discord-Connect/discord_game_sdk_v2/lib/x86_64/discord_game_sdk.dylib"))
        CreateParams().use { params ->
            params.clientID = applicationID
            params.flags = CreateParams.getDefaultFlags()
            Core(params).use { core ->
                Activity().use { activity ->
                    activity.details = details
                    activity.state = state
                    activity.type = type

                    activity.timestamps().start = Instant.now()

                    activity.assets().largeImage = largeImage
                    activity.assets().largeText = largeText
                    activity.assets().smallImage = smallImage
                    activity.assets().smallText = smallText

                    activity.party().size().maxSize = partyMaxSize
                    activity.party().size().currentSize = partyCurrentSize
                    activity.party().id = partyId

                    activity.secrets().matchSecret = matchSecret
                    activity.secrets().joinSecret = joinSecret
                    activity.secrets().spectateSecret = spectateSecret

                    activity.instance = instance

                    core.activityManager().updateActivity(activity)
                }
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