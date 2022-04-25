package civmod.behaviour.sequences.nodes;

public abstract class Node
{
	public static abstract class NodeInfo {}
	
	public Node nextNode;
	
	public Node(Node nextNode)
	{
		this.nextNode = nextNode;
	}
	public Node()
	{
		this(null);
	}
	
	public Node then(Node nextNode)
	{
		this.nextNode = nextNode;
		return nextNode;
	}
}
