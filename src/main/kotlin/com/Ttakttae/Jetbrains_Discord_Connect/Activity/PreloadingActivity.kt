package com.Ttakttae.Jetbrains_Discord_Connect.Activity

import com.Ttakttae.Jetbrains_Discord_Connect.Discord.SetActivity
import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import de.jcm.discordgamesdk.activity.ActivityType

class PreloadingActivity : PreloadingActivity() {
    override fun preload(p0: ProgressIndicator) {
        val setActivity = SetActivity()
        setActivity.update(1036849907954368563L)
    }
}