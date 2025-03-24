package cc.unknown;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.lwjgl.opengl.Display;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.jna.Platform;

import cc.unknown.event.Event;
import cc.unknown.event.impl.buz.EventBus;
import cc.unknown.event.impl.forge.ForgeEventListener;
import cc.unknown.handlers.*;
import cc.unknown.managers.*;
import cc.unknown.ui.click.AstolfoGui;
import cc.unknown.util.client.CustomLogger;
import cc.unknown.util.client.ReflectUtil;
import cc.unknown.util.client.SystemUtil;
import cc.unknown.util.render.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public enum Sakura {
    instance;

    public static final String NAME = "Sakura";
    public static final String VERSION = "6.0";

    private ModuleManager moduleManager;
    private CommandManager cmdManager;
    private FileManager fileManager;
    private InjectManager injectManager;
    private ConfigManager cfgManager;
    private DragManager dragManager;
    private ScreenshotManager screenshotManager;
    
    private AstolfoGui astolfoGui;
    private final EventBus<Event> eventBus = new EventBus<>();
    private final CustomLogger logger = new CustomLogger();
    private final DiscordHandler discordHandler = new DiscordHandler();
    private final List<Object> registeredHandlers = Collections.synchronizedList(new ArrayList<>());
    private final ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Minecraft mc = Minecraft.getMinecraft();

    public boolean firstStart;

    public void init() {
        Display.setTitle(NAME + " " + VERSION);
        Runtime.getRuntime().addShutdownHook(new Thread(threadPool::shutdown));

        FontUtil.initializeFonts();
        optimizeMinecraft();
        initializeManagers();
        registerHandlers();
        checkFirstStart();
        startDiscordPresence();

        logger.info("Initialized successfully.");
    }

    public void stop() {
        cfgManager.saveConfigs();
        discordHandler.stop();
        logger.info("Rich Presence Terminated.");
        System.gc();
        logger.info("Client Terminated.");
    }

    private void register(Object... handlers) {
        for (Object handler : handlers) {
            try {
                registeredHandlers.add(handler);
                eventBus.register(handler);
                logger.info(handler.getClass().getSimpleName() + " registered.");
            } catch (Exception e) {
                logger.error("Failed to register handler: " + handler.getClass().getSimpleName(), e);
            }
        }
    }

    private void registerHandlers() {
        logger.info("Initializing handlers...");
        register(
            new SpoofHandler(),
            new AutoJoinHandler(),
            new TransactionHandler(),
            new FixHandler(),
            new SinceTickHandler(),
            new RotationHandler(),
            new BadPacketsHandler(),
            new DragHandler(),
            new CommandHandler(),
            new KeyHandler(),
            new GuiMoveHandler()
        );

        MinecraftForge.EVENT_BUS.register(new ForgeEventListener());
        logger.info("Handlers registered.");
    }

    private void initializeManagers() {
        logger.info("Initializing managers...");

        moduleManager = new ModuleManager();
        fileManager = new FileManager();
        injectManager = new InjectManager();
        screenshotManager = new ScreenshotManager();
        cmdManager = new CommandManager();
        cfgManager = new ConfigManager();
        dragManager = new DragManager();
        astolfoGui = new AstolfoGui();

        fileManager.init();
        screenshotManager.init();
        injectManager.init();
        cfgManager.init();
        cmdManager.init();

        logger.info("Managers registered.");
    }

    private void optimizeMinecraft() {
        if (ReflectUtil.isOptifineLoaded() && Platform.isWindows()) {
            try {
                Map<String, Boolean> settings = Stream.of(new Object[][]{
                    {"ofFastRender", !ReflectUtil.isShaders()},
                    {"ofChunkUpdatesDynamic", true},
                    {"ofSmartAnimations", true},
                    {"ofShowGlErrors", false},
                    {"ofRenderRegions", true},
                    {"ofSmoothFps", false},
                    {"ofFastMath", true}
                }).collect(Collectors.toMap(data -> (String) data[0], data -> (Boolean) data[1]));

                settings.forEach((key, value) -> ReflectUtil.setGameSetting(mc, key, value));
            } catch (Exception ignored) {}
        }
        mc.gameSettings.useVbo = true;
        logger.info("Minecraft optimization initialized.");
    }

    private void checkFirstStart() {
        if (Platform.isWindows()) {
            firstStart = SystemUtil.checkFirstStart();
        } else {
            firstStart = false;
        }
    }

    private void startDiscordPresence() {
        if (!Platform.isWindows()) return;

        String userProfile = System.getenv("USERPROFILE");
        if (userProfile != null && userProfile.contains("\\Pablo")) {
            logger.info("Rich Presence stopping.");
        } else {
            discordHandler.start();
            logger.info("Rich Presence initialized.");
        }
    }

    public ScreenshotManager getScreenshotManager() { return screenshotManager; }
	public ModuleManager getModuleManager() { return moduleManager; }
    public CommandManager getCmdManager() { return cmdManager; }
    public FileManager getFileManager() { return fileManager; }
    public InjectManager getInjectManager() { return injectManager; }
    public ConfigManager getCfgManager() { return cfgManager; }
    public EventBus<Event> getEventBus() { return eventBus; }
    public CustomLogger getLogger() { return logger; }
    public Gson getGSON() { return GSON; }
    public DragManager getDragManager() { return dragManager; }
    public AstolfoGui getAstolfoGui() { return astolfoGui; }
}
