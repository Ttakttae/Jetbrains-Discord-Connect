package com.Ttakttae.Jetbrains_Discord_Connect.Activity

import com.Ttakttae.Jetbrains_Discord_Connect.Discord.SetActivity
import com.Ttakttae.Jetbrains_Discord_Connect.data.Data
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.StartupActivity
import de.jcm.discordgamesdk.activity.ActivityType

class BackgroundPostStartupActivity : StartupActivity, StartupActivity.Background, StartupActivity.DumbAware {
    override fun runActivity(p0: Project) {
        val setActivity = SetActivity()
        val data = Data()
        val editorData = data.getData()
        setActivity.updateActivity(1036849907954368563L, editorData[0],editorData[1], ActivityType.PLAYING,
            "intellij_idea_logo", "IntelliJ IDEA Ultimate", "python", "Python",
            "", 0, 0, "", "", "", false)
    }
}