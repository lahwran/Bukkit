package org.bukkit.event.player;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * Is fired before blocks are queued to be sent to a player. 
 * 
 * @author lahwran
 */

public class BlockSendEvent extends PlayerEvent {
    /** World X location at which the send event starts */
    public final int x;
    /** World Y location at which the send event starts */
    public final int y;
    /** World Z location at which the send event starts */
    public final int z;
    
    /**
     * Respective x/y/z size. the maximum index of each is the respective size minus one, same way as an array length. 
     * This means that for a MapChunk, it is one greater than the size that will be sent in the packet.
     * only used if mode==eventMode.BYTEARRAY.
     */
    public final int sizex, sizey, sizez;
    /**
     * Byte array containing Packet51MapChunk data, 
     * you probably don't want to use this directly as it's indexed in a rather confusing manner.
     * only used if mode==eventMode.BYTEARRAY
     */
    protected byte[] data_array;
    
    /**
     * Values of single block when in single block mode,  
     * only used if mode==eventMode.SINGLE
     */
    public int blockid, blockmeta;
    
    public enum eventMode { //is an enum overkill?
        BYTEARRAY,
        SINGLE
    }
    /**
     * whether to use the array or the single values
     */
    public final eventMode mode;
    /**
     * the player that will receive these blocks
     * @todo now unneeded since we subclass PlayerEvent? maybe? could be a bit faster to not need a getter
     */
    public final Player player;

    /** World in which the player resides, cached from player.getWorld() for the slight speed gain it provides. */
    public final World world;

    /**
     * Block array constructor, to be used from Packet51MapChunk
     * @param _x 
     * @param _y
     * @param _z
     * @param _sizex
     * @param _sizey
     * @param _sizez
     * @param _data
     * @param _player
     * @param _world
     */
    public BlockSendEvent(int _x, int _y, int _z, int _sizex, int _sizey, int _sizez, 
            byte[] _data, Player _player) 
    {
        super(Type.BLOCK_SEND, _player);
        world=_player.getWorld();
        x=_x+1;
        y=_y+1;
        z=_z+1;
        sizex=_sizex;
        sizey=_sizey;
        sizez=_sizez;
        data_array = _data; //this is a pointer to the same data that will be sent out - is changed in place
        player = _player;
        mode = eventMode.BYTEARRAY;
    }
    
    /**
     * Single block constructor, to be used multiple times in Packet52 and once in Packet53
     * @param initx X position to initialize with
     * @param inity Y position to initialize with
     * @param initz Z position to initialize with
     * @param initblockid  Block ID to initialize with
     * @param initblockmeta Block metadata to initialize with
     * @param initplayer Player who will be receiving the blocks
     */
    public BlockSendEvent(int initx, int inity, int initz, int initblockid, int initblockmeta, Player initplayer)
    {
        super(Type.BLOCK_SEND, initplayer);
        world=initplayer.getWorld();
        x=initx;
        y=inity;
        z=initz;
        sizex=0;
        sizey=0;
        sizez=0;
        player = initplayer;
        blockid=initblockid;
        blockmeta=initblockmeta;
        mode = eventMode.SINGLE;
    }
    
    /**
     * Gets a block at x,y,z, normalized so that if you call with 
     * 0,0,0 it will get the 0,0,0 block in the data array. 
     * 
     * If you get a block outside (less than zero or above pos+size) the array 
     * it will attempt to use the world block get method to resolve it. 
     * 
     * Y values greater than world 127 will always return block id 0 (air), 
     * Y values less than world 0 will always return block id 7 (bedrock).
     * 
     * @return blockid at coordinates
     * @param getX X coord to retrieve
     * @param getY Y coord to retrieve
     * @param getZ Z coord to retrieve
     */
    public int getBlockID(int getX, int getY, int getZ)
    {
        if( getY+y < 0 )
            return 7; //bedrock
        else if (getY+y > 127)
            return 0; //air
        else if (getX >= 0 && getX < sizex && getY >= 0 && getY < sizey && getZ >= 0 && getZ < sizez)
        {
            if (mode == eventMode.BYTEARRAY)
            {
                //see http://mc.kev009.com/Protocol#Map_Chunk_.280x33.29 for description of indexing in data
                return data_array[(getY) + (getZ * (sizey)) + (getX * (sizey) * (sizez)) ];
            }
            else //if (mode == eventMode.SINGLE)
            {
                return blockid;
            }
        }
        else
        {
            return world.getBlockTypeIdAt(getX, getY, getZ);
        }
    }
    
