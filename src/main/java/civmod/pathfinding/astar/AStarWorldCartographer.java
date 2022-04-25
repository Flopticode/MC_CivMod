package civmod.pathfinding.astar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import civmod.BinarySearchTree;
import civmod.pathfinding.WorldCartographer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;

public class AStarWorldCartographer implements WorldCartographer
{
	public static Map<ServerLevel, AStarWorldCartographer> worldCartographers = new HashMap<>();
	public BinarySearchTree<Vec3i> protectedPos = new BinarySearchTree<Vec3i>();
	public Map<Item, ArrayList<BlockPos>> resourceMap = new HashMap<>();
	public Level level;
	
	public AStarWorldCartographer(Level level)
	{
		this.level = level;
	}
	
	public static AStarWorldCartographer getCartographer(ServerLevel level)
	{
		AStarWorldCartographer c = worldCartographers.get(level);
		
		if(c == null)
			worldCartographers.put(level, c = new AStarWorldCartographer(level));
		
		return c;
	}
	
	@Override
	public void evaluateChunk(int cx, int cz)
	{
		for(int x = 0; x < WorldCartographer.CHUNK_MAX_X; x++)
		{
			for(int y = WorldCartographer.CHUNK_MIN_Y; y < WorldCartographer.CHUNK_MAX_Y; y++)
			{
				for(int z = 0; z < WorldCartographer.CHUNK_MAX_Z; z++)
				{
					BlockPos blockPos = new BlockPos(x, y, z);
					Item item = level.getBlockState(blockPos).getBlock().asItem();
					ArrayList<BlockPos> al = resourceMap.get(item);
					
					if(al == null)
						resourceMap.put(item, al = new ArrayList<>());
					
					al.add(blockPos);
				}
			}
		}
	}

	@Override
	public Level getLevel()
	{
		return level;
	}

	@Override
	public void protect(BlockPos pos)
	{
		protectedPos.add(pos);
	}

	@Override
	public boolean isProtected(BlockPos pos)
	{
		return protectedPos.contains(pos);
	}
}
