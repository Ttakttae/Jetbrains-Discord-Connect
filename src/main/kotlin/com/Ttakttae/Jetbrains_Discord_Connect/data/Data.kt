package com.Ttakttae.Jetbrains_Discord_Connect.data

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.IdeFocusManager
import com.intellij.openapi.fileEditor.FileEditor
import com.intellij.openapi.fileEditor.FileEditorManager

class Data {
    class EditorData(
        val projectName: String,
        val fileName: String
    ) {
        val fileType: String = when() {
            "py" -> "python"
        }

        private fun getFileType(fileName: String) String {
            val
        }
    }

    fun getData(): EditorData {
        val project: Project? = IdeFocusManager.getGlobalInstance().lastFocusedFrame?.project
        val editor: FileEditor? = project?.let {
            FileEditorManager.getInstance(project)?.selectedEditor
        }

        var projectName = ""
        var fileName    = ""

        if (project != null) {
            projectName = project.name

            if (editor != null) {
                val file = editor.file

                if (file != null) {
                    fileName = file.file
                }
            }
        }

        return EditorData(projectName, fileName)
    }
}