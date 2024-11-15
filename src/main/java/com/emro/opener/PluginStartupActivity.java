package com.emro.opener;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.startup.StartupActivity;
import org.jetbrains.annotations.NotNull;

public class PluginStartupActivity implements StartupActivity {

	@Override
	public void runActivity(@NotNull com.intellij.openapi.project.Project project) {
		// 백그라운드 스레드에서 웹 서버 실행
		ApplicationManager.getApplication().executeOnPooledThread(() -> {
			try {
				System.out.println("1212");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		System.out.println("Web server started during IntelliJ IDEA startup.");
	}
}