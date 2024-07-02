package me.athlaeos.enchantssquared.listeners.shadowcraft_listeners;

import com.github.sirblobman.combatlogx.api.event.NPCDropItemEvent;
import me.athlaeos.enchantssquared.enchantments.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.sc2.TriggerOnNPCDropItemEnchantment;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class NPCDropItemListener implements Listener {

    private Collection<CustomEnchant> triggerOnNpcDropItemEnchants;
    @EventHandler
    public void onNpcDropItemEvent(NPCDropItemEvent event) {
        if (triggerOnNpcDropItemEnchants == null) {
            triggerOnNpcDropItemEnchants = CustomEnchantManager.getInstance()
                    .getEnchantmentsMatchingFilter(c -> c instanceof TriggerOnNPCDropItemEnchantment);
        }
        ItemStack item = event.getItem();
        for (CustomEnchant enchant : triggerOnNpcDropItemEnchants) {
            int level = CustomEnchantManager.getInstance().getEnchantStrength(item, enchant.getType());
            ((TriggerOnNPCDropItemEnchantment) enchant).onNPCDropItem(event, level);
        }
    }

}
