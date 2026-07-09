package fr.iamacat.optimizationsandtweaks.utils.natives;

import net.minecraft.world.IBlockAccess;

/**
 * Encodes Minecraft block regions into the compact byte format the Rust pathfinder consumes.
 *
 * <p>
 * Only the snapshot path remains: the caller copies a region of the world into an immutable
 * {@code byte[]} on the server thread, then hands it to the async executor. There is no live
 * world access from worker threads anymore (the old {@code WorldAccessAdapter} / per-block JNI
 * path, which read the world off-thread, has been removed).
 */
public class RustPathfindingBridge {

    /**
     * Encode a region of blocks into a byte array for async pathfinding.
     *
     * <p>
     * Layout matches the Rust {@code CachedWorldAccess}: index = {@code y*(width*depth) + z*width + x}
     * (x varies fastest). {@code offsetX/Y/Z} is the world coordinate of the minimum corner; the Rust
     * side treats anything outside the region as air.
     *
     * <p>
     * Must run on the server thread (it reads the live world).
     */
    public static byte[] encodeBlockCache(IBlockAccess world, int offsetX, int offsetY, int offsetZ, int width,
        int height, int depth) {
        byte[] cache = new byte[width * height * depth];
        int index = 0;

        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    cache[index++] = encodeBlock(world, offsetX + x, offsetY + y, offsetZ + z);
                }
            }
        }

        return cache;
    }

    /**
     * Encode a region of blocks directly into an off-heap buffer for the zero-copy submit path
     * (PoC A/B alternative to {@link #encodeBlockCache}). Same block encoding and [y][z][x]
     * layout; writes with relative puts starting at the buffer's current position (the caller
     * positions it past the {@link DirectSnapshotPool} generation header via
     * {@code Slot.beginWrite()}).
     *
     * <p>
     * Must run on the server thread (it reads the live world), and only into a slot that is not
     * in-flight — see the ownership protocol on {@link DirectSnapshotPool}.
     */
    public static void encodeBlockCacheDirect(IBlockAccess world, int offsetX, int offsetY, int offsetZ, int width,
        int height, int depth, java.nio.ByteBuffer target) {
        for (int y = 0; y < height; y++) {
            for (int z = 0; z < depth; z++) {
                for (int x = 0; x < width; x++) {
                    target.put(encodeBlock(world, offsetX + x, offsetY + y, offsetZ + z));
                }
            }
        }
    }

    private static byte encodeBlock(IBlockAccess world, int x, int y, int z) {
        try {
            net.minecraft.block.Block b = world.getBlock(x, y, z);
            int id = net.minecraft.block.Block.getIdFromBlock(b);
            int meta = 0;
            try {
                meta = world.getBlockMetadata(x, y, z);
            } catch (Throwable ignore) {}

            if (id == 0) return 0; // Air
            if (id == 8 || id == 9) return 2; // Water
            if (id == 10 || id == 11) return 3; // Lava

            // Doors
            if (id == 64 || id == 71) {
                int m = meta;
                if ((m & 0x8) != 0) { // upper half
                    try {
                        m = world.getBlockMetadata(x, y - 1, z);
                    } catch (Throwable ignore) {}
                }
                boolean open = (m & 0x4) != 0;
                return open ? (byte) 0 : (byte) 4;
            }

            // Trapdoor
            if (id == 96) {
                boolean open = (meta & 0x4) != 0;
                return open ? (byte) 0 : (byte) 5;
            }

            // Fences
            if (id == 85 || id == 113 || (id >= 188 && id <= 192)) return 6;

            // FenceGate
            if (id == 107) {
                boolean open = (meta & 0x4) != 0;
                return open ? (byte) 0 : (byte) 7;
            }

            // Slabs & stairs
            if (id == 44 || id == 126) return 0;
            switch (id) {
                case 53:
                case 67:
                case 108:
                case 109:
                case 114:
                case 128:
                case 134:
                case 135:
                case 136:
                case 156:
                case 163:
                case 164:
                    return 0;
                default:
            }

            // Slime, Vine, Ladder, Cobweb
            if (id == 165) return 8; // Slime Block
            if (id == 106) return 9; // Vine
            if (id == 65) return 10; // Ladder
            if (id == 30) return 11; // Cobweb

            // Default solid check
            try {
                net.minecraft.block.material.Material m = b.getMaterial();
                boolean solid = m != null && m.blocksMovement();
                return solid ? (byte) 1 : (byte) 0;
            } catch (Throwable t) {
                return 0;
            }
        } catch (Throwable t) {
            return 0;
        }
    }
}
