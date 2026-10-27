# Puddles & Floods

Minecraft mods that allows rivers to visually flood their banks and adds puddles:
- during rain
- below dripping blocks
- left behind by flowing water
- left behind by splash potions
- left behind by broken filled cauldrons
- left behind by melted snow

### Links
- [Mod description and download](https://modrinth.com/mod/puddles-floods)
- [Update notes / changelog](CHANGELOG.md)

### Minecraft 1.21.10 Fabric

Build with JDK 21 using `bash gradlew :1.21.10-fabric:build --configure-on-demand`.
The release JAR is written to `versions/1.21.10-fabric/build/libs/`.
This target reuses the existing 1.21.9/1.21.10 implementation with Fabric API
0.138.4+1.21.10, Sodium 0.7.3 and Iris 1.9.7.

Waves 0.4.3 is optional. When both mods are installed, **Use shaderpack water**
can remain enabled: Puddles & Floods' own puddle material alias is accepted by
Waves' water-material validation. Other non-water material aliases still trigger
Waves' safety check. This does not add support for shader packs that Waves itself
cannot render; disable the shader pack's own water displacement as Waves recommends.
See [the production compatibility test](tests/waves/README.md).
