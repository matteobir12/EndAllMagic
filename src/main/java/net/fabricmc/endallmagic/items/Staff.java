package net.fabricmc.endallmagic.items;

import net.fabricmc.endallmagic.EndAllMagic;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ClickType;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Staff extends Item {

    public Staff(Settings settings){
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity playerEntity, Hand hand) {
        playerEntity.playSound(SoundEvents.BLOCK_WOOL_BREAK, 1.0F, 1.0F);
        return TypedActionResult.success(playerEntity.getStackInHand(hand));
    }
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        context.getPlayer().playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        return super.useOnBlock(context);
    }
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        user.playSound(SoundEvents.BLOCK_BASALT_BREAK, 1.0F, 1.0F);
        return super.useOnEntity(stack, user, entity, hand);
    }
    @Override
    public boolean onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player,
            StackReference cursorStackReference) {
        player.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0F, 1.0F);
        EndAllMagic.LOGGER.info("left clicked item!");
        return super.onClicked(stack, otherStack, slot, clickType, player, cursorStackReference);
    }
    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        miner.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0F, 1.0F);
        EndAllMagic.LOGGER.info("mined");
        return super.postMine(stack, world, state, pos, miner);
    }
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        attacker.playSound(SoundEvents.ENTITY_HOGLIN_ANGRY, 1.0F, 1.0F);
        EndAllMagic.LOGGER.info("hit");
        return super.postHit(stack, target, attacker);
    }
    
}
