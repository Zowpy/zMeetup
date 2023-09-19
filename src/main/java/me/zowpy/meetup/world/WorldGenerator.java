package me.zowpy.meetup.world;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.border.Border;
import net.minecraft.server.v1_8_R3.BiomeBase;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class WorldGenerator {

    private final String worldName;
    private final MeetupPlugin plugin;

    public void generate() {

        delete();

        for (Biome biome : Biome.values()) {
            if (plugin.getSettings().biomes.contains(biome)) continue;

            swapBiomes(biome, plugin.getSettings().biomes.get(ThreadLocalRandom.current().nextInt(plugin.getSettings().biomes.size())));
        }

        WorldCreator creator = new WorldCreator(worldName);
        creator.generateStructures(false);

        World world = creator.createWorld();

        world.setGameRuleValue("naturalRegeneration", "false");
        world.setGameRuleValue("doMobSpawning", "false");
        world.setGameRuleValue("doDaylightCycle", "false");
        world.setAutoSave(false);

        Bukkit.setSpawnRadius(0);

        Location spawn = world.getSpawnLocation().clone();
        world.setSpawnLocation(spawn.getBlockX(), world.getHighestBlockYAt(spawn.getBlockX(), spawn.getBlockZ()) + 1, spawn.getBlockZ());

        world.getWorldBorder().setCenter(0.0, 0.0);
        world.getWorldBorder().setSize((plugin.getSettings().startingBorderSize) * 2.0);

        Border border = new Border(world.getSpawnLocation(), Material.BEDROCK, plugin.getSettings().startingBorderSize, plugin.getSettings().borderHeight);
        border.getPhysicalBounds().getChunks().forEach(world::loadChunk);
        world.getEntities().stream().filter(entity -> entity.getType().isAlive() && entity.getType() != EntityType.PLAYER).forEach(Entity::remove);

        border.fill();
    }

    @SneakyThrows
    public void delete() {
        FileUtils.deleteDirectory(new File(worldName));
    }

    public void swapBiomes(Biome biome, Biome newBiome) {
        BiomeBase.getBiomes()[CraftBlock.biomeToBiomeBase(biome).id] = CraftBlock.biomeToBiomeBase(newBiome);
    }
}
