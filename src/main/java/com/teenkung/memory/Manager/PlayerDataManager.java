package com.teenkung.memory.Manager;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.EventRegister.MemoryChangeEvent;
import com.teenkung.memory.EventRegister.PlayerBoostEndEvent;
import com.teenkung.memory.EventRegister.PlayerBoostEvent;
import com.teenkung.memory.Memory;
import com.teenkung.memory.Regeneration.Regeneration;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PlayerDataManager {

    private final Player player;
    private long logoutTime;
    private final long loginTime;
    private long bypassEndTime;
    private int currentMemory;
    private int RLevel;
    private int MLevel;
    private long lastRegenerationTime;
    private long leftOverTime;
    private PersistentDataContainer container;
    private BukkitTask countdownBooster;
    private double last_task_delay;
    private double last_task_period;


    /**
     * Constructor of this class
     * ONLY USE WITH JOIN EVENT CLASS IF YOU WANT TO GET PLAYER DATA OF THIS CLASS PLEASE USE PlayerManager Class Instead!
     * @param player the player you want to load data
     */
    public PlayerDataManager(Player player) {

        this.player = player;
        this.loginTime = Memory.getCurrentUnixSeconds();
        this.leftOverTime = 0;
        this.container = player.getPersistentDataContainer();
        this.countdownBooster = null;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("INSERT INTO Memory (UUID, LeaveUnix, RLevel, MLevel, CurrentAmount, BypassUntil, LastRegen)" +
                        "    SELECT ?, ?, 1, 1, ?, ?, ?" +
                        "    WHERE NOT EXISTS (SELECT * FROM Memory WHERE UUID = ?);");

                statement.setString(1, player.getUniqueId().toString());
                statement.setLong(2, Memory.getCurrentUnixSeconds());
                statement.setInt(3, 0);
                statement.setLong(4, Memory.getCurrentUnixSeconds());
                statement.setLong(5, Memory.getCurrentUnixSeconds());
                statement.setString(6, player.getUniqueId().toString());
                System.out.println(statement);
                statement.executeUpdate();
                statement.close();

                PreparedStatement statement2 = Memory.getConnection().prepareStatement("SELECT * FROM Memory WHERE UUID = ?");
                statement2.setString(1, player.getUniqueId().toString());
                ResultSet rs = statement2.executeQuery();
                while (rs.next()) {
                    logoutTime = rs.getLong("LeaveUnix");
                    RLevel = rs.getInt("RLevel");
                    MLevel = rs.getInt("MLevel");
                    currentMemory = rs.getInt("CurrentAmount");
                    bypassEndTime = rs.getLong("BypassUntil");
                    lastRegenerationTime = rs.getLong("LastRegen");
                }
                statement2.close();

                last_task_period = ConfigLoader.getRegenTime(RLevel);
                last_task_delay = ConfigLoader.getRegenTime(RLevel);

            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }



    /**
     * Request a player class object from this class
     * @return a Player class object
     */
    public Player getPlayer() { return player; }

    /**
     * Request the Logout Time of the player
     * @return the logout time of the player in Unix time
     */
    public Long getLogoutTime() { return logoutTime; }
    /**
     * Request the current Memory of the player
     * @return the current Memory of the player
     */
    public int getCurrentMemory() { return currentMemory; }
    /**
     * Request the Last Regeneration Time of the player
     * @return the Last Regeneration Time of the player
     */
    public Long getLastRegenerationTime() { return lastRegenerationTime; }

    /**
     * Request the login Time of the player
     * NOTE: The login time of the player, only save when player join the game or plugin LOAD,
     * so if plugin gets reload the player login time will be the time that plugin got loaded
     * @return the Login time of the player in Unix time
     */
    public long getLoginTime() { return loginTime; }

    /**
     * Request the Bypass Time of the player
     * @return the Bypass time in Unix Time
     */
    public Long getBypassEndTime() { return bypassEndTime; }

    /**
     * Request the Regen Time Level of the player
     * @return the Regen time level of the player
     */
    public int getRegenLevel() { return RLevel; }

    /**
     * Request the Max Capacity Level of the player
     * @return the Max Capacity Level of the player
     */
    public int getMaxCapacityLevel() { return MLevel; }

    /**
     * Request the Leftover time after the Offline Calculation is completed
     * @return the Leftover time of the player
     */
    public long getLeftOverTime() { return leftOverTime; }

    /**
     * Request the duration of delay time of the last task
     * @return the duration of delay time
     */
    public double getLastTaskDelay() { return last_task_delay; }

    /**
     * Request the duration each period of the last task
     * @return the duration each period
     */
    public double getLastTaskPeriod() { return last_task_period; }

    /**
     * Request Countdown task of player booster
     * @return Countdown boost Task
     */
    public BukkitTask getCountdownBooster() { return countdownBooster; }

    /**
     * Request player's boost multiplier
     * @return Boost multiplier
     */
    public Double getBoosterMultiplier() {
        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Multiplier");
        return container.getOrDefault(multi, PersistentDataType.DOUBLE, 1D);
    }

    /**
     * Request player's boost duration
     * @return Duration of boost time
     */
    public Long getBoosterDuration() {
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Duration");
        return container.getOrDefault(dura, PersistentDataType.LONG, 0L);
    }

    /**
     * Request the time that the player's boost will end
     * @return The time that the player's boost will end
     */
    public Long getBoosterTimeout() {
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Timeout");
        return container.getOrDefault(dura, PersistentDataType.LONG, 0L);
    }

    public void setLastTaskDelay(double delay) {
        last_task_delay = delay;
    }

    public void setLastTaskPeriod(double period) {
        last_task_period = period;
    }

    public void setBooster(Double multiplier, Long duration) {
        // period remain = full-period - (now - last-regen-time)
        double old_multiplier = getBoosterMultiplier();
        long old_duration = getBoosterDuration();
        long old_timeout = getBoosterTimeout();
        double old_period = getLastTaskPeriod();
        double old_delay = getLastTaskDelay();

        double old_multiplied_period = ConfigLoader.getRegenTime(getRegenLevel()) / getBoosterMultiplier();

        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Timeout");

        long timeout = Memory.getCurrentUnixSeconds() + duration;

        // Countdown Boost
        countdownBooster(timeout - Memory.getCurrentUnixSeconds());

        Regeneration.cancelTask(player);

        double period_remain = Math.max(old_multiplied_period - (Memory.getCurrentUnixSeconds() - getLastRegenerationTime()), 0);
        long full_period = ConfigLoader.getRegenTime(getRegenLevel());

        double boosted_period_remain;
        double boosted_full_period;

        if (ServerManager.areNowBoosting()) {
            double server_boost_multiplier = ServerManager.getServerBoosterMultiplier();

            // combine server multiplier and player multiplier
            double combined_multiplier = server_boost_multiplier + multiplier;

            // calculate remain period with multiplier
            boosted_period_remain =  (period_remain / combined_multiplier);
            boosted_full_period =  (full_period / combined_multiplier);


        } else {
            // calculate remain period with multiplier
            boosted_period_remain =  (period_remain / multiplier);
            boosted_full_period =  (full_period / multiplier);
        }

        last_task_period = boosted_full_period;
        last_task_delay = boosted_period_remain;

        if (getCurrentMemory() > 0) {
            Regeneration.addTask(player, boosted_period_remain, boosted_full_period);
        }

        Bukkit.getScheduler().runTask(Memory.getInstance(), () -> {
            Bukkit.getPluginManager().callEvent(new PlayerBoostEvent(this, player, multiplier, duration, timeout, boosted_period_remain, boosted_full_period, old_multiplier, old_duration, old_timeout, old_delay, old_period));
        });

        container.set(multi, PersistentDataType.DOUBLE, multiplier);
        container.set(dura, PersistentDataType.LONG, duration);
        container.set(out, PersistentDataType.LONG, timeout);
    }

    public boolean isNowBoosting() {

        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");

        return container.has(multi, PersistentDataType.DOUBLE) && container.has(dura, PersistentDataType.LONG) && container.has(out, PersistentDataType.LONG) && container.get(out, PersistentDataType.LONG) > Memory.getCurrentUnixSeconds();
    }

    public boolean hasBoostingData() {

        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");

        return container.has(multi, PersistentDataType.DOUBLE) && container.has(dura, PersistentDataType.LONG) && container.has(out, PersistentDataType.LONG);
    }

    public void countdownBooster(long duration) {
        if (countdownBooster != null) {
            countdownBooster.cancel();
        }
        countdownBooster = Bukkit.getScheduler().runTaskLaterAsynchronously(Memory.getInstance(), () -> {

            Bukkit.getScheduler().runTask(Memory.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new PlayerBoostEndEvent(this, player, getBoosterMultiplier(), getBoosterDuration(), getBoosterTimeout()));
            });

            Regeneration.cancelTask(player);
            countdownBooster = null;

            double full_period = ConfigLoader.getRegenTime(RLevel);
            double player_multiplier = getBoosterMultiplier();

            double delay;
            double period;

            if (ServerManager.areNowBoosting()) {

                double server_multiplier = ServerManager.getServerBoosterMultiplier();

                delay = Math.max((full_period/server_multiplier) - (getBoosterDuration()-(last_task_delay) % (full_period/player_multiplier)), 0);
                period = (full_period/server_multiplier);

            } else {

                delay = Math.max((full_period) - (getBoosterDuration() % (full_period/player_multiplier)), 0);
                period = full_period;

            }

            if (getCurrentMemory() > 0) {
                Regeneration.addTask(player, delay, period);
            }

            removeContainer(container);

            last_task_delay = (ConfigLoader.getRegenTime(RLevel));
            last_task_period = (ConfigLoader.getRegenTime(RLevel));

        }, 20L * duration);
    }

    static void removeContainer(PersistentDataContainer container) {
        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_Server_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_Server_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_Server_Timeout");

        container.remove(multi);
        container.remove(dura);
        container.remove(out);
    }

    /**
     * Set the Regeneration Level of the player
     * @param level the Level of new Regen Level you want the player to have
     */
    public void setRegenLevel(int level) {
        RLevel = level;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET RLevel = ? WHERE UUID = ?");
                statement.setInt(1, level);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Set the Max Capacity Level of the player
     * @param level the Level of new Max Capacity Level you want the player to have
     */
    public void setMaxCapacityLevel(int level) {
        MLevel = level;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET MLevel = ? WHERE UUID = ?");
                statement.setInt(1, level);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Set the current Memory the player currently has
     * @param value the value you want the player to have
     * @param setRegenTime should the leastRegenerationTime value gets update too?
     */
    public void setCurrentMemory(int value, boolean setRegenTime) {
        int oldValue = currentMemory;
        currentMemory = value;
        if (setRegenTime) {
            lastRegenerationTime = Memory.getCurrentUnixSeconds();
        }
        player.sendMessage("PLAYER DATA MANAGER | OLD VALUE = " + oldValue);
        player.sendMessage("PLAYER DATA MANAGER | NEW VALUE = " + value);
        if (value == 0) {
            Regeneration.cancelTask(player);
        } else {
            if (oldValue == 0) {


                double period;

                // player's default regen rate
                double regen_rate = ConfigLoader.getRegenTime(getRegenLevel());

                // if both are boosting
                if (ServerManager.areNowBoosting() && isNowBoosting()) {

                    period = (regen_rate/(ServerManager.getServerBoosterMultiplier()+ getBoosterMultiplier()));

                    //if player is boosting but server not
                } else if (isNowBoosting()) {

                    period = (regen_rate/(getBoosterMultiplier()));

                    // if server is boosting but player not
                } else if (ServerManager.areNowBoosting()) {

                    period = (regen_rate/(ServerManager.getServerBoosterMultiplier()));

                    // if neither are boosting
                } else {

                    period = regen_rate;

                }
                Regeneration.addTask(player, period, period);
            }
        }

        // Call event 'MemoryChangeEvent'
        if (!(oldValue == value)) {
            Bukkit.getScheduler().runTask(Memory.getInstance(), () -> {
                Bukkit.getPluginManager().callEvent(new MemoryChangeEvent(this, player, oldValue, value));
            });
        }
    }

    /**
     * Set the Duration of Bypass Time in seconds (For example 1 means bypass for 1 second)
     * @param duration duration of the Bypass you want the player to have in seconds
     */
    public void setBypassTime(Long duration) {
        bypassEndTime = Memory.getCurrentUnixSeconds() + duration;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET BypassUntil = ? WHERE UUID = ?");
                statement.setLong(1, bypassEndTime);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Set the leave time unix of player (ONLY USE WITH PLAYER QUIT EVENT)
     * @param time The Unix time you want to set
     */
    public void setLeaveTime(Long time) {
        logoutTime = Memory.getCurrentUnixSeconds() + time;
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET LeaveUnix = ? WHERE UUID = ?");
                statement.setLong(1, logoutTime);
                statement.setString(2, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Save all the Data except logout Time to the MySQL database
     */
    public void saveDataToMySQL() {
        Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), () -> {
            try {
                PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET RLevel = ?, MLevel = ?, CurrentAmount = ?, BypassUntil = ?, LastRegen = ? WHERE UUID = ?");
                statement.setInt(1, RLevel);
                statement.setInt(2, MLevel);
                statement.setInt(3, currentMemory);
                statement.setLong(4, bypassEndTime);
                statement.setLong(5, lastRegenerationTime);
                statement.setString(6, player.getUniqueId().toString());
                statement.executeUpdate();
                statement.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * Perform the Offline Calculation of the player and give player
     * the Memory will get modified based on the Offline Calculation after thus task performed
     */
    public void performOfflineCalculation() {

        double player_boost_timeout_time = getBoosterTimeout();
        double last_regen_time = getLastRegenerationTime();
        double regen_rate = ConfigLoader.getRegenTime(RLevel);
        double player_boost_multiplier = getBoosterMultiplier();
        double player_login_time = Memory.getCurrentUnixSeconds();
        int decreaseAmount;

        player.sendMessage("PLAYER DATA MANAGER | PLAYER BOOST TIMEOUT TIME: "+(player_boost_timeout_time));
        player.sendMessage("PLAYER DATA MANAGER | LAST REGEN TIME: "+(long)last_regen_time);
        player.sendMessage("PLAYER DATA MANAGER | REGEN RATE: "+(regen_rate));
        player.sendMessage("PLAYER DATA MANAGER | PLAYER BOOST MULTIPLIER: "+(player_boost_multiplier));
        player.sendMessage("PLAYER DATA MANAGER | PLAYER LOGIN TIME: "+(long)player_login_time);

        if (isNowBoosting()) {

            decreaseAmount =  (int) ((player_login_time - last_regen_time) / (regen_rate / player_boost_multiplier));
            leftOverTime = (long) ((player_login_time - last_regen_time) % (regen_rate / player_boost_multiplier));

        } else {
            if (hasBoostingData()) {

                double no_boost_time_duration = (player_login_time - player_boost_timeout_time) + ((player_boost_timeout_time - last_regen_time) % (regen_rate / player_boost_multiplier));

                decreaseAmount = (int) (((player_boost_timeout_time - last_regen_time) / (regen_rate / player_boost_multiplier)) + (no_boost_time_duration/regen_rate));
                leftOverTime = (long) (no_boost_time_duration % regen_rate);

            } else {
                decreaseAmount = (int) ((player_login_time - last_regen_time) / regen_rate);
                leftOverTime = (long) ((player_login_time - last_regen_time) % regen_rate);
            }
        }

        player.sendMessage("PLAYER DATA MANAGER | OFFLINE CALCULATION: "+(decreaseAmount));
        setCurrentMemory(Math.max(currentMemory - decreaseAmount, 0), true);
    }

}
