pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven {
            url = uri("https://devrepo.kakao.com/nexus/content/groups/public/")
        }
        maven {
            url = uri("https://jitpack.io")
        }
        maven {
            url = uri("https://repository.map.naver.com/archive/maven")
        }
    }
}

rootProject.name = "MyDiary"
include(":app")
include(":common")
include(":core:network")
include(":core:common")
include(":core:ui")
include(":core:designsystem")
include(":core:abtest")
include(":domain")
include(":data")
