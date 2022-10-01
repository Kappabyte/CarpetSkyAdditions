package com.jsorrell.carpetskyadditions.mixin;

import com.jsorrell.carpetskyadditions.gen.SkyBlockChunkGenerator;
import com.jsorrell.carpetskyadditions.gen.SkyBlockStructures;
import com.jsorrell.carpetskyadditions.settings.Fixers;
import com.jsorrell.carpetskyadditions.settings.SkyAdditionsSettings;
import com.jsorrell.carpetskyadditions.settings.SkyBlockDefaults;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.level.ServerWorldProperties;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.io.IOException;
import java.nio.file.Path;

// Lower priority to ensure loadWorld mixin is called before carpet loads the settings
@Mixin(value = MinecraftServer.class, priority = 999)
public abstract class MinecraftServerMixin {
  @Shadow
  @Final
  protected SaveProperties saveProperties;

  @Shadow
  public abstract Path getSavePath(WorldSavePath worldSavePath);

  @Inject(method = "loadWorld", at = @At("HEAD"))
  private void fixSettingsFile(CallbackInfo ci) {
    Path worldSavePath = this.getSavePath(WorldSavePath.ROOT);
    // Fix existing settings
    try {
      Fixers.fixSettings(worldSavePath);
    } catch (IOException e) {
      SkyAdditionsSettings.LOG.error("Failed update config", e);
    }

    // Write defaults
    if (saveProperties.getGeneratorOptions().getDimensions().getOrThrow(DimensionOptions.OVERWORLD).getChunkGenerator() instanceof SkyBlockChunkGenerator && !this.saveProperties.getMainWorldProperties().isInitialized()) {
      try {
        SkyBlockDefaults.writeDefaults(worldSavePath);
      } catch (IOException e) {
        SkyAdditionsSettings.LOG.error("Failed write default configs", e);
      }
      try {
        SkyBlockDefaults.writeDefaults(worldSavePath);
      } catch (IOException e) {
        SkyAdditionsSettings.LOG.error("Failed write default configs", e);
      }
    }
  }

  @Inject(
    method = "setupSpawn",
    locals = LocalCapture.CAPTURE_FAILHARD,
    at = @At(value = "HEAD"),
    cancellable = true)
  private static void generateSpawnPlatform(ServerWorld world, ServerWorldProperties worldProperties, boolean bonusChest, boolean debugWorld, CallbackInfo ci) {
    ServerChunkManager chunkManager = world.getChunkManager();
    ChunkGenerator chunkGenerator = chunkManager.getChunkGenerator();
    if (!(chunkGenerator instanceof SkyBlockChunkGenerator)) return;

    world.setBlockState(new BlockPos(0, -61, 0), Blocks.GRASS_BLOCK.getDefaultState(), 0);
    worldProperties.setSpawnPos(new BlockPos(0, 100, 0), 0.0f);

    ci.cancel();
  }
}
