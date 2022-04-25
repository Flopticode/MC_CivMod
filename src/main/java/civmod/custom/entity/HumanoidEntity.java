package civmod.custom.entity;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.sequences.SyncGroup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class HumanoidEntity extends CivModEntity
{
	public static final short RANGE = 5;
	public static final float SPEED_MULTIPLIER = 1f;
	
	public SyncGroup syncGroup;
	
	public HumanoidEntity(EntityType<? extends HumanoidEntity> type, Level level)
	{
		super(type, level);
		
		this.syncGroup = null;
	}
	
	@Override
	public void onInteract(Player player, InteractionHand hand)
	{
		System.out.println("I am not the king and have " +
				(syncGroup == null ?
						"no group."
						:"a group that does: " + syncGroup.getTaskStr()));
	}
	
	
	
	public boolean hasCapabilityFor(LowLevelGoal goal, boolean canAccessSocialInv)
	{
		if(goal == null)
			return false;
		
		return goal.getRequirements().hasRequirements(this, canAccessSocialInv);
	}
	
	public boolean isJobless()
	{
		return syncGroup == null;
	}

	@Override
	public void defaultBehaviour()
	{
		
	}
	
	@Override
	public void socialTick()
	{
		if(level.isClientSide || !isAlive())
			return;
		
		BlockPos curBlockPos = blockPosition();
		BlockPos belowPos = curBlockPos.below();
		
		if(fallDistance > 3)
			level.setBlockAndUpdate(belowPos, Blocks.REDSTONE_BLOCK.defaultBlockState());
		
		/* Do task or default behavior */
		if(syncGroup != null)
			syncGroup.update(this);
		else
			defaultBehaviour();
	}



	@Override
	public void onDeath()
	{
		if(syncGroup != null)
			syncGroup.removeMember(this);
	}
}
