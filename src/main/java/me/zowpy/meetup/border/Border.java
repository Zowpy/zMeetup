package me.zowpy.meetup.border;

import lombok.Getter;
import me.zowpy.meetup.MeetupPlugin;
import me.zowpy.meetup.utils.Cuboid;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

@Getter
public class Border {

    private final Location origin;
    private Material material;
    private int size;
    private int height;
    private boolean wrapTerrain;
    private BorderConfiguration borderConfiguration;
    private Effect particle;
    private Cuboid physicalBounds;

    @Getter private static final boolean[] airBlocks = new boolean[256];

    public Border(Location origin,Material material,int size,int height) {
        this.material = Material.BEDROCK;
        this.wrapTerrain = false;
        this.borderConfiguration = BorderConfiguration.DEFAULT_CONFIGURATION;
        this.origin = origin;
        this.size = size;
        this.height = height;
        this.material = material == null ? Material.BEDROCK : material;
        this.physicalBounds = new Cuboid(origin.clone().add(size + 1, (double)origin.getWorld().getMaxHeight() - origin.getY(), size + 1), origin.clone().subtract(size + 1, origin.getY(), size + 1));

        MeetupPlugin.getInstance().getBorderHandler().addBorder(this);
    }

    public Cuboid contract(int amount) {
        this.size -= amount;

        final Cuboid prev = this.physicalBounds.clone();

        this.physicalBounds = this.physicalBounds.inset(Cuboid.CuboidDirection.HORIZONTAL, amount);

        return prev;
    }

    public Cuboid expand(int amount) {

        this.size += amount;

        final Cuboid prev = physicalBounds.clone();

        physicalBounds = physicalBounds.expand(Cuboid.CuboidDirection.NORTH, amount).expand(Cuboid.CuboidDirection.SOUTH, amount).expand(Cuboid.CuboidDirection.EAST, amount).expand(Cuboid.CuboidDirection.WEST, amount);

        return prev;
    }

    /*public Cuboid setSize(int size) {
        return setSize(size, true);
    }

    public Cuboid setSize(int size, boolean callEvent) {
        return setSize(size, this.height, callEvent);
    }

    public Cuboid setSize(int size, int height, boolean callEvent) {
        int previousSize = this.size;
        this.size = size;
        this.height = height;
        Cuboid prev = physicalBounds.clone();

        this.physicalBounds = new Cuboid(this.origin.clone().add(size + 1, (double)this.origin.getWorld().getMaxHeight() - this.origin.getY(), size + 1), this.origin.clone().subtract(size + 1, this.origin.getY(), size + 1));

        if (callEvent) {
            MeetupPlugin.getInstance().getServer().getPluginManager().callEvent(new BorderChangeEvent(this, previousSize, prev, BorderTask.BorderAction.SET));
        }

        return prev;
    } */

    public boolean contains(Block block) {
        return this.contains(block.getX(), block.getZ());
    }

    public boolean contains(Entity entity) {
        return this.contains(entity.getLocation());
    }

    public boolean contains(Location location) {
        return this.contains(location.getBlockX(), location.getBlockZ());
    }

    public boolean contains(int x, int z) {
        return x > this.physicalBounds.getLowerX() && x < this.physicalBounds.getUpperX() && z > this.physicalBounds.getLowerZ() && z < this.physicalBounds.getUpperZ();
    }

