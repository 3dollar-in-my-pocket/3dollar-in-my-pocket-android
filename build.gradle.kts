// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // 플러그인은 settings.gradle에서 관리되므로 여기서는 비워두거나 필요한 경우 추가합니다.
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
