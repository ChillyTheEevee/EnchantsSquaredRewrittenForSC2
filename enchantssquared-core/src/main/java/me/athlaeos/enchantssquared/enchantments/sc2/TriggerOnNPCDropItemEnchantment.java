package me.athlaeos.enchantssquared.enchantments.sc2;

import com.github.sirblobman.combatlogx.api.event.NPCDropItemEvent;

/**
 * Custom SC2 Enchantment Type
 */
public interface TriggerOnNPCDropItemEnchantment {

    void onNPCDropItem(NPCDropItemEvent e, int level);

}
