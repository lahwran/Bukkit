
package org.bukkit.entity;

import java.net.InetSocketAddress;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;

/**
 * Represents a player, connected or not
 *
 */
public interface Player extends HumanEntity, CommandSender {
    /**
     * Checks if this player is currently online
     *
     * @return true if they are online
     */
    public boolean isOnline();

    /**
     * Gets the "friendly" name to display of this player. This may include color.
     *
     * Note that this name will not be displayed in game, only in chat and places
     * defined by plugins
     *
     * @return String containing a color formatted name to display for this player
     */
    public String getDisplayName();

    /**
     * Sets the "friendly" name to display of this player. This may include color.
     *
     * Note that this name will not be displayed in game, only in chat and places
     * defined by plugins
     *
     * @return String containing a color formatted name to display for this player
     */
    public void setDisplayName(String name);

    /**
     * Set the target of the player's compass.
     *
     * @param loc
     */
    public void setCompassTarget(Location loc);

    /**
     * Get the previously set compass target.
     *
     * @return location of the target
     */
    public Location getCompassTarget();

    /**
     * Gets the socket address of this player
     * @return the player's address
     */
    public InetSocketAddress getAddress();
    
    /**
     * Sends this sender a message raw
     *
     * @param message Message to be displayed
     */
    public void sendRawMessage(String message);

    /**
     * Kicks player with custom kick message.
     *
     * @return
     */
    public void kickPlayer(String message);

    /**
     * Says a message (or runs a command).
     *
     * @param msg message to print
     */
    public void chat(String msg);

    /**
     * Makes the player perform the given command
     *
     * @param command Command to perform
     * @return true if the command was successful, otherwise false
     */
    public boolean performCommand(String command);

    /**
     * Returns if the player is in sneak mode
     * @return true if player is in sneak mode
     */
    public boolean isSneaking();

    /**
     * Sets the sneak mode the player
     * @param sneak true if player should appear sneaking
     */
    public void setSneaking(boolean sneak);

    /**
     * Saves the players current location, health, inventory, motion, and other information into the username.dat file, in the world/player folder
     */
    public void saveData();

    /**
     * Loads the players current location, health, inventory, motion, and other information from the username.dat file, in the world/player folder
     *
     * Note: This will overwrite the players current inventory, health, motion, etc, with the state from the saved dat file.
     */
    public void loadData();
    
    /**
     * Sets whether the player is ignored as not sleeping. If everyone is
     * either sleeping or has this flag set, then time will advance to the
     * next day. If everyone has this flag set but no one is actually in bed,
     * then nothing will happen.
     * 
     * @param isSleeping
     */
    public void setSleepingIgnored(boolean isSleeping);
    
    /**
     * Returns whether the player is sleeping ignored.
     * 
     * @return
     */
    public boolean isSleepingIgnored();

    /**
     * Resends all chunks this player has loaded. Use sparingly to conserve bandwidth.
     * Sends around 81 Chunk-sized Packet51MapChunks.
     * @see org.bukkit.event.player.BlockSendEvent
     */
    public void resendChunks();

    /**
     * Resends all blocks within specified area. The start parameters must be less
     * than the end parameters. Calling with (0, 0, 0, 1, 1, 1) will send one one block,
     * the block at coords 0,0,0. 
     * Sends one or more Packet51MapChunks.
     * @param startX
     * @param startY
     * @param startZ
     * @param endX
     * @param endY
     * @param endZ
     */
    public void resendArea(int startX, int startY, int startZ, int endX, int endY, int endZ);

    /**
     * Resends the blocks specified by coordArray. As it uses varargs it may be called:
     * <pre>
     * player.resendBlocks(new int[]{0,0,0}, new int[]{10,62,174});
     * player.resendBlocks(new int[][]{ new int[]{0,0,0}, new int[]{10,62,174} });
     * </pre>
     * @param coordArray
     */
    public void resendBlocks(int[]... coordArray);

    /**
     * Forces an update of the player's entire inventory.
     *
     * @return
     *
     * @deprecated This method should not be relied upon as it is a temporary work-around for a larger, more complicated issue.
     */
    public void updateInventory();
}
