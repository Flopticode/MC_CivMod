package civmod.pathfinding;

import civmod.pathfinding.astar.AStar;
import civmod.pathfinding.astar.AStarWorldCartographer;
import civmod.pathfinding.baritone.BaritoneConnector;
import civmod.pathfinding.dijkstra.Dijkstra;
import civmod.pathfinding.dijkstra.DijkstraWorldCartographer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public abstract class PathFinding
{
	public static enum PathFindingAlgorithm
	{
		AStar("A*"),
		Dijkstra("Dijkstra"),
		Baritone("Baritone");
		
		public String name;
		
		private PathFindingAlgorithm(String name)
		{
			this.name = name;
		}
		
		@Override
		public String toString()
		{
			return name;
		}
	}
	
	public static Path findPath(WorldCartographer wc, BlockPos startPos, BlockPos endPos, PathFindingAlgorithm alg, float maxCostPerNode)
	{
		wc.evaluateArea(new ChunkPos(startPos.getX()/16-1, startPos.getZ()/16-1), new ChunkPos(endPos.getX()/16+1, endPos.getZ()/16+1));
		
		switch(alg)
		{
			case Dijkstra:
				return Dijkstra.findPath((DijkstraWorldCartographer)wc, startPos, endPos, maxCostPerNode);
			case AStar:
				return AStar.findPath((AStarWorldCartographer)wc, startPos, endPos, maxCostPerNode);
			case Baritone:
				return BaritoneConnector.findPath(wc, startPos, endPos, maxCostPerNode);
			default:
				throw new IllegalArgumentException("Invalid path finding algorithm \"" + alg + "\"");
		}
	}
}
