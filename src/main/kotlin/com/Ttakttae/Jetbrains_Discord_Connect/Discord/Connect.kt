package com.Ttakttae.Jetbrains_Discord_Connect.Discord

import com.intellij.openapi.application.PreloadingActivity
import com.intellij.openapi.progress.ProgressIndicator

class Connect : PreloadingActivity() {
    override fun preload(p0: ProgressIndicator) {
        val setActivity = SetActivity()
        setActivity.updateActivity()
    }
}