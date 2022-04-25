package civmod.behaviour.sequences.nodes;

import java.util.LinkedList;

import civmod.behaviour.sequences.ActiveSequence;
import civmod.behaviour.sequences.SyncGroup;

public class SyncNode extends Node
{
	public static class SyncNodeInfo extends NodeInfo
	{
		public SyncNode syncNode;
		public LinkedList<SyncGroup> groupsToSync;
		
		public SyncNodeInfo(SyncNode node, ActiveSequence activeSequence)
		{
			this.syncNode = node;
			
			groupsToSync = new LinkedList<>();
			
			int[] indices = node.groupIndicesToSync;
			
			for(int index : indices)
				this.groupsToSync.add(activeSequence.getSyncGroups().get(index));
		}
		
		@Override
		public String toString()
		{
			String str = "{syncgroups={";
			for(int index : syncNode.groupIndicesToSync)
				str += index + ",";
			return str + "}}";
		}
	}
	
	private int[] groupIndicesToSync; 
	
	public SyncNode(int[] groupIndicesToSync, Node nextNode)
	{
		super(nextNode);
		this.groupIndicesToSync = groupIndicesToSync;
	}
	public SyncNode(int[] groupIndicesToSync)
	{
		this(groupIndicesToSync, null);
	}

	public boolean tick(SyncNodeInfo nodeInfo, SyncGroup callingGroup)
	{
		for(SyncGroup syncGroup : nodeInfo.groupsToSync)
			if(syncGroup.curNode != this)
				return false;
		
		for(SyncGroup syncGroup : nodeInfo.groupsToSync)
			if(syncGroup != callingGroup)
				callingGroup.merge(syncGroup);
		
		return true;
	}
}
