package civmod.behaviour.goals.lowlevel;

import civmod.behaviour.goals.Goal.BlockInteractionGoal;
import civmod.behaviour.goals.GoalRequirement;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public class BreakBlockGoal extends BlockInteractionGoal
{
	public BreakBlockGoal(ServerLevel level, BlockPos position)
	{
		super(level, position);
	}
	
	@Override
	public String toString()
	{
		return "{breakpos=" + interactPos.toString() + "}";
	}
	
	@Override
	public void interact(HumanoidEntity entity)
	{
		entity.inventory.addAll(Block.getDrops(entity.level.getBlockState(interactPos), (ServerLevel)entity.level, interactPos, entity.level.getBlockEntity(interactPos)));
		entity.level.destroyBlock(interactPos, false, entity);
	}
	
	@Override
	public boolean isDone(HumanoidEntity entity)
	{
		return entity.level.getBlockState(interactPos).isAir();
	}
	
	@Override
	public GoalRequirement getRequirements()
	{
		return new GoalRequirement();//ReqEnum.fromPickaxeItem(level.getBlockState(position). harvest tool function? idk));
	}
}