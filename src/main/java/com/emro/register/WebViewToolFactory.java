package com.emro.register;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefBrowser;
import org.jetbrains.annotations.NotNull;

public class WebViewToolFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        addWebViewContent(toolWindow);
    }

    public static void addWebViewContent(@NotNull ToolWindow toolWindow) {
        // WebView 콘텐츠 생성
        JBCefBrowser browser = new JBCefBrowser("https://www.naver.com");
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(browser.getComponent(), "Web Viewer", false);

        // Tool Window에 콘텐츠 추가
        toolWindow.getContentManager().addContent(content);
    }
}
