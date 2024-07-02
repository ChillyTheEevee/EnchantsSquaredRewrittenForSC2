package me.athlaeos.enchantssquared.enchantments.on_death;

import com.github.sirblobman.combatlogx.api.event.NPCDropItemEvent;
import me.athlaeos.enchantssquared.config.Config;
import me.athlaeos.enchantssquared.config.ConfigManager;
import me.athlaeos.enchantssquared.domain.MaterialClassType;
import me.athlaeos.enchantssquared.enchantments.CustomEnchant;
import me.athlaeos.enchantssquared.enchantments.LevelService;
import me.athlaeos.enchantssquared.enchantments.Levels1IfPresentInInventory;
import me.athlaeos.enchantssquared.enchantments.sc2.TriggerOnNPCDropItemEnchantment;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utility.ItemSerializer;
import me.athlaeos.enchantssquared.utility.ItemUtils;
import me.athlaeos.enchantssquared.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class Soulbound extends CustomEnchant implements TriggerOnDeathEnchantment, TriggerOnNPCDropItemEnchantment {

    private final Config storedItemDataFile;
    private final YamlConfiguration config;
    private final Collection<String> incompatibleVanillaEnchantments;
    private final Collection<String> incompatibleCustomEnchantments;

    /**
     * Constructor for a Custom Enchant. The type and id must be unique and the type will automatically be uppercased
     * by convention.
     * The id will be used on the item to store the encha\ntment and thus must be consistent, or it will risk
     * changing existing enchantments on item or simply invalidate the enchantment entirely.
     *
     * @param id   the identifying id of this custom enchant.
     * @param type the identifying type of this custom enchant.
     */
    public Soulbound(int id, String type) {
        super(id, type);
        this.config = ConfigManager.getInstance().getConfig("config.yml").get();
        this.storedItemDataFile = ConfigManager.getInstance().getConfig("data/stored_soulbound_items.yml");
        this.naturallyCompatibleWith = new HashSet<>(config.getStringList("enchantment_configuration.soulbound.compatible_with"));
        this.incompatibleVanillaEnchantments = new HashSet<>(config.getStringList("enchantment_configuration.soulbound.incompatible_vanilla_enchantments"));
        this.incompatibleCustomEnchantments = new HashSet<>(config.getStringList("enchantment_configuration.soulbound.incompatible_custom_enchantments"));

        this.singleUse = config.getBoolean("enchantment_configuration.soulbound.single_use");
        this.chanceBase = config.getDouble("enchantment_configuration.soulbound.chance_base");
        this.chanceLv = config.getDouble("enchantment_configuration.soulbound.chance_lv");
        this.icon = ItemUtils.getIconFromConfig(config, "enchantment_configuration.soulbound.icon", createIcon(Material.END_CRYSTAL));
    }

    private final LevelService levelService = new Levels1IfPresentInInventory(this);

    @Override
    public LevelService getLevelService(boolean offHand, LivingEntity entity) {
        return levelService;
    }

    @Override
    public String getDisplayEnchantment() {
        return config.getString("enchantment_configuration.soulbound.enchant_name", getType())
                .replace(" %lv_roman%", "")
                .replace(" %lv_number%", "");
    }

    @Override
    public String getDescription() {
        return config.getString("enchantment_configuration.soulbound.description");
    }

    @Override
    public boolean isEnabled() {
        return config.getBoolean("enchantment_configuration.soulbound.enabled");
    }

    @Override
    public String getRequiredPermission() {
        return "es.enchant.soulbound";
    }

    @Override
    public boolean conflictsWithEnchantment(String enchantment) {
        return incompatibleCustomEnchantments.contains(enchantment) || incompatibleVanillaEnchantments.contains(enchantment);
    }

    private final Collection<String> naturallyCompatibleWith;

    @Override
    public boolean isNaturallyCompatible(Material material) {
        return MaterialClassType.isMatchingClass(material, naturallyCompatibleWith);
    }

    @Override
    public boolean isFunctionallyCompatible(Material material) {
        return true;
    }

    @Override
    public int getWeight() {
        return config.getInt("enchantment_configuration.soulbound.weight");
    }

    @Override
    public int getMaxLevel() {
        return config.getInt("enchantment_configuration.soulbound.max_level");
    }

    @Override
    public int getMaxTableLevel() {
        return config.getInt("enchantment_configuration.soulbound.max_level_table");
    }

    @Override
    public boolean isTreasure() {
        return config.getBoolean("enchantment_configuration.soulbound.is_treasure");
    }

    @Override
    public boolean isBookOnly() {
        return config.getBoolean("enchantment_configuration.soulbound.book_only");
    }

    @Override
    public boolean isTradingEnabled() {
        return config.getBoolean("enchantment_configuration.soulbound.trade_enabled");
    }

    @Override
    public int getTradingMinBasePrice() {
        return config.getInt("enchantment_configuration.soulbound.trade_cost_base_lower");
    }

    @Override
    public int getTradingMaxBasePrice() {
        return config.getInt("enchantment_configuration.soulbound.trade_cost_base_upper");
    }

    @Override
    public int getTradingMinLeveledPrice() {
        return config.getInt("enchantment_configuration.soulbound.trade_cost_lv_lower");
    }

    @Override
    public int getTradingMaxLeveledPrice() {
        return config.getInt("enchantment_configuration.soulbound.trade_cost_base_upper");
    }

    private final ItemStack icon;

    @Override
    public ItemStack getIcon() {
        return icon;
    }


    @Override
    public String getWorldGuardFlagName() {
        return "es-deny-soulbound";
    }

    @Override
    public Collection<String> getCompatibleItems() {
        return naturallyCompatibleWith;
    }

    private final double chanceBase;
    private final double chanceLv;
    private final boolean singleUse;

    @Override
    public void onEntityDeath(EntityDeathEvent e, int level) {
        // do nothing
    }

    @Override
    public void onEntityKilled(EntityDeathEvent e, int level) {
        // do nothing
    }

    @Override
    public void onPlayerDeath(PlayerDeathEvent e, int level) {
        if (shouldEnchantmentCancel(level, e.getEntity(), e.getEntity().getLocation())) return;
        List<ItemStack> itemsToSave = new ArrayList<>();
        for (ItemStack i : new ArrayList<>(e.getDrops())) {
            if (ItemUtils.isAirOrNull(i)) continue;
            int soulboundLevel = CustomEnchantManager.getInstance().getEnchantStrength(i, getType());
            if (soulboundLevel <= 0) continue;
            double preservationChance = chanceBase + ((level - 1) * chanceLv);
            if (Utils.getRandom().nextDouble() <= preservationChance) {
                // preserve item
                if (e.getDrops().contains(i)) {
                    e.getDrops().remove(i);
                    if (singleUse) CustomEnchantManager.getInstance().removeEnchant(i, getType());
                    itemsToSave.add(i);
                }
            }
        }

        if (!itemsToSave.isEmpty()) {
            List<String> hashedItems = itemsToSave.stream().map(ItemSerializer::toBase64).collect(Collectors.toList());
            String playerUUIDString = e.getEntity().getUniqueId().toString();
            storedItemDataFile.get().set(playerUUIDString, String.join("<<<item>>>", hashedItems));
            storedItemDataFile.save();
        }
    }

    @Override
    public void onPlayerKilled(PlayerDeathEvent e, int level) {
        // do nothing
    }

    @Override
    public void onPlayerRespawn(PlayerRespawnEvent e, int level) {
        String playerUUIDString = e.getPlayer().getUniqueId().toString();
        String encodedItemString = storedItemDataFile.get().getString(playerUUIDString);
        if (encodedItemString != null && !encodedItemString.equals(" ")) {
            String[] itemHashes = encodedItemString.split("<<<item>>>");
            for (String i : itemHashes) {
                ItemStack stack = ItemSerializer.itemStackFromBase64(i);
                ItemUtils.addItem(e.getPlayer(), stack, true);
            }
            storedItemDataFile.get().set(playerUUIDString, " ");
            storedItemDataFile.save();
        }
    }

    @Override
    public void onNPCDropItem(NPCDropItemEvent e, int level) {
        if (level <= 0) {
            return;
        }
        ItemStack item = e.getItem();

        double preservationChance = chanceBase + ((level - 1) * chanceLv);
        if (Utils.getRandom().nextDouble() <= preservationChance) {
            // preserve item
            e.setCancelled(true);
            if (singleUse) CustomEnchantManager.getInstance().removeEnchant(item, getType());
            String playerUUIDString = e.getPlayer().getUniqueId().toString();

            String encodedItems = storedItemDataFile.get().getString(playerUUIDString, "");
            if (encodedItems.equals(" ")) {
                encodedItems = ItemSerializer.toBase64(item);
            } else {
                encodedItems = encodedItems + "<<<item>>>" + ItemSerializer.toBase64(item);
            }
            storedItemDataFile.get().set(playerUUIDString, encodedItems);
            storedItemDataFile.save();
        }
    }

}
