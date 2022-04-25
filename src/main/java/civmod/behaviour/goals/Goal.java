package civmod.behaviour.goals;

import java.util.LinkedList;

import civmod.behaviour.goals.lowlevel.TravelGoal;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public abstract class Goal
{
	protected int neededEntityCount;
	protected ServerLevel level;
	
	public Goal(ServerLevel level, int neededEntityCount)
	{
		if(!(this instanceof LowLevelGoal) && !(this instanceof HighLevelGoal))
			throw new IllegalStateException("Goals have to extend either LowLevelGoal or HighLevelGoal.");
		
		this.neededEntityCount = neededEntityCount;
		this.level = level;
	}
	
	public int getNeededEntityCount()
	{
		return neededEntityCount;
	}
	
	public static abstract class BlockInteractionGoal extends TravelGoal
	{
		public static enum Phase
		{
			TRAVEL_PHASE,
			INTERACTION_PHASE;
		}
		
		protected Phase currentPhase;
		protected BlockPos interactPos;
		
		public BlockInteractionGoal(ServerLevel level, BlockPos position)
		{
			super(level, position.above(), HumanoidEntity.RANGE);
			this.currentPhase = Phase.TRAVEL_PHASE;
			this.interactPos = position;
		}
		
		public Phase getCurrentPhase()
		{
			return currentPhase;
		}
		
		@Override
		public void tick(HumanoidEntity socialEntity)
		{
			switch(currentPhase)
			{
				case TRAVEL_PHASE:
					if(travel(socialEntity) || socialEntity.blockPosition().closerThan(interactPos, HumanoidEntity.RANGE))
						currentPhase = Phase.INTERACTION_PHASE;
					break;
				case INTERACTION_PHASE:
					interact(socialEntity);
					break;
				default:
					throw new IllegalStateException("Invalid current phase \"" + currentPhase + "\"");
			}
		}
		
		@Override
		public abstract boolean isDone(HumanoidEntity socialEntity);
		
		public abstract void interact(HumanoidEntity entity);
	}
	
	public static abstract class HighLevelGoal extends Goal
	{
		public HighLevelGoal(ServerLevel level, int neededEntityCount)
		{
			super(level, neededEntityCount);
		}

		public abstract LinkedList<LowLevelGoal> breakDown();
	}
	public static abstract class LowLevelGoal extends Goal
	{
		public LowLevelGoal(ServerLevel level)
		{
			super(level, 1);
		}
		
		public abstract GoalRequirement getRequirements();
		public abstract void tick(HumanoidEntity socialEntity);
		public abstract boolean isDone(HumanoidEntity socialEntity);
	}
}
