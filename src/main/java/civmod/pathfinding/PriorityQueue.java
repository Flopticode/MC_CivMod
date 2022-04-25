package civmod.pathfinding;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Function;

public class PriorityQueue<T>
{
	private LinkedList<Pair<T, Float>> elements = new LinkedList<>();

	public static class Pair<T1, T2>
	{
		public T1 key;
		public T2 value;
		
		public Pair(T1 k, T2 v)
		{
			key = k;
			value = v;
		}
		
		public T1 getKey()
		{
			return key;
		}
		public T2 getValue()
		{
			return value;
		}
	}
	
	public PriorityQueue()
	{
		
	}
	
	public boolean contains(T element)
	{
		for(Pair<T, Float> e : elements)
			if(e.key == element)
				return true;
		return false;
	}
	public boolean contains(Function<Pair<T, Float>, Boolean> predicate)
	{
		for(Pair<T, Float> e : elements)
			if(predicate.apply(e))
				return true;
		return false;
	}
	public boolean isEmpty()
	{
		return elements.isEmpty();
	}
	public void dequeue()
	{
		elements.removeLast();
	}
	
	public void addElement(T element, float priority)
	{
		Iterator<Pair<T,Float>> iterator = elements.iterator();
		int index = 0;
		
		while(iterator.hasNext())
		{
			Pair<T, Float> pair = iterator.next();
			
			if(priority > pair.value)
				break;
			
			index++;
		}
		
		elements.add(index, new Pair<T, Float>(element, priority));
	}
	public void updatePrio(T element, float priority)
	{
		elements.remove(element);
		this.addElement(element, priority);
	}
	public T front()
	{
		return elements.getLast().key;
	}
}
