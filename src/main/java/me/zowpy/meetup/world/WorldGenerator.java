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

@RequiredArgsConstructor
public class WorldGenerator {

    private final String worldName;
    private final MeetupPlugin plugin;

    public void generate() {

        delete();

        for (Biome biome : Biome.values()) {
            if (biome == Biome.PLAINS) continue;

            swapBiomes(biome, Biome.PLAINS);
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


        //plugin.setBorderManager(borderManager);

        Border border = new Border(world.getSpawnLocation(), Material.BEDROCK, plugin.getSettings().startingBorderSize, plugin.getSettings().borderHeight);
        border.getPhysicalBounds().getChunks().forEach(world::loadChunk);
        world.getEntities().stream().filter(entity -> entity.getType().isAlive() && entity.getType() != EntityType.PLAYER).forEach(Entity::remove);

        border.fill();

        world.getWorldBorder().setCenter(0, 0);
        world.getWorldBorder().setSize(plugin.getSettings().startingBorderSize * 2.0);
    }

    @SneakyThrows
    public void delete() {
        FileUtils.deleteDirectory(new File(worldName));
    }

    public void swapBiomes(Biome biome, Biome newBiome) {
        BiomeBase.getBiomes()[CraftBlock.biomeToBiomeBase(biome).id] = CraftBlock.biomeToBiomeBase(newBiome);
    }
}
