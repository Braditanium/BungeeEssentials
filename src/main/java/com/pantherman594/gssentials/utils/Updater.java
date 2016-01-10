/*
 * BungeeEssentials: Full customization of a few necessary features for your server!
 * Copyright (C) 2015  David Shen (PantherMan594)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.pantherman594.gssentials.utils;

import com.google.common.base.Preconditions;
import com.pantherman594.gssentials.BungeeEssentials;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Updater {
    private static Plugin plugin = BungeeEssentials.getInstance();

    public static boolean update(boolean beta) {
        final String oldVerDec = plugin.getDescription().getVersion();
        final int oldVersion = getVersionFromString(oldVerDec);
        File path = new File(ProxyServer.getInstance().getPluginsFolder(), "BungeeEssentials.jar");

        try {
            String versionLink = "https://raw.githubusercontent.com/PantherMan594/BungeeEssentials/master/version.txt";
            URL url = new URL(versionLink);
            URLConnection con = url.openConnection();
            InputStreamReader isr = new InputStreamReader(con.getInputStream());
            BufferedReader reader = new BufferedReader(isr);
            String newVer = reader.readLine();
            String newBVer = reader.readLine();
            int newVersion;
            String newVerDec;
            if (beta) {
                newVersion = getVersionFromString(newBVer);
                newVerDec = newBVer;
            } else {
                newVersion = getVersionFromString(newVer);
                newVerDec = newVer;
            }
            reader.close();

            if(newVersion > oldVersion) {
                plugin.getLogger().log(Level.INFO, "Update found, downloading...");
                String dlLink = "https://github.com/PantherMan594/BungeeEssentials/releases/download/" + newVerDec + "/BungeeEssentials.jar";
                url = new URL(dlLink);
                con = url.openConnection();
                InputStream in = con.getInputStream();
                FileOutputStream out = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size;
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }
                out.close();
                in.close();
                plugin.getLogger().log(Level.INFO, "Succesfully updated plugin to v" + newVerDec);
                plugin.getLogger().log(Level.INFO, "Plugin disabling, restart the server to enable changes!");
                return true;
            } else {
                plugin.getLogger().log(Level.INFO, "You are running the latest version of BungeeEssentials (v" + oldVerDec + ")!");
                updateConfig();
            }
        } catch(IOException e) {
            plugin.getLogger().log(Level.WARNING, "Failed to download update, please update manually from http://www.spigotmc.org/resources/bungeeessentials.1488/");
            plugin.getLogger().log(Level.WARNING, "Please report this error message: ");
            e.printStackTrace();
        }
        return false;
    }

    public static void updateConfig() {
        Configuration config = BungeeEssentials.getInstance().getConfig();
        Configuration messages = BungeeEssentials.getInstance().getMessages();
        int oldVersion = getVersionFromString(config.getString("configversion"));
        int newVersion = getVersionFromString(plugin.getDescription().getVersion());
        if (oldVersion == newVersion) {
            return;
        }
        if (oldVersion == 0) {
            if (!messages.getString("mute.muter.error").equals("")) {
                oldVersion = 244;
            } else {
                oldVersion = 243;
            }
        }
        try {
            File oldDir = new File(BungeeEssentials.getInstance().getDataFolder(), "old");
            if (!oldDir.exists()) {
                oldDir.mkdir();
            }
            File newConf = new File(BungeeEssentials.getInstance().getDataFolder(), "config.yml");
            File oldConf = new File(oldDir, "config_v" + oldVersion + ".yml");
            File newMess = new File(BungeeEssentials.getInstance().getDataFolder(), "messages.yml");
            File oldMess = new File(oldDir, "messages_v" + oldVersion + ".yml");
            if (!oldConf.exists()) {
                Files.copy(newConf.toPath(), oldConf.toPath());
            }
            if (!oldMess.exists()) {
                Files.copy(newMess.toPath(), oldMess.toPath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (oldVersion == 243) {
            String muteEnabled = messages.getString("mute.enabled");
            String muteDisabled = messages.getString("mute.disabled");
            String muteError = messages.getString("mute.error");
            messages.set("mute.enabled", null);
            messages.set("mute.disabled", null);
            messages.set("mute.error", null);
            messages.set("mute.muted.enabled", muteEnabled);
            messages.set("mute.muted.disabled", muteDisabled);
            messages.set("mute.muted.error", muteError);
            messages.set("mute.muter.enabled", "&c{{ PLAYER }} is now muted!");
            messages.set("mute.muter.disabled", "&a{{ PLAYER }} is no longer muted!");
            messages.set("mute.muter.error", "&cHey, you can't mute that player!");
            oldVersion = 244;
        }
        if (oldVersion == 244) {
            String muterExemptError = messages.getString("mute.muter.error");
            messages.set("mute.muter.exempt", muterExemptError);
            messages.set("mute.muter.error", "&7{{ PLAYER }} tried to chat while muted!");
            config.set("configversion", "2.4.5");
            oldVersion = 245;
        }
        if (oldVersion == 245) {
            messages.set("bannedwords.replace", "****");
            messages.set("bannedwords.list", Arrays.asList("anal", "anus", "aroused", "asshole", "bastard", "bitch", "boob", "bugger", "cock", "cum", "cunt", "dafuq", "dick", "ffs", "fuck", "gay", "hentai", "homo", "homosexual", "horny", "intercourse", "jerk", "lesbian", "milf", "nigga", "nigger", "pedo", "penis", "piss", "prostitute", "pussy", "rape", "rapist", "retard", "sex", "shit", "slag", "slut", "sperm", "spunk", "testicle", "titt", "tosser", "twat", "vagina", "wanker", "whore", "wtf"));
            config.set("configversion", "2.4.6");
            oldVersion = 246;
        }
        if (oldVersion == 246) {
            config.set("configversion", "2.4.7");
            oldVersion = 247;
        }
        if (oldVersion == 247) {
            List<String> enabledList = config.getStringList("enable");
            enabledList.remove("multirelog");
            enabledList.add("autoredirect");
            enabledList.add("fastrelog");
            enabledList.add("friend");
            enabledList.add("spam-command");
            config.set("enable", enabledList);
            config.set("friend", Arrays.asList("friend", "f"));
            List<Map<String, String>> section = (List<Map<String, String>>) config.getList("aliases");
            config.set("aliases", null);
            for (Map<String, String> map : section) {
                Preconditions.checkNotNull(map);
                Preconditions.checkArgument(!map.isEmpty());
                Preconditions.checkNotNull(map.get("alias"), "invalid alias");
                Preconditions.checkNotNull(map.get("commands"), "invalid commands");
                config.set("aliases." + map.get("alias"), map.get("commands"));
            }
            if (messages.get("bannedwords.replace").equals("****")) {
                messages.set("bannedwords.replace", "*");
            }
            messages.set("multilog", null);
            messages.set("friends.header", "&2Current Friends:");
            messages.set("friends.body", "- {{ NAME }} ({{ SERVER }})");
            messages.set("friends.new", "&aYou are now friends with {{ NAME }}!");
            messages.set("friends.old", "&aYou are already friends with {{ NAME }}!");
            messages.set("friends.remove", "&cYou are no longer friends with {{ NAME }}.");
            messages.set("friends.outrequests.header", "&2Outgoing Friend Requests:");
            messages.set("friends.outrequests.body", "- {{ NAME }}");
            messages.set("friends.outrequests.new", "&a{{ NAME }} has received your friend request.");
            messages.set("friends.outrequests.old", "&cYou already requested to be friends with {{ NAME }}. Please wait for a response!");
            messages.set("friends.outrequests.remove", "&cThe friend request to {{ NAME }} was removed.");
            messages.set("friends.inrequests.header", "&2Incoming Friend Requests:");
            messages.set("friends.inrequests.body", "- {{ NAME }}");
            messages.set("friends.inrequests.new", "&a{{ NAME }} would like to be your friend. /friend <add|remove> {{ NAME }} to accept or decline the request.");
            messages.set("friends.inrequests.remove", "&cThe friend request from {{ NAME }} was removed.");
            messages.set("errors.fastrelog", "&cPlease wait before reconnecting!");
            List<Map<String, String>> section2 = (List<Map<String, String>>) messages.getList("announcements");
            config.set("aliases", null);
            for (Map<String, String> map : section2) {
                Preconditions.checkNotNull(map);
                Preconditions.checkArgument(!map.isEmpty());
                Preconditions.checkNotNull(map.get("delay"), "invalid delay");
                Preconditions.checkNotNull(map.get("interval"), "invalid interval");
                Preconditions.checkNotNull(map.get("message"), "invalid message");
                String name;
                if (config.getString("announcements." + map.get("message").split(" ")[0] + ".message") != null) {
                    name = map.get("message").split(" ")[0];
                } else {
                    name = map.get("message").split(" ")[0] + Math.floor(Math.random() * 10);
                }
                config.set("aliases." + name + ".delay", map.get("delay"));
                config.set("aliases." + name + ".interval", map.get("interval"));
                config.set("aliases." + name + ".message", map.get("message"));
            }
            config.set("configversion", "2.5.0");
            //oldVersion = 250;
        }
        BungeeEssentials.getInstance().saveMainConfig();
        BungeeEssentials.getInstance().saveMessagesConfig();
        plugin.getLogger().log(Level.INFO, "Config updated. You may edit new values to your liking.");
    }

    private static int getVersionFromString(String from) {
        String result = from.replace(".", "");

        return result.isEmpty() ? 0 : Integer.parseInt(result);
    }
}