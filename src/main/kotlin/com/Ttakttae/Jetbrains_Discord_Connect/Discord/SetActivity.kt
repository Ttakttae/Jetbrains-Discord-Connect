package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import de.jcm.discordgamesdk.Core
import de.jcm.discordgamesdk.CreateParams
import de.jcm.discordgamesdk.activity.Activity
import de.jcm.discordgamesdk.activity.ActivityType
import java.io.File
import java.time.Instant

import com.Ttakttae.Jetbrains_Discord_Connect.data.Data

class SetActivity {
    fun update(appId: Long) {
        Core.init(File("C:\\github.com\\Ttakttae\\Jetbrains-Discord-Connect\\discord_game_sdk_v2\\lib\\x86_64\\discord_game_sdk.dll"))

        val data = Data()
        val startTime = Instant.now()

        CreateParams().use { params ->
            params.clientID = appId
            params.flags = CreateParams.getDefaultFlags()

            Core(params).use { core ->
                while (true) {
                    val editorData = data.getData()

                    Activity().use { activity ->
                        activity.details = editorData.projectName
                        activity.state = editorData.fileName
                        activity.type = ActivityType.PLAYING
                        activity.timestamps().start = startTime

                        activity.assets().largeImage = "intellij_idea_logo"
                        activity.assets().largeText = "IntelliJ IDEA Ultimate"
                        activity.assets().smallImage = ""
                        activity.assets().smallText = ""

//                    activity.party().size().maxSize = partyMaxSize
//                    activity.party().size().currentSize = partyCurrentSize
//                    activity.party().id = partyId
//
//                    activity.secrets().matchSecret = matchSecret
//                    activity.secrets().joinSecret = joinSecret
//                    activity.secrets().spectateSecret = spectateSecret

                        activity.instance = false

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