

plugins {
    kotlin("jvm") version "1.8.20"

    // For serialization: remove if not needed
    kotlin("plugin.serialization") version "1.8.20"

    idea
    java
    id("gg.essential.loom") version "0.10.0.+"
    id("dev.architectury.architectury-pack200") version "0.1.3"
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "PaulAddons"
version = "3.1"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(8))
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

loom {
    launchConfigs {
        "client" {
            property("mixin.debug", "true")
            property("asmhelper.verbose", "true")
            arg("--tweakClass", "org.spongepowered.asm.launch.MixinTweaker")
            arg("--mixin", "mixins.pauladdons.json")
        }
    }
    forge {
        pack200Provider.set(dev.architectury.pack200.java.Pack200Adapter())
        mixinConfig("mixins.pauladdons.json")
    }
    mixin {
        defaultRefmapName.set("mixins.pauladdons.refmap.json")
    }
}

sourceSets.main {
    output.setResourcesDir(file("$buildDir/classes/java/main"))
}

repositories {
    mavenCentral()
    maven("https://repo.spongepowered.org/maven/")
    maven("https://repo.sk1er.club/repository/maven-public")
    maven("https://maven.ilarea.ru/snapshots")
    maven("https://repo.essential.gg/repository/maven-public")
    maven("https://jcenter.bintray.com")
    jcenter()
}

val shadowImpl: Configuration by configurations.creating {
    configurations.implementation.get().extendsFrom(this)
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.0")
    shadowImpl ("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.mongodb:mongodb-driver-sync:4.9.1")
    compileOnly("com.yworks:yguard:4.0.0")
    minecraft("com.mojang:minecraft:1.8.9")
    mappings("de.oceanlabs.mcp:mcp_stable:22-1.8.9")
    forge("net.minecraftforge:forge:1.8.9-11.15.1.2318-1.8.9")
    implementation("org.json:json:20230227")
    shadowImpl("org.spongepowered:mixin:0.7.11-SNAPSHOT") { isTransitive = false }
    annotationProcessor("org.spongepowered:mixin:0.8.4-SNAPSHOT")
    shadowImpl("gg.essential:loader-launchwrapper:1.1.3")
//    implementation("gg.essential:universalcraft-1.8.9-forge:254")
//    implementation("gg.essential:vigilance-1.8.9-forge:280")
    implementation("gg.essential:loader-launchwrapper:1.1.3")
    implementation("gg.essential:essential-1.8.9-forge:3662")
}

tasks {
    processResources {
        filesMatching("mcmod.info") {
            expand(
                mapOf(
                    "modname" to project.name,
                    "modid" to project.name.toLowerCase(),
                    "version" to project.version,
                    "mcversion" to "1.8.9"
                )
            )
        }
    }
}

tasks.withType(JavaCompile::class) {
    options.encoding = "UTF-8"
}

tasks.withType(Jar::class) {
    manifest.attributes.run {
        this["FMLCorePluginContainsFMLMod"] = "true"
        this["ForceLoadAsMod"] = "true"

        this["MixinConfigs"] = "mixins.pauladdons.json"

        this["TweakClass"] = "gg.essential.loader.stage0.EssentialSetupTweaker"
        this["TweakOrder"] = "0"
    }
}

val remapJar by tasks.named<net.fabricmc.loom.task.RemapJarTask>("remapJar") {
    archiveClassifier.set("all")
    from(tasks.shadowJar)
    input.set(tasks.shadowJar.get().archiveFile)
}

tasks.shadowJar {
    archiveClassifier.set("all-dev")
    configurations = listOf(shadowImpl)
    doLast {
        configurations.forEach {
            println("Config: ${it.files}")
        }
    }
}

tasks.assemble.get().dependsOn(tasks.remapJar)


