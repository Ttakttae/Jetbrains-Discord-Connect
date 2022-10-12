package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator
import com.Ttakttae.Jetbrains_Discord_Connect.Discord.SetActivity

class Connect : PreloadingActivity() {
    override fun preload(p0: ProgressIndicator) {
        val setActivity = SetActivity()
        setActivity.set_activity()
    }
}