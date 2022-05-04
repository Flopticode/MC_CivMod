package civmod.pathfinding.baritone;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.GoalXZ;
import civmod.pathfinding.Path;
import civmod.pathfinding.WorldCartographer;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber
public abstract class BaritoneConnector
{
	public static Path findPath(WorldCartographer wc, BlockPos sPos, BlockPos ePos, float maxCost)
	{
		return null;
	}
	
	@SubscribeEvent
	public static void onChat(ClientChatEvent evt)
	{
		System.out.println("Done. (00)" + BaritoneAPI.getProvider().getBaritoneForPlayer(Minecraft.getInstance().player).getCustomGoalProcess().isActive());
		BaritoneAPI.getSettings().allowSprint.value = true;
		BaritoneAPI.getSettings().primaryTimeoutMS.value = 2000L;
		//BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(new GoalXZ(0, 0));
		BaritoneAPI.getProvider().getBaritoneForPlayer(Minecraft.getInstance().player).getCustomGoalProcess().setGoalAndPath(new GoalXZ(0, 0));
		System.out.println("Done. (00)" + BaritoneAPI.getProvider().getBaritoneForPlayer(Minecraft.getInstance().player).getCustomGoalProcess().isActive());
	}
	
	@SubscribeEvent
	public static void onTick(TickEvent evt)
	{
		
	}
}
