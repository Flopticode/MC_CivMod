package civmod.behaviour;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HumanoidInventory
{
	private Map<Item, Integer> items = new HashMap<>();
	
	public void add(ItemStack iStack)
	{
		add(iStack.getItem(), iStack.getCount());
	}
	public void add(Item item, int size)
	{
		Integer curSize = items.get(item);
		
		if(curSize == null)
			items.put(item, size);
		else
			items.put(item, curSize+size);
	}
	public void addAll(List<ItemStack> iStacks)
	{
		iStacks.forEach((iStack)->add(iStack));
	}
	public boolean remove(Item item, int count)
	{
		int curCount = getCount(item);
		
		if(curCount == count)
			items.remove(item);
		else if(curCount > count)
			items.put(item, curCount-count);
		else
			return false;
		
		return true;
	}
	public void clear()
	{
		items.clear();
	}
	public int getCount(Item item)
	{
		Integer count = items.get(item);
		return count == null ? 0 : count;
	}
	public boolean hasItem(Item item, int minCount)
	{
		return getCount(item) >= minCount;
	}
	public boolean hasItem(Item item)
	{
		return hasItem(item, 1);
	}
	
	public void merge(HumanoidInventory inv)
	{
		inv.items.forEach((item, count)->this.add(item, count));
	}
}