    /**
     * Sets the block to the provided ID. Coords must be within bounds of sizex, sizey, and sizez.
     * @param id ID to set block at coordinates to
     * @param setX X coordinate to alter 
     * @param setY Y coordinate to alter
     * @param setZ Z coordinate to alter
     */
    public void setBlockID(byte id, int setX, int setY, int setZ)
    {
        if (setX >= 0 && setX < sizex && setY >= 0 && setY < sizey && setZ >= 0 && setZ < sizez)
        {
            if (mode == eventMode.BYTEARRAY)
            {
                //see http://mc.kev009.com/Protocol#Map_Chunk_.280x33.29 for description of indexing in data
                data_array[(setY) + (setZ * (sizey)) + (setX * (sizey) * (sizez)) ] = id;
            }
            else
            {
                blockid = id;
            }
        }
        else
            throw new ArrayIndexOutOfBoundsException("setBlockID("+id+", "+setX+", "+setY+", "+setZ+") on a ("+sizex+", "+sizey+", "+sizez+") send array for player "+player.getName());
    }
    
    /**
     * Gets metadata of a block.
     * Warning: does not yet work on byte array events
     * @param getX X coord at which to retrieve metadata
     * @param getY Y coord at which to retrieve metadata
     * @param getZ Z coord at which to retrieve metadata
     * @return Metadata of block at coords
     */
    public int getMeta(int getX, int getY, int getZ)
    {
        if( getY+y < 0 || getY+y > 127)
            return 0; //air or bedrock
        else if (getX >= 0 && getX < sizex && getY >= 0 && getY < sizey && getZ >= 0 && getZ < sizez)
        {
            if (mode == eventMode.SINGLE)
            {
                return blockmeta;
            }
            else
            {
                throw new UnsupportedOperationException("getting the metadata of a bytearray event is not implemented yet!");
            }
        }
        else
        {
            return world.getBlockAt(getX, getY, getZ).getData();
        }
    }

    /**
     * Sets metadata of a block.
     * Warning: does not yet work on byte array events
     * @param meta Metadata to set to
     * @param setX X coord at which to set metadata
     * @param setY Y coord at which to set metadata
     * @param setZ Z coord at which to set metadata
     */
    public void setMeta(int meta, int setX, int setY, int setZ)
    {
        if (setX >= 0 && setX < sizex && setY >= 0 && setY < sizey && setZ >= 0 && setZ < sizez)
        {
            if (mode == eventMode.BYTEARRAY)
            {
                throw new UnsupportedOperationException("setting the metadata of a bytearray event is not implemented yet!");
                //see http://mc.kev009.com/Protocol#Map_Chunk_.280x33.29 for description of indexing in data
                //data_array[(setY                         ) + 
                //     (setZ * (sizey+1)             ) +
                //     (setX * (sizey+1) * (sizez+1) ) ] = id;
            }
            else
            {
                blockmeta = meta;
            }
        }
        else
            throw new ArrayIndexOutOfBoundsException("setMeta("+meta+", "+setX+", "+setY+", "+setZ+") on a ("+sizex+", "+sizey+", "+sizez+") send array for player "+player.getName());
    }

    //TODO: set light values for bytearray

    /**
     * uh ... what's this for?
     * 
     * @todo figure out wtf this is for
     */
    private static final long serialVersionUID = 2009534114381893685L;

}
