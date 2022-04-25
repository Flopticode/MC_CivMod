package civmod.pathfinding;

import java.util.LinkedList;

import net.minecraft.core.BlockPos;

public class Path
{
	private BlockPos[] path;
	private int curNode = 0;
	
	public Path(BlockPos[] pPath)
	{
		this.path = pPath;
	}
	public Path(civmod.pathfinding.dijkstra.Node[] pPath)
	{
		BlockPos[] path = new BlockPos[pPath.length];
		
		for(int i = 0; i < path.length; i++)
			path[i] = pPath[i].getPosition();
		
		this.path = path;
	}
	public Path(civmod.pathfinding.astar.AStar.Node[] pPath)
	{
		BlockPos[] path = new BlockPos[pPath.length];
		
		for(int i = 0; i < path.length; i++)
			path[i] = pPath[i].position;
		
		this.path = path;
	}
	public Path(LinkedList<civmod.pathfinding.dijkstra.Node> pPath)
	{
		this(pPath.toArray(new civmod.pathfinding.dijkstra.Node[pPath.size()]));
	}
	
	public BlockPos getNextNode()
	{
		return isDone() ? null : path[curNode];
	}
	public boolean isDone()
	{
		return curNode >= path.length;
	}
	public void advance()
	{
		curNode++;
	}
	
	public String toString()
	{
		String str = "Path {";
		
		for(BlockPos node : path)
		{
			BlockPos pos = node;
			str += " => (" + pos.getX() + " | " + pos.getY() + " | " + pos.getZ() + ")";
		}
		str += "}";
		return str;
	}
}
