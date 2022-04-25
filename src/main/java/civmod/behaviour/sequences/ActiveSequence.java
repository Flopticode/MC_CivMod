package civmod.behaviour.sequences;

import java.util.LinkedList;

import civmod.custom.entity.HumanoidEntity;

public class ActiveSequence
{
	private LinkedList<SyncGroup> syncGroups;
	
	public ActiveSequence(Sequence sequence, LinkedList<HumanoidEntity> entities)
	{
		this.syncGroups = new LinkedList<>();
		
		SyncGroup syncGroup = new SyncGroup(this, entities, sequence.root);
		for(HumanoidEntity e : entities)
			e.syncGroup = syncGroup;
		
		syncGroups.add(syncGroup);
	}
	
	public LinkedList<SyncGroup> getSyncGroups()
	{
		return syncGroups;
	}
	public void addSyncGroup(SyncGroup syncGroup)
	{
		syncGroups.add(syncGroup);
	}
	public boolean removeSyncGroup(SyncGroup syncGroup)
	{
		return syncGroups.remove(syncGroup);
	}
}
