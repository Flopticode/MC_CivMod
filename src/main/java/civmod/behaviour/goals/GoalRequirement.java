package civmod.behaviour.goals;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import civmod.behaviour.HumanoidInventory;
import civmod.custom.entity.HumanoidEntity;
import civmod.custom.entity.HumanoidKingEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.PickaxeItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.Tiers;

public class GoalRequirement
{
	public static enum ReqEnum
	{
		TOOL_WOODEN_PICKAXE,
		TOOL_STONE_PICKAXE,
		TOOL_GOLDEN_PICKAXE,
		TOOL_IRON_PICKAXE,
		TOOL_DIAMOND_PICKAXE;
		
		public boolean isFulfilled(HumanoidInventory... inventories)
		{
			return switch(this) {
				case TOOL_WOODEN_PICKAXE -> anyHasItem(inventories, Items.WOODEN_PICKAXE);
				case TOOL_STONE_PICKAXE -> anyHasItem(inventories, Items.STONE_PICKAXE);
				case TOOL_GOLDEN_PICKAXE -> anyHasItem(inventories, Items.GOLDEN_PICKAXE);
				case TOOL_IRON_PICKAXE -> anyHasItem(inventories, Items.IRON_PICKAXE);
				case TOOL_DIAMOND_PICKAXE -> anyHasItem(inventories, Items.DIAMOND_PICKAXE);
				default -> false;
			};
		}
		
		private static boolean anyHasItem(HumanoidInventory[] invs, Item item, int size)
		{
			for(HumanoidInventory inv : invs)
				if(inv.hasItem(item, size))
					return true;
			return false;
		}
		private static boolean anyHasItem(HumanoidInventory[] invs, Item item)
		{
			return anyHasItem(invs, item, 1);
		}
		
		public static ReqEnum fromTool(DiggerItem tool)
		{
			if(tool instanceof PickaxeItem)
				return fromPickaxeItem((PickaxeItem)tool);
			else
				return null;
		}
		public static ReqEnum fromPickaxeItem(PickaxeItem pickaxe)
		{
			Tier tier = pickaxe.getTier();
			
			if(tier == Tiers.WOOD)
				return TOOL_WOODEN_PICKAXE;
			else if(tier == Tiers.STONE)
				return TOOL_STONE_PICKAXE;
			else if(tier == Tiers.GOLD)
				return TOOL_GOLDEN_PICKAXE;
			else if(tier == Tiers.DIAMOND)
				return TOOL_DIAMOND_PICKAXE;
			else if(tier == Tiers.IRON)
				return TOOL_IRON_PICKAXE;
			else
				return null;
		}
	}
	
	private Map<ReqEnum, Boolean> reqs;
	
	public GoalRequirement(ReqEnum[] reqs)
	{
		this.reqs = new HashMap<>();
		for(ReqEnum req : reqs)
			this.reqs.put(req, true);
	}
	public GoalRequirement(LinkedList<ReqEnum> reqs)
	{
		this((ReqEnum[])reqs.toArray());
	}
	public GoalRequirement(Map<ReqEnum, Boolean> reqs)
	{
		this.reqs = new HashMap<>();
		reqs.forEach((req, b)->this.reqs.put(req, b));
	}
	public GoalRequirement(Object... reqBoolList)
	{
		if(reqBoolList.length % 2 != 0)
			throw new IllegalArgumentException("reqBoolList is of invalid size.");
		
		this.reqs = new HashMap<>();
		for(int i = 0; i < reqBoolList.length; i+=2)
		{
			if((reqBoolList[i] instanceof ReqEnum) && (reqBoolList[i+1] instanceof Boolean))
			{
				this.reqs.put((ReqEnum)reqBoolList[i], (boolean)reqBoolList[i+1]);
			}
			else
			{
				this.reqs.clear();
				throw new IllegalArgumentException("reqBoolList is invalid. (ReqEnum, Boolean, ReqEnum, Boolean, ... required)");
			}
		}
	}
	
	private static class BoolRet{
		public volatile boolean ret;
	}
	public boolean hasRequirements(HumanoidEntity entity, boolean canAccessSocialInv)
	{
		BoolRet ret = new BoolRet();
		ret.ret = true;
		
		reqs.forEach((req, b)-> {
			if(canAccessSocialInv)
				if(req.isFulfilled(entity.inventory, HumanoidKingEntity.getEntity((ServerLevel)entity.level).inventory) != b)
					ret.ret = false;
		});
		
		return ret.ret;
	}
}
