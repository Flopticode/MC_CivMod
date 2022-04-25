package civmod.pathfinding;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

public interface WorldCartographer
{
	public static final float WALK_COST = .2f;
	public static final float BUILD_COST = .1f;
	public static final float JUMP_COST = 1.5f;
	public static final short CHUNK_MAX_X = 16;
	public static final short CHUNK_MAX_Y = 320;
	public static final short CHUNK_MAX_Z = 16;
	public static final short CHUNK_MIN_Y = 0;
	
	public void protect(BlockPos pos);
	public boolean isProtected(BlockPos pos);
	public Level getLevel();
	public void evaluateChunk(int cx, int cz);
	public default void evaluateArea(int cx1, int cy1, int cx2, int cy2)
	{
		for(int x = cx1; x <= cx2; x++)
		{
			for(int y = cy1; y <= cy2; y++)
			{
				evaluateChunk(x, y);
			}
		}
	}
	public default void evaluateArea(ChunkPos pos1, ChunkPos pos2)
	{
		evaluateArea(pos1.x, pos1.z, pos2.x, pos2.z);
	}
	
	public static Map<Block, BlockPos[]> evaluateChunkResources(Level level, ChunkPos chunk)
	{
		Map<Block, LinkedList<BlockPos>> resourceMap = new HashMap<>();
		
		for(int x = 0; x < CHUNK_MAX_X; x++)
		{
			for(int y = CHUNK_MIN_Y; y < CHUNK_MAX_Y; y++)
			{
				for(int z = 0; z < CHUNK_MAX_Z; z++)
				{
					BlockPos curBlockPos = new BlockPos(chunk.x*CHUNK_MAX_X + x, y, chunk.z * CHUNK_MAX_Z + z);
					Block block = level.getBlockState(curBlockPos).getBlock();
					
					LinkedList<BlockPos> list = resourceMap.getOrDefault(block, new LinkedList<>());
					list.add(curBlockPos);
					
					resourceMap.putIfAbsent(block, list);
				}
			}
		}
		
		Map<Block, BlockPos[]> arrayResourceMap = new HashMap<>();
		
		resourceMap.forEach((b, l)-> {
			BlockPos[] arr = new BlockPos[l.size()];
			arrayResourceMap.put(b, l.toArray(arr));
		});
		
		return arrayResourceMap;
	}
}
