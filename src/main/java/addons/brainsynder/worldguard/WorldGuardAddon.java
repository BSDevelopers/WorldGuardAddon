package addons.brainsynder.worldguard;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import simplepets.brainsynder.addon.PetAddon;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.event.entity.PetEntitySpawnEvent;

import java.util.List;

@Namespace(namespace = "WorldGuard")
public class WorldGuardAddon extends PetAddon implements Listener {

    private FlagHandler handler;

    @Override
    public boolean shouldEnable() {
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null || !worldGuard.isEnabled()) {
            System.out.println("WorldGuard either wasn't found or isn't enabled.");
            System.out.println("Please ensure it is installed correctly.");
            return false;
        }
        return true;
    }

    @Override
    public void init() {
        Plugin simplePets = Bukkit.getPluginManager().getPlugin("SimplePets");
        if (simplePets == null) {
            return;
        }
        Bukkit.getPluginManager().registerEvents(this, simplePets);
        handler = new FlagHandler();
    }

    @Override
    public double getVersion() {
        return 0.1;
    }

    @Override
    public String getAuthor() {
        return "Thatsmusic99";
    }

    @Override
    public List<String> getDescription() {
        return Lists.newArrayList(
                "&7This addon hooks into",
                "&7WorldGuard so that you can",
                "&7stop specific pet events from",
                "&7occurring in specific regions.",
                "&7(e.g. riding, spawning)");
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPetSpawn(PetEntitySpawnEvent event) {
        Player player = event.getUser().getPlayer().getPlayer();
        if (player == null) return;
        event.setCancelled(!handler.canPetSpawn(player, player.getLocation()));
    }

}
