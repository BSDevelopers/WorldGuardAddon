package addon.brainsynder.worldguard;

import com.google.common.collect.Lists;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import simplepets.brainsynder.addon.AddonPermissions;
import simplepets.brainsynder.addon.PermissionData;
import simplepets.brainsynder.addon.PetAddon;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.event.entity.PetEntitySpawnEvent;
import simplepets.brainsynder.api.event.entity.PetMoveEvent;
import simplepets.brainsynder.api.event.entity.movment.PetRideEvent;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.debug.DebugLevel;

import java.util.List;

@Namespace(namespace = "WorldGuard")
public class WorldGuardAddon extends PetAddon implements Listener {

    private FlagHandler handler;

    @Override
    public boolean shouldEnable() {
        Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (worldEdit == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "WorldEdit wasn't found!");
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "Please ensure it is installed correctly.");
            return false;
        }
        Plugin worldGuard = Bukkit.getPluginManager().getPlugin("WorldGuard");
        if (worldGuard == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "WorldGuard wasn't found!");
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "Please ensure it is installed correctly.");
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
        handler = new FlagHandler();
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered flags. (Hopefully)");
        Bukkit.getPluginManager().registerEvents(this, simplePets);
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered listeners.");
        AddonPermissions.register(this, new PermissionData("pet.bypass.worldguard"));
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered permission.");
    }

    @Override
    public double getVersion() {
        return 0.4;
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

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetSpawn(PetEntitySpawnEvent event) {
        Player player = event.getUser().getPlayer();
        if (player == null) return;
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return;
        }
        event.setCancelled(!handler.canPetSpawn(player, player.getLocation()));
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Is PetEntitySpawnEvent cancelled after WG check: " + event.isCancelled());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetMove(PetMoveEvent event) {
        if (event instanceof PetRideEvent) return;
        Player player = event.getEntity().getPetUser().getPlayer();
        if (player == null) return;
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return;
        }
        // TODO - remove pet?
        event.setCancelled(!handler.canPetEnter(player, event.getTargetLocation()));
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPetRide(PetRideEvent event) {
        Player player = event.getEntity().getPetUser().getPlayer();
        if (player == null) return;
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR, "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return;
        }
        event.setCancelled(!handler.canRidePet(player, event.getTargetLocation()));
    }
}
