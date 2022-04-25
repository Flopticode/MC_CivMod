package civmod.behaviour.goals.highlevel;

import java.util.LinkedList;

import civmod.behaviour.goals.Goal.HighLevelGoal;
import civmod.behaviour.goals.lowlevel.BreakBlockGoal;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

public class ClearAreaGoal extends HighLevelGoal
{
	private int startX, startY, startZ;
	private int endX, endY, endZ;
	
	public ClearAreaGoal(ServerLevel level, int neededEntityCount, BlockPos center, int radX, int radY, int radZ)
	{
		this(level, neededEntityCount, center.getX()-radX, center.getY()-radY, center.getZ()-radZ, center.getX()+radX, center.getY()+radY, center.getZ()+radZ);
	}
	public ClearAreaGoal(ServerLevel level, int neededEntityCount, int startX, int startY, int startZ, int endX, int endY, int endZ)
	{
		super(level, neededEntityCount);
		
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}
	
	@Override
	public LinkedList<LowLevelGoal> breakDown()
	{
		LinkedList<LowLevelGoal> subGoals = new LinkedList<>();
		
		for(int y = endY; y >= startY; y--)
			for(int x = startX; x <= endX; x++)
				for(int z = startZ; z <= endZ; z++)
					subGoals.add(new BreakBlockGoal(level, new BlockPos(x, y, z)));
		
		return subGoals;		
	}
}
