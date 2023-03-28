package com.teenkung.memory.Manager;

import com.teenkung.memory.ConfigLoader;
import com.teenkung.memory.Memory;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitTask;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.teenkung.memory.Memory.colorize;

@SuppressWarnings("unused")
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
    private final PersistentDataContainer container;
    private Double multiplier;
    private Long duration;
    private Long timeout;
    private Long p;
    private BukkitTask task;


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
        this.p = 0L;
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

                //Load the Data Container
                NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Multiplier");
                NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Duration");
                NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Timeout");

                this.multiplier = container.getOrDefault(multi, PersistentDataType.DOUBLE, 1D);
                this.duration = container.getOrDefault(dura, PersistentDataType.LONG, 0L);
                this.timeout = container.getOrDefault(out, PersistentDataType.LONG, Memory.getCurrentUnixSeconds()-1);

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

    public PersistentDataContainer getContainer() { return container; }

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
     * Request player's boost multiplier
     * @return Boost multiplier
     */
    public Double getBoosterMultiplier() {
        return multiplier;
    }

    /**
     * Request player's boost duration
     * @return Duration of boost time
     */
    public Long getBoosterDuration() {
        return this.duration;
    }

    /**
     * Request the time that the player's boost will end
     * @return The time that the player's boost will end
     */
    public Long getBoosterTimeout() {
        return this.timeout;
    }

    public void setBooster(Double multiplier, Long duration) {
        // period remain = full-period - (now - last-regen-time)

        //double old_multiplied_period = ConfigLoader.getRegenTime(getRegenLevel()) / getBoosterMultiplier();

        NamespacedKey multi = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Multiplier");
        NamespacedKey dura = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Duration");
        NamespacedKey out = new NamespacedKey(Memory.getInstance(), "Memory_PlayerBoost_Timeout");
        long timeout = Memory.getCurrentUnixSeconds() + duration;

        container.set(multi, PersistentDataType.DOUBLE, multiplier);
        container.set(dura, PersistentDataType.LONG, duration);
        container.set(out, PersistentDataType.LONG, timeout);
        this.multiplier = multiplier;
        this.duration = duration;
        this.timeout = timeout;
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
     */
    public void setCurrentMemory(int value) {
        currentMemory = value;
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
     */
    public void setLeaveTime(boolean... async) {
        logoutTime = Memory.getCurrentUnixSeconds();
        boolean isAsync = async.length == 0 || async[0];
        if (isAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), this::saveLeaveTime);
        } else {
            saveLeaveTime();
        }
    }

    /**
     * PRIVATE CLASS, ONLY USE WITH setLeaveTime()
     */
    private void saveLeaveTime() {
        try {
            PreparedStatement statement = Memory.getConnection().prepareStatement("UPDATE Memory SET LeaveUnix = ? WHERE UUID = ?");
            statement.setLong(1, logoutTime);
            statement.setString(2, player.getUniqueId().toString());
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save all the Data except logout Time to the MySQL database
     */
    public void saveDataToMySQL(boolean... async) {
        boolean isAsync = async.length == 0 || async[0];
        if (isAsync) {
            Bukkit.getScheduler().runTaskAsynchronously(Memory.getInstance(), this::saveAllData);
        } else {
            saveAllData();
        }
    }

    /**
     * PRIVATE CLASS, ONLY USE WITH saveDataToMySQL()
     */
    private void saveAllData() {
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
    }

    /**
     * Perform the Offline Calculation of the player and give player
     * the Memory will get modified based on the Offline Calculation after thus task performed
     */
    public void performOfflineCalculation() {
        long def = ConfigLoader.getRegenTime(RLevel);
        double playerMultiplier = getBoosterMultiplier();

        long logoutDuration = Memory.getCurrentUnixSeconds() - logoutTime;
        long boostDuration = Math.max(timeout - logoutTime, 0);
        long unBoostDuration = logoutDuration - boostDuration;

        long calcDuration = Math.round((boostDuration*playerMultiplier) + unBoostDuration);
        int amount = Long.valueOf(calcDuration/def).intValue();

        leftOverTime = calcDuration%def;

        if (currentMemory - amount < 0) {
            currentMemory = 0;
        } else {
            currentMemory -= amount;
        }

    }

    /**
     * Calculates the next time the player will regenerate Memory in Unix seconds
     * This method uses the Mathematical formula of:
     * <p>
     * T1+(T2/PlayerMultiplier)+(T3/ServerMultiplier)+(T4/(PlayerMultiplier+ServerMultiplier))
     * Where:
     * T1 is the time in seconds that player will not get affected by the boosters
     * T2 is the time in seconds that player will get affected by the player boosters
     * T3 is the time in seconds that player will get affected by the server boosters
     * T4 is the time in seconds that player will get affected by the player and server boosters
     * <p>
     * WHERE:
     * - player and server booster duration must not exceed player's default regeneration rate
     * <p>
     * If the rolldownBoost is true, the booster duration will get decreased by the regeneration rate that get from the Mathematical formula
     *
     * @param rolldownBoost should this calculate also reduce the Duration of the booster too or not
     * @param includeLeftover should this calculate also include the leftover time from the offline calculation
     */
    public void calculatePeriod(boolean rolldownBoost, boolean includeLeftover) {
        //Bukkit.broadcastMessage(colorize("Stated Calculation"));
        long def = ConfigLoader.getRegenTime(RLevel);

        double playerMultiplier = 1;
        long playerDuration = 0;
        double serverMultiplier = 1;
        long serverDuration = 0;

        if (timeout >= Memory.getCurrentUnixSeconds()) {
            playerMultiplier = getBoosterMultiplier();
            playerDuration = Math.min(getBoosterDuration(), def);
        }

        if (ServerManager.getTimeOut() >= Memory.getCurrentUnixSeconds()) {
            serverMultiplier = ServerManager.getMultiplier();
            serverDuration = Math.min(ServerManager.getDuration(), def);
        }

        double t1 = def - Math.max(serverDuration, playerDuration);
        double t2 = Math.max(playerDuration-serverDuration, 0);
        double t3 = Math.max(serverDuration-playerDuration, 0);
        double t4 = Math.min(serverDuration, playerDuration);

        if (includeLeftover) {
            t1 += leftOverTime;
        }

        long t = Math.round(t1 + (t2/playerMultiplier) + (t3/serverMultiplier) + (t4/(playerMultiplier+serverMultiplier)));

        p = Memory.getCurrentUnixSeconds() + t;
        if (rolldownBoost) {
            if (timeout >= Memory.getCurrentUnixSeconds()) {
                long a = (duration-def)+playerDuration - t;
                long d = Math.max(a, 0);
                if (d == 0) {
                    setBooster(1D, d);
                } else {
                    setBooster(multiplier, d);
                }
            }
        }

    }

    /**
     * Start the regeneration Task of the player
     * ONLY USE ONCE WHEN PLAYER DATA IS LOADED
     * <p>
     * This method will execute once when player join the server / plugin loaded
     * after this method is executed an async task that runs every 1 second will start
     * this method will check if player is online already or not if player is not online at that time this task will be automatically canceled
     * but if player online, this task will first check if p variable is null or not, if yes it will calculate the p variable first
     * but if no it will check if p value is less than or equal to the current unix seconds (check if it is time to regenerate or not)
     * if yes it will check if current mem is not 0 if yes, it will regenerate a memory
     * <p>
     * next is it will check if player's booster is just ended or not if yes it will send a message to the player
     * and remove the booster
     * <p>
     * next is it will check if player's bypass time is just ended or not if yes it will send a message to the player
     * and remove the bypass
     */
    public void startGenerationTask() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(Memory.getInstance(), () -> {
            if (player.isOnline()) {
                if (p == null) {
                    calculatePeriod(false, true);
                } else if (p <= Memory.getCurrentUnixSeconds()) {
                    if (currentMemory > 0) {
                        currentMemory--;
                        calculatePeriod(true, false);
                    }
                }
                if (timeout.equals(Memory.getCurrentUnixSeconds())) {
                    player.sendMessage(colorize(ConfigLoader.getMessage("Info.Player_Boost_End", true)));
                    setBooster(1D, 0L);
                }
                if (bypassEndTime < Memory.getCurrentUnixSeconds() && bypassEndTime != 0) {
                    player.sendMessage(colorize(ConfigLoader.getMessage("Info.Bypass_End", true)));
                    setBypassTime(-Memory.getCurrentUnixSeconds());
                }
            }
        }, 0, 20);
    }

    /**
     * Cancel the regeneration task of the player
     */
    public void cancelTask() { task.cancel(); }

    /**
     * Get how many seconds are left before player regenerate new memory
     * @return the time left in seconds
     */
    public Long getNextPerionIn() { return Math.max(p - Memory.getCurrentUnixSeconds(), 0); }

    /**
     * Get the time left for the player to fill the memory
     * @return the time left in seconds
     */
    public Long getFillTime() {

        if (currentMemory <= 0) {
            return 0L;
        }

        long def = (ConfigLoader.getRegenTime(RLevel).longValue()*currentMemory) - (ConfigLoader.getRegenTime(RLevel)-getNextPerionIn());
        long serverDuration = 0;
        long playerDuration = 0;
        double serverMultiplier = 1;
        double playerMultiplier = 1;

        if (timeout >= Memory.getCurrentUnixSeconds()) {
            playerMultiplier = getBoosterMultiplier();
            playerDuration = Math.min(getBoosterDuration(), def);
        }

        if (ServerManager.getTimeOut() >= Memory.getCurrentUnixSeconds()) {
            serverMultiplier = ServerManager.getMultiplier();
            serverDuration = Math.min(ServerManager.getDuration(), def);
        }

        long t1 = def - Math.max(serverDuration, playerDuration);
        long t2 = Math.max(playerDuration-serverDuration, 0);
        long t3 = Math.max(serverDuration-playerDuration, 0);
        long t4 = Math.min(serverDuration, playerDuration);

        return Math.round(t1 + (t2/playerMultiplier) + (t3/serverMultiplier) + (t4/(playerMultiplier+serverMultiplier)));
    }

}
