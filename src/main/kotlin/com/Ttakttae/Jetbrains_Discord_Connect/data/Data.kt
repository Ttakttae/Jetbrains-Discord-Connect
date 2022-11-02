package com.Ttakttae.Jetbrains_Discord_Connect.data

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager

class Data {
    fun getFileName(): String{
        val project: Project? = IdeFocusManager.getGlobalInstance().lastFocusedFrame?.project
        val editor: FileEditor? = project?.let {
            FileEditorManager.getInstance(project)?.selectedEditor
        }

        if (project != null) {
            if (editor != null) {
                val file = editor.file

                if (file != null) {
                    val fileName = file.name
                    return fileName
                }
            }
        }

        return "notFound!"
    }
}