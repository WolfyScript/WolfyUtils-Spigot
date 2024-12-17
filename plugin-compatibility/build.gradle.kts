plugins {
    id("com.wolfyscript.wolfyutils.spigot.java-conventions")
}

dependencies {
    compileOnly("com.ssomar.score:SCore:4.24.4.15")
    compileOnly("com.ssomar.executableblocks:ExecutableBlocks:4.24.4.15")
    compileOnly("com.denizenscript:denizen:1.2.5-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.willfp:eco:6.74.2")
    compileOnly("com.github.LoneDev6:api-itemsadder:3.1.5")
    compileOnly("com.elmakers.mine.bukkit:MagicAPI:10.2")
/*    compileOnly("com.github.AlessioGr:FancyBags:2.7.0")*/
    compileOnly("com.github.oraxen:oraxen:1.152.0")
    compileOnly("io.lumine:MythicLib:1.1.5")
    compileOnly("net.Indyuce:MMOItems-API:6.9.2-SNAPSHOT")
    compileOnly("io.lumine:Mythic-Dist:5.6.1")
    compileOnly("com.google.inject:guice:5.1.0")
    compileOnly(project(":core"))
}

description = "plugin-compatibility"