package civmod.behaviour.goals.lowlevel;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.goals.GoalRequirement;
import civmod.custom.entity.HumanoidEntity;
import civmod.pathfinding.Path;
import civmod.pathfinding.PathFinding;
import civmod.pathfinding.PathFinding.PathFindingAlgorithm;
import civmod.pathfinding.astar.AStarWorldCartographer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;

public class TravelGoal extends LowLevelGoal
{
	protected BlockPos position;
	protected double precision;
	
	public TravelGoal(ServerLevel level, BlockPos position, double precision)
	{
		super(level);
		this.position = position;
		this.precision = precision;
	}

	@Override
	public GoalRequirement getRequirements()
	{
		return new GoalRequirement();
	}

	@Override
	public void tick(HumanoidEntity socialEntity)
	{
		travel(socialEntity);
	}

	@Override
	public boolean isDone(HumanoidEntity entity)
	{
		return entity.position().distanceTo(new Vec3(position.getX()+0.5, position.getY(), position.getZ()+0.5)) < precision
				&& entity.fallDistance <= 0.1
				&& !entity.level.getBlockState(entity.blockPosition().below()).getCollisionShape(level, entity.blockPosition().below()).isEmpty();
	}
	
	public BlockPos getPosition()
	{
		return position;
	} 
	
	private Path path = null;
	
	/**
	 * Lets the entity travel to the target position.
	 * TODO: The code is crap and should be refined soon.
	 * @param entity
	 * @return true, if the entity reached the target position, false otherwise
	 */
	protected boolean travel(HumanoidEntity entity)
	{
		if(this.isDone(entity))
			return true;
		
		if(path == null || path.isDone() || path.getNextNode().distManhattan(entity.blockPosition()) > 2)
		{
			path = PathFinding.findPath(AStarWorldCartographer.getCartographer((ServerLevel)entity.level), entity.blockPosition(), position, PathFindingAlgorithm.AStar, Float.POSITIVE_INFINITY);
			
			if(path == null)
				return false;
		}
		
		if(path.isDone())
		{
			entity.getMoveControl().setWantedPosition(position.getX()+0.5, position.getY(), position.getZ()+0.5, 0.2f * HumanoidEntity.SPEED_MULTIPLIER);
			return false;
		}
		
		if(path != null)
		{
			BlockPos nextNode = path.getNextNode();
			BlockPos entityPos = entity.blockPosition();
			BlockPos abovePos = entityPos.above();
			BlockPos belowPos = entityPos.below();
			
			while(entityPos.equals(nextNode))
			{
				path.advance();
				
				if(path.isDone())
				{
					return false;//buildStraight(entity);
				}
					
				nextNode = path.getNextNode();
			}
			
			if(!level.getBlockState(nextNode).getCollisionShape(level, nextNode).isEmpty())
			{
				level.setBlockAndUpdate(nextNode, Blocks.AIR.defaultBlockState());
			}
			
			entity.getLookControl().setLookAt(new Vec3(nextNode.getX()+0.5, nextNode.getY(), nextNode.getZ()+0.5));
			entity.getMoveControl().setWantedPosition(nextNode.getX()+0.5, nextNode.getY(), nextNode.getZ()+0.5, 0.4f * HumanoidEntity.SPEED_MULTIPLIER);
			
			if(nextNode.getY() > entityPos.getY()) /* next node.y is above entity */
			{
				if(!entity.isJumping())
				{
					if(!level.getBlockState(abovePos).isPathfindable(level, abovePos, PathComputationType.LAND))
						level.setBlockAndUpdate(abovePos, Blocks.AIR.defaultBlockState());
					entity.getJumpControl().jump();
				}
				
				if(nextNode.getX() == entityPos.getX() && nextNode.getZ() == entityPos.getZ() && level.getBlockState(belowPos).isPathfindable(level, belowPos, PathComputationType.LAND))
					level.setBlockAndUpdate(belowPos, Blocks.DIRT.defaultBlockState());
				
				entity.getMoveControl().setWantedPosition(nextNode.getX()+0.5, nextNode.getY(), nextNode.getZ()+0.5, 0.2f * HumanoidEntity.SPEED_MULTIPLIER);
			}
		}
		
		return false;
	}
	
	@Override
	public String toString()
	{
		return "{position={" + position.toString() + "}";
	}
}
