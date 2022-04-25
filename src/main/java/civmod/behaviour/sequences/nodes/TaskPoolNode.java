package civmod.behaviour.sequences.nodes;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.sequences.SyncGroup;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class TaskPoolNode extends Node
{
	public static class TaskPoolNodeInfo extends NodeInfo
	{
		public List<LowLevelGoal> goals;
		
		public List<LowLevelGoal> assignedGoals;
		public List<HumanoidEntity> assignees;
		
		public TaskPoolNodeInfo(TaskPoolNode node, ServerLevel level, BlockPos position)
		{
			this.assignedGoals = new LinkedList<>();
			this.assignees = new LinkedList<>();
			this.goals = new LinkedList<>();
			
			for(BiFunction<ServerLevel, BlockPos, LowLevelGoal> goalSupplier : node.goalsSupplier)
				this.goals.add(goalSupplier.apply(level, position));
		}
		
		@Override
		public String toString()
		{
			String str = "{goals={";
			for(LowLevelGoal goal : goals)
				str += goal.getClass().getSimpleName() + ",";
			str += "},assignments={";
			for(HumanoidEntity entity : this.assignees)
				str += entity.getId() + ",";
			return str + "}}";
		}
	}
	
	protected List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> goalsSupplier;
	
	public TaskPoolNode(List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> goalsSupplier, Node nextNode)
	{
		super(nextNode);
		this.goalsSupplier = goalsSupplier;
	}
	public TaskPoolNode(List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> goalsSupplier)
	{
		this(goalsSupplier, null);
	}

	public boolean tick(TaskPoolNodeInfo nodeInfo, SyncGroup callingGroup)
	{
		/* update assigned goals in case a member left the group (prevent assignment leak) */
		for(int i = 0; i < nodeInfo.assignedGoals.size(); i++)
		{
			if(!callingGroup.isMember(nodeInfo.assignees.get(i)))
			{
				nodeInfo.assignees.remove(i);
				nodeInfo.assignedGoals.remove(i);
			}
		}
		
		for(HumanoidEntity member : callingGroup.getMembers())
		{
			int assigneeIndex = nodeInfo.assignees.indexOf(member);
			LowLevelGoal assignedGoal = assigneeIndex == -1 ? null : nodeInfo.assignedGoals.get(assigneeIndex);
			
			if(assignedGoal != null)
			{
				if(assignedGoal.isDone(member))
				{
					nodeInfo.goals.remove(assignedGoal);
					nodeInfo.assignedGoals.remove(assignedGoal);
					nodeInfo.assignees.remove(member);
					
					assignedGoal = null;
				}
				else
				{
					assignedGoal.tick(member);
				}
			}			
			
			if(assignedGoal == null)
			{
				for(int i = 0; i < nodeInfo.goals.size(); i++)
				{
					LowLevelGoal curGoal = nodeInfo.goals.get(i);
					
					if(!nodeInfo.assignedGoals.contains(curGoal) && member.hasCapabilityFor(nodeInfo.goals.get(i), true))
					{
						nodeInfo.assignedGoals.add(curGoal);
						nodeInfo.assignees.add(member);
						break;
					}
				}
			}
		}
		
		return nodeInfo.goals.isEmpty();
	}
}
