package civmod.behaviour.sequences;

import java.util.LinkedList;

import civmod.behaviour.sequences.nodes.Node;
import civmod.custom.entity.HumanoidEntity;

public class Sequence
{
	protected int numEntities;
	protected Node root;
	
	public Sequence(int numEntities, Node root)
	{
		this.numEntities = numEntities;
		this.root = root;
	}
	
	public ActiveSequence startSequence(LinkedList<HumanoidEntity> originalEntities)
	{
		if(originalEntities.size() != numEntities)
			throw new IllegalArgumentException("Sequence started with " + originalEntities.size() + " entities, but the sequence expects " + numEntities + " entities.");
			
		return new ActiveSequence(this, originalEntities);
	}
	
	public int getNumEntities()
	{
		return numEntities;
	}
}
