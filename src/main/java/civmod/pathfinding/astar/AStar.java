package civmod.pathfinding.astar;

import java.util.Iterator;
import java.util.LinkedList;

import civmod.pathfinding.Path;
import civmod.pathfinding.PriorityQueue;
import civmod.pathfinding.WorldCartographer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public abstract class AStar
{
	@SubscribeEvent
	public static void onChat(ServerChatEvent evt)
	{
		if(evt.getMessage().toLowerCase().contains("nav"))
		{
			String[] p = evt.getMessage().split(" ");
			
			try
			{
				int x = Integer.parseInt(p[1]);
				int y = Integer.parseInt(p[2]);
				int z = Integer.parseInt(p[3]);
				
				Path path = AStar.findPath(AStarWorldCartographer.getCartographer((ServerLevel)evt.getPlayer().level), evt.getPlayer().blockPosition(), new BlockPos(x, y, z), Float.POSITIVE_INFINITY);
			
				if(path == null)
					System.err.println("No path found.");
				else
					System.err.println("Path: " + path);
			}
			catch(NumberFormatException fne)
			{
				
			}
		}
	}
	
	public static class Node
	{
		public BlockPos position;
		public Node prev;
		public float gCost;
		public float fCost;
		public float hCost;
		
		public Node(BlockPos position, Node prev, float gCost)
		{
			this.position = position;
			this.prev = prev;
			this.gCost = gCost;
		}
		public Node(BlockPos position, float gCost)
		{
			this(position, null, gCost);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof Node && ((Node)obj).position.equals(position);
		}
	}
	
	public static Path findPath(AStarWorldCartographer wc, BlockPos sPos, BlockPos ePos, float maxCost)
	{
		System.out.println(sPos + " => " + ePos);
		
		PriorityQueue<Node> openlist = new PriorityQueue<>();
		LinkedList<Node> closedlist = new LinkedList<>();
		LinkedList<Node> pathFound = new LinkedList<>();
		
		openlist.addElement(new Node(sPos, null, 0), 0);
		
		do
		{
			Node currentNode = openlist.front();
			openlist.dequeue();
			
			//if(currentNode != null) return null;
			
			if(currentNode.position.equals(ePos))
			{
				while(currentNode.position != sPos)
				{
					pathFound.add(0, currentNode);
					currentNode = currentNode.prev;
				}
				return toPath(pathFound);
			}
			
			closedlist.add(currentNode);
			
			expandNode(wc, openlist, closedlist, currentNode, ePos, sPos);
		} while(!openlist.isEmpty());
		
		return null;
	}
	private static void expandNode(AStarWorldCartographer wc, PriorityQueue<Node> openlist, LinkedList<Node> closedlist, Node currentNode, BlockPos ePos, BlockPos sPos)
	{
		Level level = wc.getLevel();
		BlockPos curPos = currentNode.position;
		
		Node[] successorNodes = new Node[] {
			new Node(curPos.above(), getCostFromTo(level, curPos.above(), sPos, false)),
			new Node(curPos.below(), getCostFromTo(level, curPos.below(), sPos, false)),
			new Node(curPos.north(), getCostFromTo(level, curPos.north(), sPos, false)),
			new Node(curPos.south(), getCostFromTo(level, curPos.south(), sPos, false)),
			new Node(curPos.east(), getCostFromTo(level, curPos.east(), sPos, false)),
			new Node(curPos.west(), getCostFromTo(level, curPos.west(), sPos, false)),
		};
		
		for(Node successor : successorNodes)
		{
			if(level.isOutsideBuildHeight(successor.position))
				continue;
			
			if(level.getBlockState(successor.position).is(Blocks.WATER))
				continue;
			
			if(level.getBlockState(successor.position.below()).is(Blocks.WATER))
				continue;
			
			if(closedlist.contains(successor))
				continue;
			
			float tentative_g = currentNode.gCost + getCost(level, successor.position, ePos, true);
			
			if(openlist.contains(successor) && tentative_g >= successor.gCost)
				continue;
			
			successor.prev = currentNode;
			successor.gCost = tentative_g;
			
			float fCost = tentative_g + getCostFromTo(level, curPos, ePos, true);
			
			if(openlist.contains(successor))
				openlist.updatePrio(successor, fCost);
			else
				openlist.addElement(successor, fCost);
		}
	}
	
	private static float getCostFromTo(Level level, BlockPos from, BlockPos to, boolean ePosZero)
	{
		float cost = 0;
		BlockPos p = from;
		while(!p.equals(to))
		{
			int xDif = p.getX()-to.getX();
			int yDif = p.getY()-to.getY();
			int zDif = p.getZ()-to.getZ();
			
			if(Math.abs(xDif) >= Math.abs(yDif) && Math.abs(xDif) >= Math.abs(zDif))
				p = p.offset(xDif > 0 ? -1 : 1, 0, 0);
			else if(Math.abs(yDif) >= Math.abs(xDif) && Math.abs(yDif) >= Math.abs(zDif))
				p = p.offset(0, yDif > 0 ? -1 : 1, 0);
			else if(Math.abs(zDif) >= Math.abs(yDif) && Math.abs(zDif) >= Math.abs(xDif))
				p = p.offset(0, 0, zDif > 0 ? -1 : 1);
			
			cost += getCost(level, p, to, ePosZero);
		}
		return cost;
	}
	
	private static Path toPath(LinkedList<Node> nodes)
	{
		BlockPos[] pos = new BlockPos[nodes.size()];
		
		Iterator<Node> i = nodes.iterator();
		int index = 0;
		
		while(i.hasNext())
		{
			pos[index] = i.next().position;
			index++;
		}
		
		return new Path(pos);
	}
	private static float getCost(Level level, BlockPos pos, BlockPos ePos, boolean ePosZero)
	{
		if(pos.equals(ePos) && ePosZero)
			return 0;
		
		float cost = WorldCartographer.WALK_COST;
		
		BlockState state = level.getBlockState(pos);
		float destroyTime = state.getBlock().defaultDestroyTime();
		
		if(destroyTime == -1)
			destroyTime = Float.MAX_VALUE;
		else if(destroyTime > 0)
			destroyTime += 2; /* Punish algorithm for destroying blocks.
			It should rather find a path that already exists to not destroy the entire world
			and cause death of humanity due to climate change */
		
		cost += destroyTime;
		
		if(state.isAir() && level.getBlockState(pos.below()).isAir())
		{
			cost += WorldCartographer.JUMP_COST;
		}
		
		return cost;
	}
}
