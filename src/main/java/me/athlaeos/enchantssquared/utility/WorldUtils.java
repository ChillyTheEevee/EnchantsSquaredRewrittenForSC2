package me.athlaeos.enchantssquared.utility;

import org.bukkit.World;

public class WorldUtils {

    public static boolean isDay(World world) {
        long time = world.getTime();

        return time > 0 && time < 12300;
    }

}
