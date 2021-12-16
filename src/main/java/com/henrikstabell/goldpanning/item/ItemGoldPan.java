package com.henrikstabell.goldpanning.item;

import com.henrikstabell.goldpanning.GoldPanning;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ItemGoldPan extends Item {

    public static final ResourceLocation PANNING_LOOT_TABLE = new ResourceLocation(GoldPanning.MOD_ID, "misc/panning");

    public ItemGoldPan() {
        super(new Properties()
                .tab(CreativeModeTab.TAB_TOOLS)
                .stacksTo(1)
                .durability(125)
                .setNoRepair()
        );
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack itemStack) {
        return 20;
    }

    private BlockState getCurrentLookedAtBlock() {
        Minecraft instance = Minecraft.getInstance();

        if(instance.hitResult.getType() != BlockHitResult.Type.BLOCK) {

            Vec3 blockVector = instance.hitResult.getLocation();

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
    public void onUseTick(Level world, LivingEntity entity, ItemStack stack, int tick) {
        if (entity.getItemInHand(entity.getUsedItemHand()).getDamageValue() > 0 && !world.isClientSide()) {
            entity.getItemInHand(entity.getUsedItemHand()).hurtAndBreak(0, entity, e -> e.broadcastBreakEvent(entity.getUsedItemHand()));
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand mainHand) {
        ItemStack stack = player.getItemInHand(mainHand);
        if (!level.isClientSide) {
            LootContext.Builder lootCtxb = (new LootContext.Builder((ServerLevel) level)).withParameter(LootContextParams.ORIGIN, player.position()).withParameter(LootContextParams.TOOL, stack).withRandom(player.getRandom()).withLuck((float)player.getLuck());
            if (player.isCrouching() && player.isInWater() && !player.isEyeInFluid(FluidTags.WATER) && getCurrentLookedAtBlock().getBlock() == Blocks.WATER.defaultBlockState().getBlock()) {
                player.startUsingItem(mainHand);
                stack.hurt(1, player.getRandom(), (ServerPlayer) player);
                if (player.getCommandSenderWorld() instanceof ServerLevel) {
                    LootTable loot = player.level.getServer().getLootTables().get(PANNING_LOOT_TABLE);
                    List<ItemStack> list = loot.getRandomItems(lootCtxb.create(LootContextParamSets.EMPTY));
                    for(ItemStack lootItemStack : list) {
                        player.getInventory().add(lootItemStack);
                    }
                }
            } return InteractionResultHolder.success(player.getItemInHand(mainHand));
        } else return InteractionResultHolder.fail(stack);
    }
}
