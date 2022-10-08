import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.ToggleAction;
import org.jetbrains.annotations.NotNull;

public class Show_Activity extends ToggleAction {

    boolean selection = false;

    @Override
    public boolean isSelected(@NotNull AnActionEvent anActionEvent) {
        return selection;
    }

    @Override
    public void setSelected(@NotNull AnActionEvent anActionEvent, boolean b) {
        selection = !selection;
    }
}