package com.emro.register;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class RestoreWebViewAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("WebViewTool");
            if (toolWindow != null) {
                ContentManager contentManager = toolWindow.getContentManager();
                if (contentManager.getContentCount() == 0) {
                    WebViewToolFactory.addWebViewContent(toolWindow); // 콘텐츠 다시 추가
                }
                if (toolWindow.isVisible()) toolWindow.hide();
                else toolWindow.show();
            }
        }
    }

}
