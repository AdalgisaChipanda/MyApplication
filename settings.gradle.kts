@file:Suppress("UnstableApiUsage") // Adicione esta linha no topo para esconder os avisos de @Incubating

pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Repositório necessário para o gráfico
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "My Application"
include(":app")
