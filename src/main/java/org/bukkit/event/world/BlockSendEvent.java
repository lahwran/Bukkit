package org.bukkit.event.world;

import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * tells a plugin about blocks that are 
 * 
 * @author lahwran
 * @todo world seems like the wrong place for this?
 */

public class BlockSendEvent extends WorldEvent {
	
	public int x;
	public int y;
	public int z;
	public int sizex;
	public int sizey;
	public int sizez;
	public byte[] data;
	public Player player;
	
	public BlockSendEvent(int _x, int _y, int _z, int _sizex, int _sizey, int _sizez, 
			byte[] _data, Player _player, World _world) 
	{
		super(Type.BLOCK_SEND, _world);
		x=_x;
		y=_y;
		z=_z;
		sizex=_sizex;
		sizey=_sizey;
		sizez=_sizez;
		data = _data; //this is a pointer to the same data that will be sent out - is changed in place
		player = _player;
		
	}

	public BlockSendEvent(int _x, int _y, int _z, int _sizex, int _sizey, int _sizez, 
			byte[] _data, World _world) 
	{
		this(_x, _y, _z, _sizex, _sizey, _sizez, _data, null, _world);
	}
	
	//short ... just in case. all my code assumes this
	/**
	 * Gets a block at x,y,z, normalized so that if you call with 
	 * 0,0,0 it will get the 0,0,0 block in the data array. 
	 * 
	 * If you get a block outside (less than zero or above pos+size) the array 
	 * it will attempt to use the world block get method to resolve it. 
	 * 
	 * Y values greater than 127 will always return block id 0 (air), 
	 * Y values less than 0 will always return block id 7 (bedrock).
	 * 
	 * @return blockid at coordinates
	 */
	public short getBlockID(int getX, int getY, int getZ)
	{
		if( getY+y < 0 )
			return 7; //bedrock
		else if (getY+y > 127)
			return 0; //air
		else if (getX >= 0 && getX<=sizex && getY >= 0 && getY <= sizey && getZ >= 0 && getZ <= sizez)
		{
			//see http://mc.kev009.com/Protocol#Map_Chunk_.280x33.29 for description of indexing in data
			return data[(getY                         ) + 
			            (getZ * (sizey+1)             ) +
			            (getX * (sizey+1) * (sizez+1) ) ];
		}
		else
		{
			return (short) getWorld().getBlockTypeIdAt(getX, getY, getZ);
		}
	}
	
	public void setBlockID(byte id, int setX, int setY, int setZ)
	{
		if (setX >= 0 && setX<=sizex && setY >= 0 && setY <= sizey && setZ >= 0 && setZ <= sizez)
		{
			//see http://mc.kev009.com/Protocol#Map_Chunk_.280x33.29 for description of indexing in data
			data[(setY                         ) + 
			     (setZ * (sizey+1)             ) +
			     (setX * (sizey+1) * (sizez+1) ) ] = id;
		}
		else
			throw new ArrayIndexOutOfBoundsException("setBlockID("+id+", "+setX+", "+setY+", "+setZ+") on a ("+sizex+", "+sizey+", "+sizez+") send array");
	}
	
	/*
	public byte getMeta(int getX, int getY, int getZ)
	{
		
	}*/

	/**
	 * uh ... what's this for?
	 * 
	 * @todo figure out wtf this is for
	 */
	private static final long serialVersionUID = 2009534114381893685L;

}
