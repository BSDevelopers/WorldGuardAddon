package addons.brainsynder.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

public class FlagHandler {

    public StateFlag ALLOW_PET_SPAWN = new StateFlag("allow-pet-spawn", true);
    public StateFlag ALLOW_PET_ENTER = new StateFlag("allow-pet-enter", true);
    public StateFlag ALLOW_PET_RIDING = new StateFlag("allow-pet-riding", true);
    private final FlagRegistry registry;

    public FlagHandler() {
        registry = WorldGuard.getInstance().getFlagRegistry();
        // oh lawd forgive me for what i'm boutta do
        setInitialised(false);
        forceRegister(ALLOW_PET_ENTER);
        forceRegister(ALLOW_PET_SPAWN);
        forceRegister(ALLOW_PET_RIDING);
        setInitialised(true);
        reloadWG();
    }

    public boolean canPetEnter(Player player, Location loc) {
        return query(player, loc, ALLOW_PET_ENTER);
    }

    public boolean canPetSpawn(Player player, Location loc) {
        return query(player, loc, ALLOW_PET_SPAWN);
    }

    public boolean canRidePet(Player player, Location loc) {
        return query(player, loc, ALLOW_PET_RIDING);
    }

    private boolean query(Player player, Location loc, StateFlag flag) {
        if (player != null) {
            // TODO bypass permission?
            if (player.hasPermission("pet.bypass.worldguard")) return true;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        // Get the region manager for the world we're inWorld
        RegionManager manager = container.get(BukkitAdapter.adapt(loc.getWorld()));
        // Uses the query cache
        RegionQuery query = container.createQuery();
        if (manager == null) return true;
        // Get all the applicable regions, as some will overlap
        ApplicableRegionSet regions = query.getApplicableRegions(BukkitAdapter.adapt(loc));
        return regions.testState(player != null ? WorldGuardPlugin.inst().wrapPlayer(player) : null, flag);
    }

    private void setInitialised(boolean b) {
        try {
            Method setInit = registry.getClass().getDeclaredMethod("setInitialized", boolean.class);
            setInit.invoke(registry, b);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            System.out.println("SPWorldGuardAddon > Some cool hacky stuff went wrong. Here's an error to slap in the devs' faces.");
            e.printStackTrace();
        }
    }

    private void forceRegister(StateFlag flag) {
        try {
            registry.register(flag);
        } catch (FlagConflictException ex) {
            // Force it in
            try {
                Field flagsField = registry.getClass().getDeclaredField("flags");
                flagsField.setAccessible(true);
                ConcurrentMap<String, Flag<?>> flags = (ConcurrentMap<String, Flag<?>>) flagsField.get(registry);
                flags.put(flag.getName().toLowerCase(), flag);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void reloadWG() {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        container.reload();
    }

}
