package Actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ToggleAction

class Show_Activity : ToggleAction() {
    var selection = false
    override fun isSelected(anActionEvent: AnActionEvent): Boolean {
        return selection
    }

    override fun setSelected(anActionEvent: AnActionEvent, b: Boolean) {
        selection = !selection
    }
}