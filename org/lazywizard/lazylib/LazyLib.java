package org.lazywizard.lazylib;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import org.apache.log4j.Level;
import org.json.JSONObject;
import org.lazywizard.lazylib.campaign.CargoUtils;
import org.lazywizard.lazylib.campaign.FleetUtils;
import org.lazywizard.lazylib.campaign.MessageUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.DefenseUtils;
import org.lazywizard.lazylib.combat.WeaponUtils;
import org.lazywizard.lazylib.combat.entities.AnchoredEntity;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lazywizard.lazylib.opengl.DrawUtils;

/**
 * Contains information on the current version of LazyLib.
 *
 * @author LazyWizard
 * @since 1.1
 */
public class LazyLib extends BaseModPlugin
{
    private static final String SETTINGS_FILE = "lazylib_settings.json";
    private static final boolean IS_DEV_BUILD = false;
    private static final float LIBRARY_VERSION = 1.8f;
    private static final String GAME_VERSION = "0.6.2a";
    private static boolean LOG_DEPRECATED = false, CRASH_DEPRECATED = false;
    private static Level LOG_LEVEL;

    /**
     * Returns the running version of LazyLib.
     *
     * @return The current version of LazyLib, as a {@link Float}.
     * <p>
     * @since 1.1
     */
    public static float getVersion()
    {
        return LIBRARY_VERSION;
    }

    /**
     * Returns the Starsector release this version was coded for.
     *
     * @return The version of Starsector this library supports, as a
     *         {@link String}.
     * <p>
     * @since 1.2
     */
    public static String getSupportedGameVersion()
    {
        return GAME_VERSION;
    }

    /**
     * Checks if this is a development (potentially unstable) build of LazyLib.
     *
     * @return {@code true} if this is a beta build of LazyLib, {@code false}
     *         otherwise.
     * <p>
     * @since 1.4
     */
    public static boolean isDevBuild()
    {
        return IS_DEV_BUILD;
    }

    /**
     * Gets the library information (for startup messages, etc).
     *
     * @return A {@link String} containing information on the library.
     * <p>
     * @since 1.2
     */
    public static String getInfo()
    {
        return "LazyLib v" + LIBRARY_VERSION + (IS_DEV_BUILD ? "_dev" : "")
                + " for Starsector " + GAME_VERSION;
    }

    /**
     * Returns the log level used for all other LazyLib classes. {@link LazyLib}
     * itself will always use log level {@link Level#ALL}.
     *
     * @return The current log level for all LazyLib classes.
     * <p>
     * @since 1.6b
     */
    public static Level getLogLevel()
    {
        return LOG_LEVEL;
    }

    /**
     * Sets the log level used for all other LazyLib classes.
     *
     * @param level The minimum level of entries that will be logged.
     * <p>
     * @since 1.6
     */
    public static void setLogLevel(Level level)
    {
        Global.getLogger(LazyLib.class).setLevel(Level.ALL);
        Global.getLogger(LazyLib.class).log(Level.INFO,
                "Setting log level to " + level);

        // org.lazywizard.lazylib
        Global.getLogger(CollectionUtils.class).setLevel(level);
        Global.getLogger(CollisionUtils.class).setLevel(level);
        Global.getLogger(MathUtils.class).setLevel(level);
        Global.getLogger(VectorUtils.class).setLevel(level);
        // org.lazywizard.lazylib.campaign
        Global.getLogger(CargoUtils.class).setLevel(level);
        Global.getLogger(FleetUtils.class).setLevel(level);
        Global.getLogger(MessageUtils.class).setLevel(level);
        // org.lazywizard.lazylib.combat
        Global.getLogger(AIUtils.class).setLevel(level);
        Global.getLogger(CombatUtils.class).setLevel(level);
        Global.getLogger(DefenseUtils.class).setLevel(level);
        Global.getLogger(WeaponUtils.class).setLevel(level);
        // org.lazywizard.lazylib.combat.entities
        Global.getLogger(AnchoredEntity.class).setLevel(level);
        Global.getLogger(SimpleEntity.class).setLevel(level);
        // org.lazywizard.lazylib.opengl
        Global.getLogger(DrawUtils.class).setLevel(level);

        LOG_LEVEL = level;
    }

    /**
     * Called internally by LazyLib when a deprecated method is used. If
     * "logDeprecated" is set in lazylib_settings.json it will log usage of
     * those methods. If "crashOnDeprecated" is true, this method will throw a
     * {@link RuntimeException} so modders can track down the problematic code
     * using the stacktrace. You can ignore this method; there's no reason to
     * ever call it manually.
     * <p>
     * @param source    The class that contains the deprecated method.
     * @param methodSig The signature of the method that is deprecated.
     * <p>
     * @since 1.7
     */
    public static void onDeprecatedMethodUsage(Class source, String methodSig)
    {
        if (LOG_DEPRECATED)
        {
            Global.getLogger(LazyLib.class).log(Level.WARN,
                    "Using deprecated method " + source.getSimpleName()
                    + "." + methodSig);
        }

        if (CRASH_DEPRECATED)
        {
            throw new RuntimeException("Deprecated method "
                    + source.getSimpleName() + "." + methodSig
                    + " used while \"crashOnDeprecated\" = true");
        }
    }

    @Override
    public void onApplicationLoad() throws Exception
    {
        Global.getLogger(LazyLib.class).log(Level.INFO, "Running " + getInfo());

        // Load LazyLib settings from JSON file
        JSONObject settings = Global.getSettings().loadJSON(SETTINGS_FILE);
        setLogLevel(Level.toLevel(settings.getString("logLevel"), Level.ERROR));
        LOG_DEPRECATED = settings.getBoolean("logDeprecated");
        CRASH_DEPRECATED = settings.getBoolean("crashOnDeprecated");
    }
}
