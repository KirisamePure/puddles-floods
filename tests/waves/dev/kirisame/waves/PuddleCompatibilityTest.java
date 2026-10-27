package dev.kirisame.waves;

import dev.kirisame.waves.iris.IrisBridge;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.irisshaders.iris.shaderpack.materialmap.WorldRenderingSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL20;

/** Runs in Waves' separate production test mod; never shipped in Puddles & Floods. */
public final class PuddleCompatibilityTest implements FabricClientGameTest {
    private static void require(boolean ok, String message) {
        if (!ok) throw new AssertionError(message);
    }

    private static void shaderWater(boolean enabled) {
        try {
            Object config = Class.forName("pigcart.puddleflood.config.ConfigManager").getField("config").get(null);
            config.getClass().getField("useShaderpackWater").setBoolean(config, enabled);
        } catch (ReflectiveOperationException e) { throw new AssertionError(e); }
    }

    @Override
    public void runTest(ClientGameTestContext context) {
        context.runOnClient(client -> {
            shaderWater(true);
            IrisTestSupport.disable();
            client.options.pauseOnLostFocus = false;
            client.options.getViewDistance().setValue(3);
        });
        try (var world = context.worldBuilder().create()) {
            var server = world.getServer();
            server.runCommand("gamemode spectator @a");
            server.runCommand("fill -12 60 -12 12 61 12 minecraft:stone");
            server.runCommand("fill -12 62 -12 12 62 12 minecraft:water");
            server.runCommand("setblock 0 63 0 puddleflood:puddle");
            server.runCommand("tp @a 0 66 0 0 30");
            context.waitTicks(30);
            context.takeScreenshot("puddle-waves-native");
            context.runOnClient(client -> IrisTestSupport.enableFixture());
            context.waitTicks(30);
            context.takeScreenshot("puddle-waves-shader-water-on");
            context.runOnClient(client -> {
                BlockState puddle = Registries.BLOCK.get(Identifier.of("puddleflood", "puddle")).getDefaultState();
                require(!puddle.isAir(), "Puddle block missing");
                var settings = WorldRenderingSettings.INSTANCE;
                var original = settings.getBlockStateIds();
                int id = original.getInt(Blocks.WATER.getDefaultState());
                require(original.containsKey(puddle) && original.getInt(puddle) == id,
                        "Puddle must actually share the shader water material");
                require(IrisBridge.waterId() == id && IrisBridge.active(),
                        "Shared puddle material disabled Waves");
                int program = IrisTestSupport.program();
                int location = GL20.glGetUniformLocation(program, "waves_water_id");
                require(location >= 0 && GL20.glGetUniformf(program, location) == id,
                        "GPU water ID does not match the shared material");
                // Exercise the transformed, real Waves method with controlled material maps.
                try {
                    var mixed = new Object2IntOpenHashMap<BlockState>(original);
                    mixed.put(Blocks.STONE.getDefaultState(), id);
                    settings.setBlockStateIds(mixed);
                    IrisBridge.invalidateWaterId();
                    require(IrisBridge.waterId() < 0, "Unrelated material aliases must still disable Waves");
                    var waterOnly = new Object2IntOpenHashMap<BlockState>();
                    waterOnly.put(Blocks.WATER.getDefaultState(), id);
                    settings.setBlockStateIds(waterOnly);
                    IrisBridge.invalidateWaterId();
                    require(IrisBridge.waterId() == id, "Ordinary exclusive water must still work");
                    waterOnly.put(puddle, id);
                    shaderWater(false);
                    IrisBridge.invalidateWaterId();
                    require(IrisBridge.waterId() < 0, "Alias exception must follow the shader-water setting");
                } finally {
                    shaderWater(true);
                    settings.setBlockStateIds(original);
                    IrisBridge.invalidateWaterId();
                }
            });
            context.runOnClient(client -> { shaderWater(false); IrisTestSupport.enableFixture(); });
            context.waitTicks(10);
            context.takeScreenshot("puddle-waves-shader-water-off");
            require(context.computeOnClient(client -> IrisBridge.active()), "Turning shader water off disabled Waves");
            context.runOnClient(client -> { shaderWater(true); IrisTestSupport.enableFixture(); });
            context.waitTicks(10);
            context.takeScreenshot("puddle-waves-shader-water-reenabled");
            require(context.computeOnClient(client -> IrisBridge.active()), "Re-enabling shader water disabled Waves");
            WavesClient.LOGGER.info("PUDDLE_WAVES_COMPAT_PASS sharedMaterial=true unrelatedAliasRejected=true reload=true");
        }
    }
}
