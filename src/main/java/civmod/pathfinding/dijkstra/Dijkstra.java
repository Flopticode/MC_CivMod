package civmod.pathfinding.dijkstra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import civmod.pathfinding.Path;
import civmod.pathfinding.PriorityQueue;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;

public abstract class Dijkstra
{
	private static class EdgeInfo
	{
		public Node start;
		public Node end;
		
		public EdgeInfo(Node s, Node e)
		{
			this.start = s;
			this.end = e;
		}
	}
	
	public static Path findPath(DijkstraWorldCartographer map, BlockPos startPos, BlockPos endPos, float maxCostPerNode)
	{
		map.evaluateArea(new ChunkPos(startPos.getX()/16-1, startPos.getZ()/16-1), new ChunkPos(endPos.getX()/16+1, endPos.getZ()/16+1));
		
		/* Using a fancy dijkstra variation to build the cost-node-array (here it's a map) */
		Map<Node, Float> pathCost = new HashMap<>();
		Map<Node, Node> pathNode = new HashMap<>();
		PriorityQueue<EdgeInfo> prioQueue = new PriorityQueue<>();
		
		Node startNode;
		Node endNode = map.getNode(endPos);
		
		/* If the end pos in in the air, the algorithm will not find a way there. Translating the position downwards
		 * until a node is found is therefore helpful. In general, a path to the floor under the target position will
		 * be found */
		while(endNode == null && map.level.getBlockState(endPos.below()).isCollisionShapeFullBlock(map.level, endPos.below()))
		{
			endPos = endPos.below();
			
			endNode = map.getNode(endPos);
		}
		
		pathCost.put(startNode = map.getNode(startPos), 0f);
		
		if(endNode == null || startNode == null)
			return null; /* Position not evaluated */
		
		for(Node neighbor : startNode.neighbors)
			prioQueue.addElement(new EdgeInfo(startNode, neighbor), neighbor.getCost());
		
		while(!prioQueue.isEmpty())
		{
			EdgeInfo edgeInfo = prioQueue.front();
			
			Float costToStart = pathCost.get(edgeInfo.start);
			if(costToStart == null)
				costToStart = 0f;
			
			float costToEnd = costToStart + edgeInfo.end.getCost();
			
			pathCost.put(edgeInfo.end, costToEnd);
			pathNode.put(edgeInfo.end, edgeInfo.start);
			prioQueue.dequeue();
			
			if(edgeInfo.end == endNode)
				break;
			
			
			for(Node neighbor : edgeInfo.end.neighbors)
			{
				if(neighbor.getCost() <= maxCostPerNode && pathCost.get(neighbor) == null && !prioQueue.contains(ei->(ei.key.start==edgeInfo.end && ei.key.end==neighbor)))
					prioQueue.addElement(new EdgeInfo(edgeInfo.end, neighbor), costToEnd + neighbor.getCost());
			}
		}
		
		/* Tracing through the map and constructing the path from the start node to the end */
		LinkedList<Node> pathNodes = new LinkedList<>();
		Node curNode = endNode;
		
		while(curNode != startNode)
		{
			Node prevNode = pathNode.get(curNode);
			pathNodes.add(0, curNode);			
			curNode = prevNode;
			
			if(curNode == null)
				return null;
		}
		
		/* If nodes were clustered, the exact target position is not reached by the algorithm.
		 * In that case the exact endPos has to be added to the path. */
		Node last = pathNodes.size() == 0 ? null : pathNodes.getLast();
		
		if(last == null || !last.getPosition().equals(endPos) && last.y == endPos.getY())
			pathNodes.add(new Node(endPos.getX(), endPos.getY(), endPos.getZ(), 0, true, false));
		
		return new Path(pathNodes); /* The path found (hopefully) */
	}
}
