package civmod.behaviour.sequences.nodes;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.sequences.SyncGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class BuildNode extends TaskPoolNode
{
	public static class BuildNodeInfo extends TaskPoolNodeInfo
	{
		public BuildNodeInfo(BuildNode node, ServerLevel level, BlockPos position)
		{
			super(node, level, position);
		}
	}
	
	public BuildNode(List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> goalsSupplier, Node nextNode)
	{
		super(goalsSupplier, nextNode);
	}
	public BuildNode(BiFunction<ServerLevel, BlockPos, LowLevelGoal>[] goalsSupplier, Node nextNode)
	{
		this(Arrays.asList(goalsSupplier), nextNode);
	}
	public BuildNode(List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> goalsSupplier)
	{
		this(goalsSupplier, null);
	}
	
	@Override
	public boolean tick(TaskPoolNodeInfo nodeInfo, SyncGroup syncGroup)
	{
		if(syncGroup.getSize() != 1)
			throw new IllegalStateException("BuildNodes expect exactly 1 worker.");
		
		return super.tick(nodeInfo, syncGroup);
	}
}
