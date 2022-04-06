package addon.brainsynder.worldguard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import simplepets.brainsynder.addon.AddonPermissions;
import simplepets.brainsynder.addon.PermissionData;
import simplepets.brainsynder.addon.presets.RegionModule;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.api.user.PetUser;
import simplepets.brainsynder.debug.DebugLevel;

@Namespace(namespace = "WorldGuard")
public class WorldGuardAddon extends RegionModule implements Listener {

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
    public boolean isSpawningAllowed(PetUser petUser, Location location) {
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR,
                    "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return true;
        }
        return handler.canPetSpawn(petUser.getPlayer(), location);
    }

    @Override
    public boolean isMovingAllowed(PetUser petUser, Location location) {
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR,
                    "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return true;
        }
        return handler.canPetEnter(petUser.getPlayer(), location);
    }

    @Override
    public boolean isRidingAllowed(PetUser petUser, Location location) {
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR,
                    "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return true;
        }
        return handler.canRidePet(petUser.getPlayer(), location);
    }

    @Override
    public boolean isMountingAllowed(PetUser petUser, Location location) {
        if (handler == null) {
            SimplePets.getDebugLogger().debug(DebugLevel.ERROR,
                    "WorldGuard 'handler' is not set... Did something happen when initializing?");
            return true;
        }
        return handler.canMountPet(petUser.getPlayer(), location);
    }

    @Override
    public void init() {
        handler = new FlagHandler();
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered flags. (Hopefully)");
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered listeners.");
        AddonPermissions.register(this, new PermissionData("pet.bypass.worldguard"));
        SimplePets.getDebugLogger().debug(DebugLevel.HIDDEN, "Registered permission.");
    }
}
