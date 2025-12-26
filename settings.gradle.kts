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
include(":community:data")
include(":community:domain")
include(":community:presentation")
include(":home:data")
include(":home:domain")
include(":home:presentation")
include(":my:data")
include(":my:domain")
include(":my:presentation")
include(":login:data")
include(":login:domain")