    public void fill() {
        World world = this.origin.getWorld();
        int xMin = this.physicalBounds.getLowerX();
        int xMax = this.physicalBounds.getUpperX();
        int zMin = this.physicalBounds.getLowerZ();
        int zMax = this.physicalBounds.getUpperZ();
        int tick = 0;
        int chunksPerTick = 20;

        int chunkX;
        for(chunkX = zMin >> 4; chunkX <= zMax >> 4; ++chunkX) {

            final int finalChunkX = chunkX;

            MeetupPlugin.getInstance().getServer().getScheduler().runTaskLater(MeetupPlugin.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMin >> 4, finalChunkX);

                for(int z = Math.max(zMin, finalChunkX << 4); z < Math.min(zMax, finalChunkX + 1 << 4); ++z) {
                    fillAtXZ(world, chunk, xMin, z);
                }

            }, tick++ / chunksPerTick);
            MeetupPlugin.getInstance().getServer().getScheduler().runTaskLater(MeetupPlugin.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(xMax >> 4, finalChunkX);

                for (int z = Math.max(zMin + 1, finalChunkX << 4); z < Math.min(zMax + 1, finalChunkX + 1 << 4); ++z) {
                    fillAtXZ(world, chunk, xMax, z);
                }

            }, tick++ / chunksPerTick);
        }

        for(chunkX = xMin >> 4; chunkX <= xMax >> 4; ++chunkX) {

            final int finalChunkX = chunkX;

            MeetupPlugin.getInstance().getServer().getScheduler().runTaskLater(MeetupPlugin.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(finalChunkX, zMin >> 4);

                for(int x = Math.max(xMin + 1, finalChunkX << 4); x < Math.min(xMax + 1, finalChunkX + 1 << 4); ++x) {
                    fillAtXZ(world, chunk, x, zMin);
                }

            }, tick++ / chunksPerTick);
            MeetupPlugin.getInstance().getServer().getScheduler().runTaskLater(MeetupPlugin.getInstance(), () -> {
                Chunk chunk = world.getChunkAt(finalChunkX, zMax >> 4);

                for(int x = Math.max(xMin, finalChunkX << 4); x < Math.min(xMax, finalChunkX + 1 << 4); ++x) {
                    fillAtXZ(world, chunk, x, zMax);
                }

            }, tick++ / chunksPerTick);
        }

    }

    private void fillAtXZ(World world, Chunk chunk, int x, int z) {
        int y;
        if (this.wrapTerrain) {
            for (y = world.getHighestBlockYAt(x, z); airBlocks[chunk.getBlock(x, y, z).getType().getId()] && y > 0; --y) {

            }

            for(y += this.height; y >= 0; --y) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
            }
        } else {
            for (y = 0; y <= this.origin.getBlockY() + this.height; ++y) {
                chunk.getBlock(x, y, z).setTypeIdAndData(this.material.getId(), (byte)0, false);
            }
        }

    }

    public Location correctLocation(Location location) {
        Cuboid cuboid = this.getPhysicalBounds();

        int validX = location.getBlockX();
        int validZ = location.getBlockZ();

        EnsureAction xAction = null;
        EnsureAction zAction = null;

        if (location.getBlockX() + 2 > cuboid.getUpperX()) {
            xAction = EnsureAction.DECREASE;
            validX = xAction.apply(cuboid.getUpperX(), 4);
        } else if (location.getBlockX() - 2 < cuboid.getLowerX()) {
            xAction = EnsureAction.INCREASE;
            validX = xAction.apply(cuboid.getLowerX(), 4);
        }

        if (location.getBlockZ() + 2 > cuboid.getUpperZ()) {
            zAction = EnsureAction.DECREASE;
            validZ = zAction.apply(cuboid.getUpperZ(), 4);
        } else if (location.getBlockZ() - 2 < cuboid.getLowerZ()) {
            zAction = EnsureAction.INCREASE;
            validZ = zAction.apply(cuboid.getLowerZ(), 4);
        }

        int validY = location.getWorld().getHighestBlockYAt(validX, validZ);
        Location validLoc = new Location(location.getWorld(), (double)validX + 0.5D, (double)validY + 0.5D, (double)validZ + 0.5D);

        for(int var9 = 0; !isSafe(validLoc) && var9++ < 30; validLoc = new Location(location.getWorld(), (double)validX + 0.5D, (double)validY + 0.5D, (double)validZ + 0.5D)) {
            if (xAction != null) {
                validX = xAction.apply(validX, 1);
            }

            if (zAction != null) {
                validZ = zAction.apply(validZ, 1);
            }

            validY = location.getWorld().getHighestBlockYAt(validX, validZ);
        }

        validLoc.setPitch(location.getPitch());
        validLoc.setYaw(location.getYaw());
        return validLoc;
    }

    private static boolean isSafe(Location location) {
        return location.getBlock().getRelative(BlockFace.DOWN).getType().isSolid() && location.getBlock().isEmpty() && location.getBlock().getRelative(BlockFace.UP).isEmpty();
    }

    public Cuboid getPhysicalBounds() {
        return this.physicalBounds.clone();
    }

    public Border setMaterial(Material material) {
        this.material = material;
        return this;
    }

    public Border setHeight(int height) {
        this.height = height;
        return this;
    }

    public Border setWrapTerrain(boolean wrapTerrain) {
        this.wrapTerrain = wrapTerrain;
        return this;
    }

    public Border setBorderConfiguration(BorderConfiguration borderConfiguration) {
        this.borderConfiguration = borderConfiguration;
        return this;
    }

    public Border setParticle(Effect particle) {
        this.particle = particle;
        return this;
    }

    static {
        airBlocks[Material.LOG.getId()] = true;
        airBlocks[Material.LOG_2.getId()] = true;
        airBlocks[Material.LEAVES.getId()] = true;
        airBlocks[Material.LEAVES_2.getId()] = true;
        airBlocks[Material.HUGE_MUSHROOM_1.getId()] = true;
        airBlocks[Material.HUGE_MUSHROOM_2.getId()] = true;
        airBlocks[Material.SNOW.getId()] = true;

        for (int i = 0; i < Material.values().length; i++) {

            final Material material = Material.values()[i];

            if (material.isBlock() && !material.isSolid()) {
                airBlocks[material.getId()] = true;
            }

        }

        airBlocks[Material.WATER.getId()] = false;
        airBlocks[Material.STATIONARY_WATER.getId()] = false;
        airBlocks[Material.LAVA.getId()] = false;
        airBlocks[Material.STATIONARY_LAVA.getId()] = false;
    }

    public enum EnsureAction {

        INCREASE,
        DECREASE;

        public int apply(int previous, int amount) {
            return this == INCREASE ? previous + amount : previous - amount;
        }

    }
}
