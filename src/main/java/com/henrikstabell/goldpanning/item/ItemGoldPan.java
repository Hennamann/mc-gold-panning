package com.henrikstabell.goldpanning.item;

import com.henrikstabell.goldpanning.GoldPanning;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

public class ItemGoldPan extends Item {

    public static final ResourceLocation PANNING_LOOT_TABLE = new ResourceLocation(GoldPanning.MOD_ID, "misc/panning");

    public ItemGoldPan() {
        super(new Properties()
                .tab(ItemGroup.TAB_TOOLS)
                .stacksTo(1)
                .durability(125)
                .setNoRepair()
        );
    }

    @Override
    public UseAction getUseAnimation(ItemStack itemStack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 20;
    }

    private BlockState getCurrentLookedAtBlock() {
        Minecraft instance = Minecraft.getInstance();

        if(instance.hitResult.getType() != RayTraceResult.Type.BLOCK) {

            Vector3d blockVector = instance.hitResult.getLocation();

            double bX = blockVector.x();
            double bY = blockVector.y();
            double bZ = blockVector.z();
            double pX = instance.player.getX();
            double pY = instance.player.getY();
            double pZ = instance.player.getZ();

            if (bX == Math.floor(bX) && bX <= pX) {
                bX--;
            }
            if (bY == Math.floor(bY) && bY <= pY + 1) {
                bY--;
            } // +1 on Y to get y from player eyes instead of feet
            if (bZ == Math.floor(bZ) && bZ <= pZ) {
                bZ--;
            }

            return instance.level.getBlockState(new BlockPos(bX, bY, bZ));
        } else return Blocks.AIR.defaultBlockState();
    }

    @Override
    public void onUseTick(World world, LivingEntity entity, ItemStack stack, int tick) {
        if (entity.getItemInHand(entity.getUsedItemHand()).getDamageValue() > 0 && !world.isClientSide()) {
            entity.getItemInHand(entity.getUsedItemHand()).hurtAndBreak(0, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
        }
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand mainHand) {
        ItemStack stack = player.getItemInHand(mainHand);
        if (!world.isClientSide) {
            LootContext.Builder lootCtxb = (new LootContext.Builder((ServerWorld) world)).withParameter(LootParameters.ORIGIN, player.position()).withParameter(LootParameters.TOOL, stack).withRandom(this.random).withLuck((float)player.getLuck());
            if (player.isCrouching() && player.isInWater() && !player.isEyeInFluid(FluidTags.WATER) && getCurrentLookedAtBlock().getBlock() == Blocks.WATER.getBlock()) {
                player.startUsingItem(mainHand);
                stack.hurt(1, player.getRandom(), (ServerPlayerEntity) player);
                if (player.getCommandSenderWorld() instanceof ServerWorld) {
                    LootTable loot = player.level.getServer().getLootTables().get(PANNING_LOOT_TABLE);
                    List<ItemStack> list = loot.getRandomItems(lootCtxb.create(LootParameterSets.EMPTY));
                    for(ItemStack lootItemStack : list) {
                        player.inventory.add(lootItemStack);
                    }
                }
            } return ActionResult.success(player.getItemInHand(mainHand));
        } else return ActionResult.fail(stack);
    }
}
