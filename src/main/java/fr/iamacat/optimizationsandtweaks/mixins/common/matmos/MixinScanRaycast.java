package fr.iamacat.optimizationsandtweaks.mixins.common.matmos;

import eu.ha3.matmos.data.scanners.Scan;
import eu.ha3.matmos.data.scanners.ScanAir;
import eu.ha3.matmos.data.scanners.ScanRaycast;
import eu.ha3.matmos.data.scanners.ScannerModule;
import eu.ha3.matmos.util.BlockPos;
import eu.ha3.matmos.util.MAtUtil;
import eu.ha3.matmos.util.Vec3d;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.block.BlockLeaves;
import net.minecraft.client.Minecraft;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

@Mixin(ScanRaycast.class)
public abstract class MixinScanRaycast  extends Scan {
    @Shadow
    private static final Random rnd = new Random();
    @Shadow
    int startX;
    @Shadow
    int startY;
    @Shadow
    int startZ;
    @Shadow
    Vec3d center;
    @Shadow
    int xSize;
    @Shadow
    int ySize;
    @Shadow
    int zSize;
    @Shadow
    int startNearness;
    @Shadow
    int maxRange;
    @Shadow
    int raysCast = 0;
    @Shadow
    int raysToCast;
    @Shadow
    int directScore;
    @Shadow
    int indirectScore;
    @Shadow
    int distanceSqSum;
    @Shadow
    private int THRESHOLD_SCORE;
    @Shadow
    private int THRESHOLD_INDIRECT_SCORE;
    @Shadow
    private Vec3d[] rays;
    @Shadow
    private Set<BlockPos> scanned;
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    void initScan(int x, int y, int z, int xsizeIn, int ysizeIn, int zsizeIn, int opspercallIn) {
        this.startX = x;
        this.startY = y;
        this.startZ = z;
        this.center = new Vec3d(this.startX + 0.5, this.startY + 0.5, this.startZ + 0.5);
        this.xSize = xsizeIn;
        this.ySize = ysizeIn;
        this.zSize = zsizeIn;
        this.raysToCast = this.opspercall * 20;
        this.raysCast = 0;
        this.distanceSqSum = 0;
        if (this.rays == null || this.rays.length != this.raysToCast) {
            rnd.setSeed(1L);
            this.rays = new Vec3d[this.raysToCast];

            for(int i = 0; i < this.raysToCast; ++i) {
                double vx = 0.0;
                double vy = 0.0;

                double vz;
                double squareDist;
                for(vz = 0.0; (squareDist = vx * vx + vy * vy + vz * vz) < 0.01 || squareDist > 1.0; vz = 2.0 * (rnd.nextDouble() - 0.5)) {
                    vx = 2.0 * (rnd.nextDouble() - 0.5);
                    vy = 2.0 * (rnd.nextDouble() - 0.5);
                }

                this.rays[i] = (new Vec3d(vx, vy, vz)).normalize();
            }
        }

        if (this.scanned == null) {
            this.scanned = new HashSet<>(this.raysToCast + 1, 1.0F);
        }

        this.finalProgress = 1;
        this.startNearness = 60;
        this.maxRange = 100;
        this.directScore = 0;
        this.indirectScore = 0;
        this.THRESHOLD_SCORE = 10000;
        this.THRESHOLD_INDIRECT_SCORE = 10;
        this.scanned.clear();
    }
    @Shadow
    private Vec3d getRay(int index) {
        return this.rays[index];
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private Optional<Vec3d> getNormalVector(MovingObjectPosition result) {
        if (result == null) {
            return Optional.empty();
        } else {
            switch (result.sideHit) {
                case 0:
                    return Optional.of(new Vec3d(0.0, -1.0, 0.0));
                case 1:
                    return Optional.of(new Vec3d(0.0, 1.0, 0.0));
                case 2:
                    return Optional.of(new Vec3d(0.0, 0.0, 1.0));
                case 3:
                    return Optional.of(new Vec3d(0.0, 0.0, -1.0));
                case 4:
                    return Optional.of(new Vec3d(-1.0, 0.0, 0.0));
                case 5:
                    return Optional.of(new Vec3d(1.0, 0.0, 0.0));
                default:
                    return Optional.empty();
            }
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    protected boolean doRoutine() {
        int ops = 0;
        while (ops < this.opspercall && this.raysCast < this.raysToCast) {
            this.castRay(this.getRay(this.raysCast));
            ops++;
            this.raysCast++;
        }

        if (this.raysCast >= this.raysToCast) {
            this.progress = 1;
            int isOutdoors = this.directScore > this.THRESHOLD_SCORE ? 1 : 0;
            int isNearSurfaceOwn = this.indirectScore > this.THRESHOLD_INDIRECT_SCORE ? 1 : 0;
            this.pipeline.setValue(".is_outdoors", isOutdoors);
            this.pipeline.setValue(".__score", this.directScore);
            this.pipeline.setValue(".__indirect_score", this.indirectScore);
            this.pipeline.setValue("._is_near_surface_own", isNearSurfaceOwn);
            this.pipeline.setValue(".spaciousness", this.distanceSqSum);
        }

        return true;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private int calculateWeight(int dx, int dy, int dz, int maxRange) {
        int distanceSquared = dx * dx + dy * dy + dz * dz;
        float distanceScale = 1.0F;
        float weight = MathHelper.clamp_float(1.0F / (1.0F + distanceSquared / distanceScale), 0.0F, 1.0F);
        return (int) (weight * 1000.0F);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    public static MovingObjectPosition rayTraceNonSolid(Vec3d start, Vec3d end) {
        World world = Minecraft.getMinecraft().theWorld;
        MovingObjectPosition result = world.func_147447_a(start, end, true, false, true);

        Vec3d delta = end.subtract(start);
        double infNorm = Math.max(Math.abs(delta.xCoord), Math.max(Math.abs(delta.yCoord), Math.abs(delta.zCoord)));
        delta = delta.scale(0.01 / infNorm);

        while (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK &&
            ScanAir.isTransparentToSound(MAtUtil.getBlockAt(new BlockPos(result.blockX, result.blockY, result.blockZ)),
                MAtUtil.getMetaAt(new BlockPos(result.hitVec), -1), world, new BlockPos(result.hitVec), true)) {
            result = world.func_147447_a(delta.add(result.hitVec), end, true, true, true);
        }

        return result;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private boolean scanNearRayHit(MovingObjectPosition result, int scanDistance, boolean direct) {
        World world = Minecraft.getMinecraft().theWorld;
        BlockPos hitBlock = new BlockPos(result.blockX, result.blockY, result.blockZ);
        this.distanceSqSum += hitBlock.distanceSq(this.center.xCoord, this.center.yCoord, this.center.zCoord);
        Block[] blockBuf = new Block[1];
        int[] metaBuf = new int[1];
        int[] pos = new int[3];
        boolean centerSolid = false;
        boolean foundSky = false;
        int airPenalty = 0;
        int solidPenalty = 55;

        for (int scanDir = 0; scanDir < 6; ++scanDir) {
            int nearness = this.startNearness;

            for (int offset = 0; offset <= scanDistance; ++offset) {
                if (offset != 0 || scanDir == 0) {
                    int scanAxis = scanDir >= 3 ? scanDir - 3 : scanDir;
                    pos[0] = hitBlock.getX();
                    pos[1] = hitBlock.getY();
                    pos[2] = hitBlock.getZ();
                    pos[scanAxis] += offset * (scanDir >= 3 ? -1 : 1);
                    BlockPos blockPos = new BlockPos(pos[0], pos[1], pos[2]);

                    if (!this.scanned.contains(blockPos)) {
                        this.scanned.add(blockPos);
                        int dx = this.startX - pos[0];
                        int dy = this.startY - pos[1];
                        int dz = this.startZ - pos[2];

                        if (direct) {
                            ((ScannerModule) this.pipeline).inputAndReturnBlockMeta(pos[0], pos[1], pos[2], this.calculateWeight(dx, dy, dz, this.maxRange), blockBuf, metaBuf);
                        } else {
                            blockBuf[0] = MAtUtil.getBlockAt(blockPos);
                            metaBuf[0] = MAtUtil.getMetaAt(blockPos, -1);
                        }

                        Block block = blockBuf[0];
                        AxisAlignedBB collisionBox = block.getCollisionBoundingBoxFromPool(world, blockPos.getX(), blockPos.getY(), blockPos.getZ());
                        boolean solid = collisionBox != null && !(block instanceof BlockLeaves);

                        if (solid && offset == 0 && scanDir == 0) {
                            centerSolid = true;
                        } else if (centerSolid && scanDir != 0 && offset == 1) {
                            nearness -= solidPenalty;
                        }

                        nearness -= solid ? solidPenalty : airPenalty;

                        if (nearness > 0 && block instanceof BlockAir && MAtUtil.canSeeSky(blockPos)) {
                            if (direct) {
                                this.directScore += nearness;
                            } else {
                                ++this.indirectScore;
                            }
                            foundSky = true;
                        }
                    }
                }
            }
        }

        return foundSky;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite(remap = false)
    private void castRay(Vec3d dir) {
        Vec3d end = this.center.add(dir.scale(this.maxRange));
        MovingObjectPosition result = rayTraceNonSolid(this.center, end);

        if (result != null && result.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            boolean foundSky = this.scanNearRayHit(result, 2, true);

            if (!foundSky) {
                Vec3d normal = this.getNormalVector(result).orElse(Vec3d.ZERO);

                if (!normal.equals(Vec3d.ZERO)) {
                    Vec3d otherSide = normal.scale(-1.1).add(result.hitVec);
                    MovingObjectPosition continuedResult = rayTraceNonSolid(otherSide, end);

                    if (continuedResult != null) {
                        this.scanNearRayHit(continuedResult, 1, false);
                    } else if (dir.yCoord > 0.0) {
                        this.indirectScore += 7;
                    }
                }
            }
        } else {
            this.distanceSqSum += this.maxRange * this.maxRange;

            if (dir.yCoord > 0.0) {
                Vec3d rayEnd = this.center.add(dir.scale(this.maxRange));
                BlockPos rayEndBlockPos = new BlockPos(rayEnd);

                if (MAtUtil.canSeeSky(rayEndBlockPos)) {
                    this.directScore += this.startNearness * 13;
                }
            }
        }
    }
}
