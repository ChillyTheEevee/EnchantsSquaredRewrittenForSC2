package me.athlaeos.enchantssquared.commands.shadowcraft_commands;

import me.athlaeos.enchantssquared.commands.Command;
import me.athlaeos.enchantssquared.managers.CustomEnchantManager;
import me.athlaeos.enchantssquared.utility.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.List;

public class UpdateEnchantmentsCommand implements Command {
    @Override
    public boolean execute(CommandSender sender, String[] args) {
        if (args.length == 1) {
            return executeNoArguments(sender);
        } else if (args.length == 2) {
            return executeOnPlayer(sender, args[1]);
        } else {
            return false;
        }
    }

    @Override
    public String[] getRequiredPermission() {
        return new String[]{"es.update"};
    }

    @Override
    public String getFailureMessage() {
        return "/es update";
    }

    @Override
    public String[] getHelpEntry() {
        return new String[]{
                ChatUtils.chat("&8&m                                             "),
                ChatUtils.chat("&d/es update"),
                ChatUtils.chat("&7Updates the Player's held item from the legacy enchantment system"),
                ChatUtils.chat("&7> &des.enchant")
        };
    }

    @Override
    public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
        return null;
    }

    private boolean executeNoArguments(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Unknown target player.!");
            return true;
        }

        return executeOnPlayer(sender, player.getName());
    }

    private boolean executeOnPlayer(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);

        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Cannot find a player with the specified name.");
            return true;
        }
        PlayerInventory playerInventory = player.getInventory();

        ItemStack[] items = playerInventory.getContents();
        ItemStack[] extraContents = playerInventory.getExtraContents();

        for (ItemStack item : items) {
            if (item != null) {
                CustomEnchantManager.getInstance().updateItem(item);
            }
        }

        for (ItemStack item : extraContents) {
            if (item != null) {
                CustomEnchantManager.getInstance().updateItem(item);
            }
        }

        player.sendMessage(ChatColor.GREEN + "Inventory successfully updated!");
        return true;
    }
}
