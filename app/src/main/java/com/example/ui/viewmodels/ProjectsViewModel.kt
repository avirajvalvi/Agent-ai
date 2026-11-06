package com.example.ui.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.data.knowledge.SecurityKnowledgeBase
import com.example.data.local.AppDatabase
import com.example.data.local.CustomToolEntity
import com.example.data.local.ProjectEntity
import com.example.data.models.CustomSecurityTool
import com.example.data.models.SecurityProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProjectsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        AppDatabase::class.java,
        "secmentor_db"
    ).fallbackToDestructiveMigration().build()

    private val _projects = MutableStateFlow<List<SecurityProject>>(emptyList())
    val projects: StateFlow<List<SecurityProject>> = _projects.asStateFlow()

    private val _customTools = MutableStateFlow<List<CustomSecurityTool>>(emptyList())
    val customTools: StateFlow<List<CustomSecurityTool>> = _customTools.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            db.projectDao().getAllProjects().collect { entities ->
                if (entities.isEmpty()) {
                    // Seed initial sample projects
                    SecurityKnowledgeBase.sampleProjects.forEach { proj ->
                        db.projectDao().insertProject(
                            ProjectEntity(
                                id = proj.id,
                                title = proj.title,
                                description = proj.description,
                                category = proj.category,
                                lastUpdated = proj.lastUpdated,
                                scriptCount = proj.scriptCount,
                                sampleSnippet = proj.sampleSnippet
                            )
                        )
                    }
                } else {
                    _projects.value = entities.map {
                        SecurityProject(
                            id = it.id,
                            title = it.title,
                            description = it.description,
                            category = it.category,
                            lastUpdated = it.lastUpdated,
                            scriptCount = it.scriptCount,
                            sampleSnippet = it.sampleSnippet
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            db.customToolDao().getAllTools().collect { entities ->
                if (entities.isEmpty()) {
                    SecurityKnowledgeBase.sampleCustomTools.forEach { tool ->
                        db.customToolDao().insertTool(
                            CustomToolEntity(
                                id = tool.id,
                                name = tool.name,
                                category = tool.category,
                                language = tool.language,
                                description = tool.description,
                                code = tool.code,
                                usageExample = tool.usageExample
                            )
                        )
                    }
                } else {
                    _customTools.value = entities.map {
                        CustomSecurityTool(
                            id = it.id,
                            name = it.name,
                            category = it.category,
                            language = it.language,
                            description = it.description,
                            code = it.code,
                            usageExample = it.usageExample
                        )
                    }
                }
            }
        }
    }

    fun addNewProject(title: String, category: String, desc: String) {
        val proj = SecurityProject(
            title = title,
            category = category,
            description = desc,
            scriptCount = 1,
            sampleSnippet = "# Custom Security Lab Script\nprint('Initializing lab $title...')"
        )
        viewModelScope.launch {
            db.projectDao().insertProject(
                ProjectEntity(
                    id = proj.id,
                    title = proj.title,
                    description = proj.description,
                    category = proj.category,
                    lastUpdated = proj.lastUpdated,
                    scriptCount = proj.scriptCount,
                    sampleSnippet = proj.sampleSnippet
                )
            )
        }
    }
}
