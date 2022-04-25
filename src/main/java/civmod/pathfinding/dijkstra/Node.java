package civmod.pathfinding.dijkstra;

import java.util.LinkedList;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class Node
{
	public int x, y, z;
	private float cost;
	private boolean surfaceNode;
	private boolean surfaceEdgeNode;
	public Node[] neighbors;
	
	public Node(int x, int y, int z, float cost, boolean surfaceNode, boolean surfaceEdgeNode)
	{
		this.x = x;
		this.y = y;
		this.z = z;
		this.cost = cost;
		this.surfaceNode = surfaceNode;
		this.surfaceEdgeNode = surfaceEdgeNode;
		
		this.neighbors = new Node[0];
	}
	
	public boolean isSurfaceNode()
	{
		return surfaceNode;
	}
	public boolean isSurfaceEdgeNode()
	{
		return surfaceEdgeNode;
	}
	
	public int manhattanDistanceTo(Node other)
	{
		return Math.abs(other.x-x) + Math.abs(other.y-y) + Math.abs(other.z-z);
	}
	public BlockPos getPosition()
	{
		return new BlockPos(x, y, z);
	}
	public Vec3 getPositionVec3()
	{
		return new Vec3(x+0.5, y+0.5, z+0.5);
	}
	
	public void connect(Node other)
	{
		if(neighbors == null)
			neighbors = new Node[0];
		
		if(!isNeighbor(other))
		{
			Node[] newNeighbors = new Node[neighbors.length+1];
			
			int i = 0;
			for(; i < neighbors.length; i++)
				newNeighbors[i] = neighbors[i];
			
			newNeighbors[i] = other;
			
			neighbors = newNeighbors;
		}
	}
	public void clusterConnections(Node other)
	{
		LinkedList<Node> newNeighbors = new LinkedList<>();
		boolean cNodeAdded = false;
		
		for(Node neighbor : neighbors)
		{
			if((neighbor.y == other.y && Math.abs(neighbor.x-other.x) <= 1 && Math.abs(neighbor.z-other.z) <= 1
				|| neighbor.getPosition().equals(other.getPosition())))
			{
				if(!cNodeAdded)
				{
					cNodeAdded = true;
					newNeighbors.add(neighbor);
				}
			}
			else
			{
				newNeighbors.add(neighbor);
			}
		}
	}
	public boolean isNeighbor(Node node)
	{
		if(neighbors == null)
			return false;
		
		for(Node neighbor : neighbors)
			if(neighbor == node)
				return true;
		return false;
	}
	
	public float getCost()
	{
		return cost;
	}
}
