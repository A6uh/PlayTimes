package me.codedred.playtimes;

import java.io.IOException;
import java.util.Objects;
import me.codedred.playtimes.commands.Time;
import me.codedred.playtimes.commands.TopTime;
import me.codedred.playtimes.commands.Uptime;
import me.codedred.playtimes.commands.completer.TimeTabCompleter;
import me.codedred.playtimes.listeners.Join;
import me.codedred.playtimes.listeners.Quit;
import me.codedred.playtimes.server.ServerManager;
import me.codedred.playtimes.statistics.StatManager;
import me.codedred.playtimes.time.TimeManager;
import me.codedred.playtimes.utils.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayTimes extends JavaPlugin {

  @Override
  public void onEnable() {
    checkForUpdate();

    ServerManager.getInstance().register();
    StatManager.getInstance().registerStatistics();
    TimeManager.getInstance().registerTimings();

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      Expansions exp = new Expansions();
      exp.register();
      getLogger().info("[PlayTimes] PlaceholdersAPI Hooked!");
    }

    registerEvents();
    registerCommands();

    @SuppressWarnings("unused")
    Metrics metrics = new Metrics(this, 5289);
    getLogger().info("[PlayTimes] Successfully loaded.");
  }

  @Override
  public void onDisable() {
    getLogger().info("PlayTimes shutting down");
  }

  private void registerEvents() {
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvents(new Join(), this);
    pm.registerEvents(new Quit(), this);
  }

  private void registerCommands() {
    Objects.requireNonNull(getCommand("playtime")).setExecutor(new Time());
    Objects.requireNonNull(getCommand("uptime")).setExecutor(new Uptime());
    Objects
      .requireNonNull(getCommand("topplaytime"))
      .setExecutor(new TopTime());
    Objects
      .requireNonNull(this.getCommand("pt"))
      .setTabCompleter(new TimeTabCompleter());
  }

  private void checkForUpdate() {
    Bukkit
      .getScheduler()
      .runTaskAsynchronously(
        this,
        () -> {
          UpdateChecker updater = new UpdateChecker(this, 58858);
          try {
            if (updater.hasUpdatesAvailable()) {
              getLogger()
                .warning(
                  ChatUtil.format(
                    "You are using an older version of PlayTimes!"
                  )
                );
              getLogger()
                .info(ChatUtil.format("Download the newest version here:"));
              getLogger()
                .info(
                  ChatUtil.format("https://www.spigotmc.org/resources/58858/")
                );
            } else {
              getLogger()
                .info(
                  "[PlayTimes] Plugin is up to date! - " +
                  getDescription().getVersion()
                );
            }
          } catch (IOException e) {
            getLogger().warning("[PlayTimes] Could not check for updates!");
          }
        }
      );
  }
}
