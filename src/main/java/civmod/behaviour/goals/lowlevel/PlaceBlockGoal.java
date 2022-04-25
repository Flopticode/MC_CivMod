package civmod.behaviour.goals.lowlevel;

import civmod.behaviour.goals.Goal.BlockInteractionGoal;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class PlaceBlockGoal extends BlockInteractionGoal
{
	private Block block;
	
	public PlaceBlockGoal(ServerLevel level, BlockPos position, Block block)
	{
		super(level, position);
		this.block = block;
	}
	
	@Override
	public String toString()
	{
		return "{placepos=" + interactPos.toString() + ",block=" + block.getRegistryName().toString() + "}";
	}

	@Override
	public boolean isDone(HumanoidEntity socialEntity)
	{
		return this.level.getBlockState(this.interactPos).getBlock() == block;
	}

	@Override
	public void interact(HumanoidEntity entity)
	{
		this.level.setBlockAndUpdate(interactPos, block.defaultBlockState());
		System.out.println("Remove worked: " + entity.inventory.remove(block.asItem(), 1));
	}


}
