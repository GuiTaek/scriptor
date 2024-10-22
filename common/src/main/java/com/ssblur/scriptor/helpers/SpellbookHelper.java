package com.ssblur.scriptor.helpers;

import com.ssblur.scriptor.advancement.ScriptorAdvancements;
import com.ssblur.scriptor.data.DictionarySavedData;
import com.ssblur.scriptor.effect.EmpoweredStatusEffect;
import com.ssblur.scriptor.gamerules.ScriptorGameRules;
import com.ssblur.scriptor.helpers.targetable.SpellbookTargetable;
import com.ssblur.scriptor.word.Spell;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class SpellbookHelper {
  public static List<Item> SPELLBOOKS = new ArrayList<>();

  /**
   * Casts a spell from an item with a Written Book component.
   * @param itemStack The itemstack to cast from.
   *                  Fails if this does not have a Written Book component.
   * @param player The player casting the spell.
   * @return true if casting succeeded
   */
  public static boolean castFromItem(ItemStack itemStack, Player player) {
    var text = itemStack.get(DataComponents.WRITTEN_BOOK_CONTENT);
    var level = player.level();
    if(text == null || !(level instanceof ServerLevel server))
      return false;

    level.playSound(null, player.blockPosition(), SoundEvents.EVOKER_CAST_SPELL, SoundSource.PLAYERS, 0.4F, level.getRandom().nextFloat() * 1.2F + 0.6F);

    Spell spell = DictionarySavedData.computeIfAbsent(server).parse(LimitedBookSerializer.decodeText(text));
    if(spell != null) {
      int maxCost = level.getGameRules().getInt(ScriptorGameRules.TOME_MAX_COST);
      if(spell.cost() > maxCost || maxCost == -1) {
        player.sendSystemMessage(Component.translatable("extra.scriptor.fizzle"));
        ScriptorAdvancements.FIZZLE.get().trigger((ServerPlayer) player);
        if(!player.isCreative())
          addCooldown(player, (int) Math.round( 350.0D * ( (double) level.getGameRules().getInt(ScriptorGameRules.TOME_COOLDOWN_MULTIPLIER) / (double) 100) ));
        return true;
      }
      spell.cast(new SpellbookTargetable(itemStack, player, player.getInventory().selected).withTargetItem(false));
      if(!player.isCreative()) {
        double costScale = 1.0;
        for(var instance: player.getActiveEffects())
          if(instance.getEffect().value() instanceof EmpoweredStatusEffect empoweredStatusEffect)
            for(int i = 0; i <= instance.getAmplifier(); i++)
              costScale *= empoweredStatusEffect.getScale();
		    double adjustedCost = costScale * spell.cost() * ((double) level.getGameRules().getInt(ScriptorGameRules.TOME_COOLDOWN_MULTIPLIER) / (double) 100);
        addCooldown(player, (int) Math.round(adjustedCost * 7));
	  }
      return false;
    }
    return true;
  }

  static void addCooldown(Player player, int time) {
    for(var spellbook: SPELLBOOKS)
      player.getCooldowns().addCooldown(spellbook, time);
  }
}
