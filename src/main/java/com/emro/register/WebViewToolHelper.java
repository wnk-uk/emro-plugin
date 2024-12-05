package com.emro.register;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.content.ContentManagerAdapter;
import com.intellij.ui.content.ContentManagerEvent;
import org.jetbrains.annotations.NotNull;

public class WebViewToolHelper {

    public static void attachContentListener(ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContentManagerListener(new ContentManagerAdapter() {
            @Override
            public void contentRemoved(@NotNull ContentManagerEvent event) {
                // Web Viewer 콘텐츠가 제거되었을 때 복구
                if (contentManager.getContentCount() == 0) { // 모든 콘텐츠가 닫힌 경우
                    WebViewToolFactory.addWebViewContent(toolWindow); // 콘텐츠 다시 추가
                }
            }
        });
    }
}
