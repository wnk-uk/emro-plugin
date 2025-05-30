package com.emro.emro.plugin

import com.emro.dictionary.DictionaryCacheService
import com.intellij.openapi.application.ApplicationManager
import com.emro.opener.ElementInfoReceiverService
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

class StartUpActivity : ProjectActivity, DumbAware {
    override suspend fun execute(project: Project) {
        ApplicationManager.getApplication().invokeLater {
//            val service = project.getService(ElementInfoReceiverService::class.java)
//            service?.let {
//                println("ElementInfoReceiverService initialized manually.")
//            }

            val cacheService = project.getService(DictionaryCacheService::class.java)
            cacheService?.let {
                println("DictionaryCacheService initialized manually.")
            }
        }

    }
}