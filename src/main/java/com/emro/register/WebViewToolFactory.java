package com.emro.register;

import com.emro.configuration.PluginSettingsState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class WebViewToolFactory implements ToolWindowFactory {
	static AtomicBoolean clickedOnce = new AtomicBoolean(false);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
	    PluginSettingsState state = PluginSettingsState.getInstance(project);
        addWebViewContent(toolWindow, state.syncServiceUrl);
    }

	public static void addWebViewSignContent(@NotNull ToolWindow toolWindow, @NotNull String syncServiceUrl) {
		// WebView 콘텐츠 생성
		JBCefBrowser browser = new JBCefBrowser(syncServiceUrl);
		browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
			@Override
			public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int httpStatusCode) {
				cefBrowser.executeJavaScript(
						"document.querySelector('.tab[data-target=signup]')?.click();",
						cefBrowser.getURL(),
						0
				);
			}
		}, browser.getCefBrowser());

		ContentFactory contentFactory = ContentFactory.getInstance();
		Content content = contentFactory.createContent(browser.getComponent(), "Web Viewer", false);

		// Tool Window에 콘텐츠 추가
		toolWindow.getContentManager().addContent(content);
	}

    public static void addWebViewContent(@NotNull ToolWindow toolWindow, @NotNull String syncServiceUrl) {
        // WebView 콘텐츠 생성
        JBCefBrowser browser = new JBCefBrowser(syncServiceUrl);
	    clickedOnce.set(false);
	    browser.getJBCefClient().addLoadHandler(new CefLoadHandlerAdapter() {
		    @Override
		    public void onLoadEnd(CefBrowser cefBrowser, CefFrame cefFrame, int httpStatusCode) {
			    if(!clickedOnce.get()) {
				    cefBrowser.executeJavaScript(
						    "document.getElementById('langList')?.click();",
						    cefBrowser.getURL(),
						    0
				    );
			    }
			    clickedOnce.set(true);
		    }
	    }, browser.getCefBrowser());


	    ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(browser.getComponent(), "Web Viewer", false);

        // Tool Window에 콘텐츠 추가
        toolWindow.getContentManager().addContent(content);
    }
}
