package civmod.behaviour.goals.lowlevel;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class HarvestBlockGoal extends BreakBlockGoal
{
	public HarvestBlockGoal(ServerLevel level, Block block, BlockPos center, int minY, int maxY, int searchRadius)
	{
		super(level, findBlock(level, block, center, minY, maxY, searchRadius));
	}
	
	public static BlockPos findBlock(ServerLevel level, Block block, BlockPos center, int minY, int maxY, int radius)
	{
		for(int y = minY; y <= maxY; y++)
			for(int x = -radius; x <= radius; x++)
				for(int z = -radius; z <= radius; z++)
				{
					BlockPos curBlockPos = new BlockPos(x,y,z);
					if(level.getBlockState(curBlockPos).getBlock() == block)
						return curBlockPos;
				}
		
		return null;
	}
}
