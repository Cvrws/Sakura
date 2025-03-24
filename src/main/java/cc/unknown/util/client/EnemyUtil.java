package cc.unknown.util.client;

import cc.unknown.util.structure.lists.SHashSet;
import net.minecraft.entity.player.EntityPlayer;

public class EnemyUtil {
    private static final SHashSet<String> enemies = new SHashSet<>();

    public static void addEnemy(String target) {
        enemies.add(target.toLowerCase());
    }

    public static void removeEnemy(String target) {
        enemies.remove(target.toLowerCase());
    }

    public static boolean isEnemy(EntityPlayer entityPlayer) {
        return enemies.contains(entityPlayer.getName().toLowerCase());
    }

    public static boolean isEnemy(String target) {
        return enemies.contains(target.toLowerCase());
    }

    public static SHashSet<String> getEnemies() {
        return enemies;
    }

    public static void clearEnemies() {
        enemies.clear();
    }
}