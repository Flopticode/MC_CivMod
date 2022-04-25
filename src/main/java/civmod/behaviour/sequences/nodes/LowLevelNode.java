package civmod.behaviour.sequences.nodes;

import java.util.function.BiFunction;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class LowLevelNode extends Node
{
	public static class LowLevelNodeInfo extends NodeInfo
	{
		public LowLevelGoal goal;
		
		public LowLevelNodeInfo(LowLevelNode node, ServerLevel level, BlockPos pos)
		{
			this.goal = node.goalSupplier.apply(level, pos);
		}
		
		@Override
		public String toString()
		{
			return "{goal=" + goal.toString() + "}";
		}
	}
	
	private BiFunction<ServerLevel, BlockPos, LowLevelGoal> goalSupplier;
	
	public LowLevelNode(BiFunction<ServerLevel, BlockPos, LowLevelGoal> goalSupplier, Node nextNode)
	{
		super(nextNode);
		this.goalSupplier = goalSupplier;
	}
	public LowLevelNode(BiFunction<ServerLevel, BlockPos, LowLevelGoal> goalSupplier)
	{
		this(goalSupplier, null);
	}

	public boolean tick(LowLevelNodeInfo nodeInfo, HumanoidEntity entity)
	{
		if(entity.syncGroup.getSize() > 1)
			throw new IllegalArgumentException("Multiple entities in LowLevelNode.");
		
		LowLevelGoal goal = nodeInfo.goal; 
		
		if(goal.isDone(entity))
			return true;
		else
		{
			goal.tick(entity);
			return false;
		}
	}
}
