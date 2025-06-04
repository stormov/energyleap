import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    alias(libs.plugins.paper)
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

dependencies {
    paperweight.paperDevBundle(libs.versions.bukkit)

    implementation("com.github.fierioziy.particlenativeapi:ParticleNativeAPI-core:4.4.0")
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}

tasks.withType<ShadowJar> {
    dependencies {
        exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
    }
}

description = "EnergyLeap paper"