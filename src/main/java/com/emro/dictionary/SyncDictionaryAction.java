package com.emro.dictionary;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SyncDictionaryAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }

        // 비동기 진행률 표시
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "다국어 동기화 진행 중...") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                indicator.setText("다국어를 동기화중입니다...");
                indicator.setFraction(0.0);

                try {
                    // Dictionary 동기화 실행
                    DictionaryCacheService service = project.getService(DictionaryCacheService.class);
                    if (service != null) {
                        service.sync(true);
                    }

                    // 진행률 업데이트 (예제용 대기 시간 추가)
                    for (int i = 1; i <= 5; i++) {
                        Thread.sleep(500);
                        indicator.setFraction(i / 5.0);
                    }

                    // 동기화 완료 알림
                    Notification notification = NotificationGroupManager.getInstance()
                            .getNotificationGroup("DictionarySyncNotification")
                            .createNotification("다국어 동기화 완료", NotificationType.INFORMATION);
                    notification.notify(project);

                } catch (Exception e) {
                    Notification notification = NotificationGroupManager.getInstance()
                            .getNotificationGroup("DictionarySyncNotification")
                            .createNotification("동기화 실패: " + e.getMessage(), NotificationType.ERROR);
                    notification.notify(project);
                }
            }
        });
    }
}
