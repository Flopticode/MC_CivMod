package civmod.behaviour.sequences.factories.buildings;

import java.util.LinkedList;
import java.util.List;
import java.util.function.BiFunction;

import civmod.behaviour.goals.Goal.LowLevelGoal;
import civmod.behaviour.goals.lowlevel.BreakBlockGoal;
import civmod.behaviour.goals.lowlevel.PlaceBlockGoal;
import civmod.behaviour.goals.lowlevel.TravelGoal;
import civmod.behaviour.sequences.Sequences;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;

public abstract class BuildHouse
{
	private static final List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> buildHouse = new LinkedList<>();
	
	static
	{
		// Clear area
		BlockPos[] clearPos = new BlockPos[4*5*5];
		int i = 0;
		for(int y = -1; y <=3; y++)
			for(int x = -2; x <= 2; x++)
				for(int z = -2; z <= 2; z++)
					clearPos[i++] = new BlockPos(x, y, z);
		
		for(BlockPos cP : clearPos)
			buildHouse.add((lvl,callpos)->new BreakBlockGoal(lvl, cP));
					
		
		// Build
		Sequences.addPlaceGoals(buildHouse, Blocks.OAK_PLANKS, new Vec3i[] {
			// Floor
			new Vec3i(-1, -1, -1), new Vec3i(-1, -1, 0), new Vec3i(-1, -1, 1),
			new Vec3i(0, -1, -1), new Vec3i(0, -1, 0), new Vec3i(0, -1, 1),
			new Vec3i(1, -1, -1), new Vec3i(1, -1, 0), new Vec3i(1, -1, 1),
			
			// Walls (row 1)
			new Vec3i(-2, 0, -1), new Vec3i(-2, 0, 0), new Vec3i(-2, 0, 1),
			new Vec3i(2, 0, -1), new Vec3i(2, 0, 0), new Vec3i(2, 0, 1),
			new Vec3i(-1, 0, -2), new Vec3i(0, 0, -2), new Vec3i(1, 0, -2),
			new Vec3i(-1, 0, 2), new Vec3i(0, 0, 2), new Vec3i(1, 0, 2),
		});
		
		// Build up
		buildHouse.add((level, pos)->new TravelGoal(level, pos.offset(-1, 0, 0), 0.1f));
		buildHouse.add((level, pos)->new PlaceBlockGoal(level, pos.offset(1, 0, 0), Blocks.DIRT));
		buildHouse.add((level, pos)->new TravelGoal(level, pos.offset(1, 1, 0), 0.1f));
		
		Sequences.addPlaceGoals(buildHouse, Blocks.OAK_PLANKS, new Vec3i[] {
			// Walls (row 2)
			new Vec3i(-2, 1, -1), new Vec3i(-2, 1, 0), new Vec3i(-2, 1, 1),
			new Vec3i(2, 1, -1), new Vec3i(2, 1, 0), new Vec3i(2, 1, 1),
			new Vec3i(-1, 1, -2), new Vec3i(0, 1, -2), new Vec3i(1, 1, -2),
			new Vec3i(-1, 1, 2), new Vec3i(0, 1, 2), new Vec3i(1, 1, 2),
		});
	}
	
	public static final List<BiFunction<ServerLevel, BlockPos, LowLevelGoal>> get()
	{
		return buildHouse.subList(0, buildHouse.size());
	}
}
