package com.dumptruckman.chestrestock;

import com.dumptruckman.chestrestock.api.LootConfig;
import com.dumptruckman.chestrestock.api.LootTable;
import com.dumptruckman.minecraft.pluginbase.util.Logging;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.WeakHashMap;

class DefaultLootConfig implements LootConfig {

    private FileConfiguration config;

    private Map<String, LootTable> cachedTables = new WeakHashMap<String, LootTable>();

    DefaultLootConfig(ChestRestockPlugin plugin) {
        File configFile = new File(plugin.getDataFolder(), "loot_tables.yml");
        config = YamlConfiguration.loadConfiguration(configFile);
        String nl = System.getProperty("line.separator");
        config.options().header("This is where you define loot tables for your chests to have random loot."
                + nl + "Properties for each section of a table:"
                + nl + "chance - the chance at which the section will be picked (as a fraction: 0.25 == 25%).  default: 1"
                + nl + "rolls - the number of times the section will be considered.  default: 1"
                + nl + "split (true/false) - if true, chance will be used as section weight and only 1 section will be picked.  default: false"
                + nl + "id - the item id (number).  default: none"
                + nl + "data - the item data value (number).  default: none"
                + nl + "amount - the amount of the item.  default: 1");
        try {
            config.save(configFile);
            YamlConfiguration.loadConfiguration(plugin.getResource("loot_example.yml"))
                    .save(new File(plugin.getDataFolder(), "loot_example.yml"));
        } catch (IOException e) {
            Logging.severe("Could not save loot_tables.yml!");
            Logging.severe("Reason: " + e.getMessage());
        }
    }

    @Override
    public LootTable getLootTable(String name) {
        if (name.isEmpty()) {
            return null;
        }
        if (cachedTables.containsKey(name)) {
            Logging.fine("Got cached table!");
            return cachedTables.get(name);
        }
        ConfigurationSection section = config.getConfigurationSection(name);
        if (section == null) {
            Logging.warning("Could not locate loot table: " + name);
            return null;
        }
        LootTable newTable = new DefaultLootTable(section);
        cachedTables.put(name, newTable);
        Logging.fine("Loaded loot table from config.");
        return newTable;
    }
}
