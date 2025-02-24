package magicbees.bees.allele.effect;

import java.lang.reflect.Field;

import magicbees.main.utils.compat.ThaumcraftHelper;

import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import forestry.api.apiculture.BeeManager;
import forestry.api.apiculture.IBeeGenome;
import forestry.api.apiculture.IBeeHousing;

public class AlleleEffectSpawnWisp extends AlleleEffectSpawnMob {

    private byte[] wispTypes = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 23, 24, 25,
            26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47 };

    public AlleleEffectSpawnWisp(String id, boolean isDominant, String mobToSpawn, String soundToPlay) {
        super(id, isDominant, mobToSpawn, soundToPlay);
    }

    @Override
    protected boolean spawnMob(IBeeGenome bee, EntityPlayer player, World world, IBeeHousing housing,
            boolean spawnAlternate) {
        boolean spawnedFlag = false;

        EntityLiving mob;
        if (spawnAlternate && this.alternateMob != null) {
            mob = (EntityLiving) EntityList.createEntityByName(this.alternateMob, world);
        } else {
            mob = (EntityLiving) EntityList.createEntityByName(this.mobName, world);
        }

        if (mob != null) {
            double pos[] = this.randomMobSpawnCoords(world, bee, housing);

            int entitiesCount = world.getEntitiesWithinAABB(
                    mob.getClass(),
                    AxisAlignedBB.getBoundingBox(
                            (int) pos[0],
                            (int) pos[1],
                            (int) pos[2],
                            (int) pos[0] + 1,
                            (int) pos[1] + 1,
                            (int) pos[2] + 1).expand(8.0D, 4.0D, 8.0D))
                    .size();

            mob.setPositionAndRotation(pos[0], pos[1], pos[2], world.rand.nextFloat() * 360f, 0f);

            if (entitiesCount < this.maxMobsInArea && mob.getCanSpawnHere()) {
                // Try some Reflection on TC code. This could be dangerous.
                try {
                    Class<?> wispEntity = Class.forName(ThaumcraftHelper.Entity.WISP.getClassName());
                    Field type = wispEntity.getDeclaredField("type");
                    type.setByte(mob, wispTypes[world.rand.nextInt(wispTypes.length)]);
                } catch (Exception e) {
                    /*
                     * Last time I had a request to post error messages, I regretted it.
                     */
                }

                spawnedFlag = world.spawnEntityInWorld(mob);
                if (this.aggosOnPlayer && player != null) {
                    if (BeeManager.armorApiaristHelper.wearsItems((EntityLivingBase) player, getUID(), true) < 4) {
                        // Protect fully suited player from initial murder
                        // intent.
                        mob.setAttackTarget(player);
                    }
                }
            }
        }

        return spawnedFlag;
    }
}
