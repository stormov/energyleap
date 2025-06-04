plugins {
    alias(libs.plugins.kotlin)
    alias(libs.plugins.paper) apply false
    alias(libs.plugins.shadow) apply false
}

tasks.jar { enabled = false }

repositories {
    mavenCentral()
}

subprojects {
    group = "ru.stormov.energyleap"
    version = "1.0-SHAPSHOT"

    val libs = rootProject.libs

    repositories {
        mavenCentral()

        maven("https://jcenter.bintray.com")
        maven("https://jitpack.io")
        maven("https://repo.minebench.de/")
    }

    applyPlugins(
        libs.plugins.kotlin,
        libs.plugins.shadow
    )

    dependencies {
        compileOnly(libs.kotlinx.coroutines)
    }

    kotlin.jvmToolchain(21)

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = 21
    }

    tasks.withType<Jar> {
        destinationDirectory = file("$rootDir/build")
        archiveVersion = ""
    }

    sourceSets.main {
        kotlin.srcDir("src")
        resources.srcDir("resources")
    }

}

fun Project.applyPlugins(vararg plugins: Provider<PluginDependency>) {
    plugins.mapNotNull { it.orNull?.pluginId }.forEach {
        apply {
            plugin(it)
        }
    }
}