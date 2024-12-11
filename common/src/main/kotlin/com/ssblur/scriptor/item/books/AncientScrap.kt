package com.ssblur.scriptor.item.books

import com.ssblur.scriptor.events.reloadlisteners.ScrapReloadListener
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level

class AncientScrap(properties: Properties, var tier: Int) : Item(properties) {
    override fun appendHoverText(
        itemStack: ItemStack,
        level: TooltipContext,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(itemStack, level, list, tooltipFlag)
        list.add(Component.translatable("extra.scriptor.scrap_description"))
        list.add(Component.translatable("extra.scriptor.scrap_tier", tier))
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand
    ): InteractionResultHolder<ItemStack> {
        val result = super.use(level, player, interactionHand)

        if (!level.isClientSide) {
            player.sendSystemMessage(Component.translatable("extra.scriptor.scrap_use"))
            player.cooldowns.addCooldown(this, 20)

            // Generate and distribute scrap
            val scrap = ScrapReloadListener.INSTANCE.getRandomScrap(tier, player)


            if (!player.addItem(scrap)) {
                val entity = ItemEntity(
                    level,
                    player.x,
                    player.y + 1,
                    player.z + 1,
                    scrap
                )
                level.addFreshEntity(entity)
            }
            player.sendSystemMessage(Component.translatable("extra.scriptor.scrap_get"))
            player.getItemInHand(interactionHand).shrink(1)
            return InteractionResultHolder.consume(player.getItemInHand(interactionHand))
        }

        return result
    }
}
