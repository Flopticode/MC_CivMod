package civmod.behaviour.sequences;

import java.util.List;
import java.util.function.BiFunction;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.goals.lowlevel.PlaceBlockGoal;
import civmod.behaviour.goals.lowlevel.TravelGoal;
import civmod.behaviour.sequences.factories.buildings.BuildHouse;
import civmod.behaviour.sequences.nodes.BuildNode;
import civmod.behaviour.sequences.nodes.LowLevelNode;
import civmod.custom.entity.HumanoidKingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;

public abstract class Sequences
{
	private static final BiFunction<ServerLevel, BlockPos, LowLevelGoal> travelToNextHouseSpot = (level, pos)->new TravelGoal(level, HumanoidKingEntity.getEntity(level).blockPosition().offset(level.random.nextInt(50), 0, level.random.nextInt(50)), 1);
	
	public static final Sequence SEQ_GRP_1;
	
	public static final Sequence BUILD_NEXT_HOUSE;
	
	public static final void addPlaceGoals(List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> lst, Block block, Vec3i... offsets)
	{
		for(Vec3i offset : offsets)
		{
			lst.add((lvl, callPos)->new PlaceBlockGoal(lvl, callPos.offset(offset.getX(), offset.getY(), offset.getZ()), block));
		}
	}
	
	static
	{
		BuildNode buildHouseNode = new BuildNode(BuildHouse.get());
		
		BUILD_NEXT_HOUSE = new Sequence(1, new LowLevelNode(travelToNextHouseSpot, buildHouseNode));
		
		SEQ_GRP_1 = new Sequence(1, BUILD_NEXT_HOUSE.root);
	}
}
