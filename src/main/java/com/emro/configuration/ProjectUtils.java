package com.emro.configuration;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;

public class ProjectUtils {

    public static Project getCurrentProject() {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        return (openProjects.length > 0) ? openProjects[0] : null;
    }

}
