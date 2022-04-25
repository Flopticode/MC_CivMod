package civmod.custom.entity;

import java.util.LinkedList;
import java.util.List;

import civmod.behaviour.king.CivController;
import civmod.behaviour.sequences.Sequence;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;

public class HumanoidKingEntity extends CivModEntity
{
	private CivController civController;
	
	public HumanoidKingEntity(EntityType<? extends HumanoidKingEntity> type, Level level)
	{
		super(type, level);
		civController = new CivController();
	}
	
	public static HumanoidKingEntity getEntity(ServerLevel level)
	{
		@SuppressWarnings("unchecked")
		List<HumanoidKingEntity> entities = (List<HumanoidKingEntity>)level.getEntities(EntityTypeTest.forClass(HumanoidKingEntity.class), entity->true);
	
		return entities.size() == 0 ? null : entities.get(0);
	}
	public static AttributeSupplier setCustomAttributes()
	{
		return Mob.createMobAttributes().build();
	}
	
	public void onDeath()
	{
		System.out.println("The king is dead. Long live(d) the king!");
		
		((ServerLevel)level).getAllEntities().forEach((e)->{
			if(e instanceof HumanoidEntity humanoidEntity && humanoidEntity.syncGroup != null)
				humanoidEntity.syncGroup.dissolve();
		});
	}
	
	@Override
	public void socialTick()
	{
		LinkedList<HumanoidEntity> jobless = getJoblessWorkers((ServerLevel)level);
		
		Sequence sequence = civController.getSequence(jobless.size());
		
		if(sequence != null)
		{
			LinkedList<HumanoidEntity> newSeqEntities = new LinkedList<>();
			for(HumanoidEntity e : jobless)
			{
				if(newSeqEntities.size() == sequence.getNumEntities())
					break;
				
				newSeqEntities.add(e);
			}
			
			sequence.startSequence(newSeqEntities);
		}
	}
	
	private LinkedList<HumanoidEntity> getJoblessWorkers(ServerLevel level)
	{
		LinkedList<HumanoidEntity> jobless = new LinkedList<>();
		
		level.getEntities().getAll().forEach((e)->{
			if(e instanceof HumanoidEntity)
			{
				HumanoidEntity humanoid = (HumanoidEntity)e;
				
				if(humanoid instanceof HumanoidEntity)
				{
					if(humanoid.isJobless())
						jobless.add(humanoid);
				}
			}
		});
		
		return jobless;
	}
	
	@Override
	public void defaultBehaviour()
	{
		
	}

	@Override
	public void onInteract(Player player, InteractionHand hand)
	{
		System.out.println("I am the king.");
	}
}
