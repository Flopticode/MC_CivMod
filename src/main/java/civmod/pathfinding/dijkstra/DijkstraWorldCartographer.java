package civmod.pathfinding.dijkstra;

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import civmod.pathfinding.WorldCartographer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public class DijkstraWorldCartographer implements WorldCartographer
{
	public static Map<ServerLevel, DijkstraWorldCartographer> worldCartographers = new HashMap<>();
	protected Level level;
	private HashMap<Point, ChunkData> chunkData;
	
	public DijkstraWorldCartographer(Level level)
	{
		this.level = level;
		this.chunkData = new HashMap<>();
	}
	
	public static DijkstraWorldCartographer getCartographer(ServerLevel level)
	{
		DijkstraWorldCartographer c = worldCartographers.get(level);
		
		if(c == null)
			worldCartographers.put(level, c = new DijkstraWorldCartographer(level));
		
		return c;
	}
	
	public Node getNode(BlockPos pos)
	{
		Point chunkPos = new Point(pos.getX() / CHUNK_MAX_X, pos.getZ() / CHUNK_MAX_Z);
		BlockPos innerChunkPos = new BlockPos(pos.getX()-(chunkPos.x*16), pos.getY(), pos.getZ()-(chunkPos.y*16));
		
		ChunkData data = chunkData.get(chunkPos);
		
		if(data == null)
			return null;
		
		Node directNode = data.nodes[innerChunkPos.getX()][innerChunkPos.getY()][innerChunkPos.getZ()];
		
		return directNode != null ? directNode : data.clusterPositions.get(pos);
	}
	
	public PathfindNodeData getPathfindData(int x, int y, int z)
	{
		BlockPos pos = new BlockPos(x, y, z), belowPos;
		BlockState state = level.getBlockState(pos);
		BlockState belowState = level.getBlockState(belowPos = pos.offset(0, -1, 0));
		
		boolean hasCollisionShape = !state.getCollisionShape(level, pos).isEmpty();
		boolean fullCollisionShape = state.isCollisionShapeFullBlock(level, pos);
		
		Block block = state.getBlock();
		float destroyTime = block.defaultDestroyTime();
		
		if(fullCollisionShape && destroyTime < 60 && belowState.isCollisionShapeFullBlock(level, belowPos))
			return new PathfindNodeData(destroyTime+WALK_COST, false, false);
		
		if(!hasCollisionShape && !belowState.getCollisionShape(level, pos).isEmpty())
			return new PathfindNodeData(WALK_COST, true, false);
		
		if(!hasCollisionShape && belowState.getCollisionShape(level, pos).isEmpty())
		{
			for(BlockPos curBlockPos : new BlockPos[] 
				{pos.offset(-1, -1, 0),
				pos.offset(1, -1, 0),
				pos.offset(0, -1, -1),
				pos.offset(0, -1, 1)})
			{
				if(!level.getBlockState(curBlockPos).getCollisionShape(level, curBlockPos).isEmpty())
					return new PathfindNodeData(WALK_COST, true, true);
			}
		}
		
		return null;
	}
	
	private boolean checkBorders(int radius, int x, int y, int z, boolean px, boolean nx, boolean pz, boolean nz, Node[][][] nodes)
	{
		for(int i = 0; i <= radius; i++)
		{
			if(px) /* positive x */
			{
				Node curNode = nodes[x+radius][y][z+i];
				if(curNode == null || !curNode.isSurfaceNode() || curNode.isSurfaceEdgeNode())
					return false;
			}
			if(nx) /* negative x */
			{
				Node curNode = nodes[x-radius][y][z+i];
				if(curNode == null || !curNode.isSurfaceNode() || curNode.isSurfaceEdgeNode())
					return false;
			}
			
			if(pz) /* positive z */
			{
				Node curNode = nodes[x+i][y][z+radius];
				if(curNode == null || !curNode.isSurfaceNode() || curNode.isSurfaceEdgeNode())
					return false;
			}
			if(nz) /* negative z */
			{
				Node curNode = nodes[x+i][y][z-radius];
				if(curNode == null || !curNode.isSurfaceNode() || curNode.isSurfaceEdgeNode())
					return false;
			}
		}
		return true;
	}
	
	@Override
	public void evaluateChunk(int cx, int cz)
	{		
		Node[][][] nodes = new Node[CHUNK_MAX_X+1][CHUNK_MAX_Y][CHUNK_MAX_Z+1];
		
		int numNodes = 0;
		
		long ts1 = System.currentTimeMillis();
		
		/* Only evaluate dirty or unevaluated chunks */
		ChunkData data = chunkData.get(new Point(cx, cz));
		if(data == null || data.isDirty)
		{
		
			/* Create nodes for every pathfindable blockpos */
			for(int x = 0; x < CHUNK_MAX_X; x++)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int z = 0; z < CHUNK_MAX_Z; z++)
					{
						PathfindNodeData nodeData = getPathfindData(cx*16+x, y, cz*16+z);
						if(nodeData != null)
						{
							nodes[x][y][z] = new Node(cx*16+x, y, cz*16+z, nodeData.cost, nodeData.surfaceNode, nodeData.surfaceEdgeNode);
							numNodes++;
						}
					}
				}
			}
			
			/* Connect nodes */
			for(int x = 0; x < CHUNK_MAX_X; x++)
			{
				for(int y = CHUNK_MAX_Y-1; y >= CHUNK_MIN_Y; y--)
				{
					for(int z = 0; z < CHUNK_MAX_Z; z++)
					{
						if(nodes[x][y][z] != null)
						{
							LinkedList<Node> neighbors = new LinkedList<>();
							
							if(x < CHUNK_MAX_X-1 && nodes[x+1][y][z] != null)
								neighbors.add(nodes[x+1][y][z]);
							if(x > 0 && nodes[x-1][y][z] != null)
								neighbors.add(nodes[x-1][y][z]);
							
							if(z < CHUNK_MAX_Z-1 && nodes[x][y][z+1] != null)
								neighbors.add(nodes[x][y][z+1]);
							if(z > 0 && nodes[x][y][z-1] != null)
								neighbors.add(nodes[x][y][z-1]);
							
							if(y < CHUNK_MAX_Y-1 && nodes[x][y+1][z] != null)
								neighbors.add(nodes[x][y+1][z]);
							if(y > CHUNK_MIN_Y && nodes[x][y-1][z] != null)
								neighbors.add(nodes[x][y-1][z]);
							
							Node[] neighborsArr = new Node[neighbors.size()];
							nodes[x][y][z].neighbors = neighbors.toArray(neighborsArr);
						}
					}
				}
			}
			
			/* Connect to previously evaluated neighbor chunks */
			ChunkData[] neighborChunkData = new ChunkData[] {
				chunkData.get(new Point(cx-1, cz)),
				chunkData.get(new Point(cx+1, cz)),
				chunkData.get(new Point(cx, cz-1)),
				chunkData.get(new Point(cx, cz+1)),
			};
			
			int interChunkNodesConnected = 0;
			if(neighborChunkData[0] != null)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int z = 0; z < CHUNK_MAX_Z; z++)
					{
						if(neighborChunkData[0].nodes[CHUNK_MAX_X-1][y][z] != null && nodes[0][y][z] != null)
						{
							neighborChunkData[0].nodes[CHUNK_MAX_X-1][y][z].connect(nodes[0][y][z]);
							nodes[0][y][z].connect(neighborChunkData[0].nodes[CHUNK_MAX_X-1][y][z]);
							
							interChunkNodesConnected++;
						}
					}
				}
			}
			if(neighborChunkData[1] != null)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int z = 0; z < CHUNK_MAX_Z; z++)
					{
						if(neighborChunkData[1].nodes[0][y][z] != null && nodes[CHUNK_MAX_X-1][y][z] != null)
						{
							neighborChunkData[1].nodes[0][y][z].connect(nodes[CHUNK_MAX_X-1][y][z]);
							nodes[CHUNK_MAX_X-1][y][z].connect(neighborChunkData[1].nodes[0][y][z]);
							
							interChunkNodesConnected++;
						}
					}
				}
			}
			if(neighborChunkData[2] != null)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int x = 0; x < CHUNK_MAX_X; x++)
					{
						if(neighborChunkData[2].nodes[x][y][CHUNK_MAX_Z-1] != null && nodes[x][y][0] != null)
						{
							neighborChunkData[2].nodes[x][y][CHUNK_MAX_Z-1].connect(nodes[x][y][0]);
							nodes[x][y][0].connect(neighborChunkData[2].nodes[x][y][CHUNK_MAX_Z-1]);
							
							interChunkNodesConnected++;
						}
					}
				}
			}
			if(neighborChunkData[3] != null)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int x = 0; x < CHUNK_MAX_X; x++)
					{
						if(neighborChunkData[3].nodes[x][y][0] != null && nodes[x][y][CHUNK_MAX_Z-1] != null)
						{
							neighborChunkData[3].nodes[x][y][0].connect(nodes[x][y][CHUNK_MAX_Z-1]);
							nodes[x][y][CHUNK_MAX_Z-1].connect(neighborChunkData[3].nodes[x][y][0]);
							
							interChunkNodesConnected++;
						}
					}
				}
			}
			
			/* Cluster surface nodes to bigger squares if possible */
			Map<BlockPos, Node> clusterPositions = new HashMap<>();
			int numClusters = 0;
			
			boolean[][][] clustered = new boolean[nodes.length][nodes[0].length][nodes[0][0].length];
			
			for(int x = 0; x < CHUNK_MAX_X-1; x++)
			{
				for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
				{
					for(int z = 0; z < CHUNK_MAX_Z-1; z++)
					{
						if(!clustered[x][y][z] && nodes[x][y][z] != null && nodes[x][y][z].isSurfaceNode() && !nodes[x][y][z].isSurfaceEdgeNode())
						{						
							// Finding cluster bounds (extend the rectangle from 1x1 in every direction until a border is reached)
							int minX = x, maxX = x;
							int minZ = z, maxZ = z;
							
							for(; minX >= 0 && checkBorders(x-minX, x, y, z, false, true, false, false, nodes); minX--) { }
							minX++;
							for(; maxX < CHUNK_MAX_X && checkBorders(maxX-x, x, y, z, true, false, false, false, nodes); maxX++) { }
							maxX--;
							for(; minZ >= 0 && checkBorders(z-minZ, x, y, z, false, false, false, true, nodes); minZ--) { }
							minZ++;
							for(; maxZ < CHUNK_MAX_Z && checkBorders(maxZ-z, x, y, z, false, false, true, false, nodes); maxZ++) { }
							maxZ--;
							
							for(int x1 = minX; x1 <= maxX; x1++)
								for(int z1 = minZ; z1 <= maxZ; z1++)
									clustered[x1][y][z1] = true;
							
							// Cluster bounds found. Now cluster the area to one center node:
							Node centerNode = nodes[(maxX+minX)/2][y][(maxZ+minZ)/2];
							
							// Finding every neighbor
							LinkedList<Node> newNeighbors = new LinkedList<>();
							
							for(int x1 = minX; x1 <= maxX; x1++)
							{
								for(int z1 = minZ; z1 <= maxZ; z1++)
								{
									/* Add nodes that are outside the cluster area (neighbors of the cluster) */
									for(Node a : nodes[x1][y][z1].neighbors)
										if(!(a.y == y && (a.x >= minX || a.x <= maxX || a.z >= minZ || a.z <= maxZ)) && !newNeighbors.contains(a))
											newNeighbors.add(a);
									
									/* Remove nodes in the area that are not the center node*/
									if(nodes[x1][y][z1] != centerNode)
									{
										clusterPositions.put(new BlockPos(x1, y, z1).offset(cx*CHUNK_MAX_X, 0, cz*CHUNK_MAX_Z), centerNode);
										nodes[x1][y][z1] = null;
										numNodes--;
									}
								}
							}
							Node[] newNeighborsArr = new Node[newNeighbors.size()];
							newNeighbors.toArray(newNeighborsArr);
							centerNode.neighbors = newNeighborsArr;
							
							// Cluster the neighbors' connections to the centerNode
							for(Node neighbor : newNeighborsArr)
							{
								LinkedList<Node> nNeighbors = new LinkedList<>();
								boolean centerNodeAdded = false;
								for(Node n : neighbor.neighbors)
								{
									if(n.x >= minX && n.x <= maxX && n.y == y && n.z >= minZ && n.z <= maxZ)
									{
										if(!centerNodeAdded)
										{
											centerNodeAdded = true;
											nNeighbors.add(centerNode);
										}
									}
									else
									{
										nNeighbors.add(n);
									}
								}
								Node[] nNeighborsArr = new Node[nNeighbors.size()];
								nNeighbors.toArray(nNeighborsArr);
								neighbor.neighbors = nNeighborsArr;
							}
							
							numClusters++;
						}
					}
				}
			}
			
			chunkData.put(new Point(cx, cz), new ChunkData(nodes, clusterPositions));
			
			System.out.println("WorldEvaluation: Evaluated chunk (" + cx + " | " + cz + "). Found " + numNodes + " pathfindable nodes. The operation took " + (System.currentTimeMillis()-ts1) + " millis. Connected " + interChunkNodesConnected + " InterChunkNodes. Created " + numClusters  + " clusters of surface nodes.");
		}
	}
	
	public static void setChunkDirty(Level level, BlockPos pos)
	{
		if(level.isClientSide())
			return;
		
		ServerLevel sl = (ServerLevel)level;
		DijkstraWorldCartographer c = DijkstraWorldCartographer.getCartographer(sl);
		ChunkData cd = c.chunkData.get(new Point(pos.getX()/16, pos.getZ()/16));
		
		if(cd != null)
			cd.isDirty = true;
	}
	@SubscribeEvent
	public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent evt)
	{
		setChunkDirty((Level)evt.getWorld(), evt.getPos());
	}
	
	@Override
	public Level getLevel()
	{
		return level;
	}
	
	private static final class ChunkData
	{
		public Node[][][] nodes;
		public Map<BlockPos, Node> clusterPositions;
		public boolean isDirty = false;
		
		public ChunkData(Node[][][] nodes, Map<BlockPos, Node> clusterPositions)
		{
			this.nodes = nodes;
			this.clusterPositions = clusterPositions;
		}
	}
	private static final class PathfindNodeData
	{
		public float cost;
		public boolean surfaceNode;
		public boolean surfaceEdgeNode;
		
		public PathfindNodeData(float cost, boolean surfaceNode, boolean surfaceEdgeNode)
		{
			this.cost = cost;
			this.surfaceNode = surfaceNode;
			this.surfaceEdgeNode = surfaceEdgeNode;
		}
	}
	
	@Override
	public void protect(BlockPos pos)
	{
		// TODO Implement
	}

	@Override
	public boolean isProtected(BlockPos pos) {
		// TODO Implement
		return false;
	}
}
