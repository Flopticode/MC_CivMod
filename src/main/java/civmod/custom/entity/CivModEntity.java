package civmod.custom.entity;

import civmod.behaviour.HumanoidInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public abstract class CivModEntity extends Monster
{
	public HumanoidInventory inventory;
	
	public CivModEntity(EntityType<? extends CivModEntity> type, Level level)
	{
		super(type, level);
		
		this.inventory = new HumanoidInventory();
	}
	
	public static AttributeSupplier setCustomAttributes()
	{
		return Mob.createMobAttributes().build();
	}
	
	@SubscribeEvent
	public static void onLivingTick(LivingUpdateEvent evt)
	{
		Entity entity = evt.getEntity();
		
		if(entity instanceof CivModEntity)
		{
			CivModEntity humanoid = (CivModEntity)entity;
			humanoid.setNoActionTime(0);
			
			if(!entity.level.isClientSide && entity.isAlive())
			{
				humanoid.socialTick();
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingDeath(LivingDeathEvent evt)
	{
		Entity entity = evt.getEntity();
		
		if(entity instanceof CivModEntity civModEntity)
			civModEntity.onDeath();
	}
	
	@Override
	protected InteractionResult mobInteract(Player player, InteractionHand hand)
	{
		if(player.level.isClientSide)
			return InteractionResult.CONSUME;
		
		this.onInteract(player, hand);
		
		return InteractionResult.SUCCESS;
	}
	
	@Override
	public void registerGoals()
	{
		super.registerGoals();
	}
	
	@Override
	public SoundEvent getAmbientSound()
	{
		return SoundEvents.VILLAGER_AMBIENT;
	}

	@Override
	public SoundEvent getDeathSound()
	{
		return SoundEvents.VILLAGER_DEATH;
	}
	
	@Override
	public int getExperienceReward(Player player)
	{
		return 1 + level.random.nextInt(2);
	}
	
	@Override
	public void playStepSound(BlockPos pos, BlockState blockState)
	{
		this.playSound(SoundEvents.ZOMBIE_VILLAGER_STEP, 0.2f, 0.5f);
	}
	
	@Override
	protected boolean shouldDespawnInPeaceful()
	{
		return false;
	}
	
	@Override
	public boolean isPreventingPlayerRest(Player player)
	{
		return false;
	}
	
	public boolean isJumping()
	{
		return jumping;
	}
	
	public abstract void socialTick();
	public abstract void onDeath();
	public abstract void onInteract(Player player, InteractionHand hand);
	public abstract void defaultBehaviour();
}
