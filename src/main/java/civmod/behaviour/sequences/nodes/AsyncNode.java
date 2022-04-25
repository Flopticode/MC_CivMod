package civmod.behaviour.sequences.nodes;

import civmod.behaviour.sequences.SyncGroup;

public class AsyncNode extends Node
{
	public static class AsyncNodeInfo extends NodeInfo
	{
		public int subgroupCount;
		
		public AsyncNodeInfo(AsyncNode node)
		{
			subgroupCount = node.subgroupCount;
		}
		
		public String toString()
		{
			return "AsyncNode {subgroups=" + subgroupCount + "}";
		}
	}
	
	private int subgroupCount;
	private Node[] nextNodes;
	
	public AsyncNode(int subgroupCount, Node... nextNodes)
	{
		super(null);
		
		this.subgroupCount = subgroupCount;
		this.nextNodes = nextNodes;
	}
	
	@Override
	public Node then(Node node)
	{
		throw new UnsupportedOperationException("then(Node) is unsupported for type AsyncNode. Pass nodes in constructor instead.");
	}
	
	public boolean tick(AsyncNodeInfo nodeInfo, SyncGroup callingGroup)
	{
		if(nodeInfo.subgroupCount > callingGroup.getSize())
			throw new IllegalStateException("Cannot devide group of " + callingGroup.getSize() + " in " + nodeInfo.subgroupCount + " subgroups. In other words: WAS MACHST DU DA?!?");
		
		SyncGroup[] newGroups = callingGroup.divide(nodeInfo.subgroupCount);
		
		for(int index = 0; index < newGroups.length; index++)
			newGroups[index].setNode(index >= nextNodes.length ? null : nextNodes[index]);
		
		return false; // Return "Not done" because node was already advanced.
	}
}
