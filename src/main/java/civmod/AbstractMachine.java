package civmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class AbstractMachine<TransitionType, ContentType>
{
	public static class Builder<TransitionType, ContentType>
	{
		private int nextID = Integer.MIN_VALUE;
		public LinkedList<AbstractMachineNode.NodeBuilder<TransitionType, ContentType>> nodes = new LinkedList<>();
		
		public Builder()
		{
			
		}
		
		public AbstractMachineNode.NodeBuilder<TransitionType, ContentType> node(ContentType content)
		{
			AbstractMachineNode.NodeBuilder<TransitionType, ContentType> n = new AbstractMachineNode.NodeBuilder<TransitionType, ContentType>(this).content(content);
			nodes.add(n);
			return n;
		}
		public AbstractMachine<TransitionType, ContentType> build(AbstractMachineNode.NodeBuilder<TransitionType, ContentType> root)
		{
			if(root == null)
				throw new NullPointerException("Passed parameter 'root' is null.");
			
			ArrayList<AbstractMachineNode<TransitionType, ContentType>> nodes = new ArrayList<AbstractMachineNode<TransitionType, ContentType>>(this.nodes.size());
			Iterator<AbstractMachineNode.NodeBuilder<TransitionType, ContentType>> i = this.nodes.iterator();
			
			HashMap<AbstractMachineNode<TransitionType, ContentType>, LinkedList<TransitionType>> keyMap = new HashMap<>();
			HashMap<AbstractMachineNode<TransitionType, ContentType>, LinkedList<AbstractMachineNode.NodeBuilder<TransitionType, ContentType>>> valueMap = new HashMap<>();
			
			int z = 0;
			while(i.hasNext())
			{
				AbstractMachineNode.NodeBuilder<TransitionType, ContentType> node = i.next();
				AbstractMachineNode<TransitionType, ContentType> newNode = new AbstractMachineNode<>(node.id, node.isEnd, node.content, null, null);
				
				keyMap.put(newNode, node.keys);
				valueMap.put(newNode, node.values);
				
				nodes.add(z, newNode);;
				z++;
			}
			
			for(AbstractMachineNode<TransitionType, ContentType> node : nodes)
			{
				LinkedList<TransitionType> transition = keyMap.get(node);
				LinkedList<AbstractMachineNode.NodeBuilder<TransitionType, ContentType>> value = valueMap.get(node);
				
				ArrayList<TransitionType> tList = new ArrayList<>(transition.size());
				ArrayList<AbstractMachineNode<TransitionType, ContentType>> vList = new ArrayList<>(value.size());
				
				z = 0;
				Iterator<TransitionType> i1 = transition.iterator();
				while(i1.hasNext())
				{
					tList.add(z, i1.next());
					z++;
				}
				
				z = 0;
				Iterator<AbstractMachineNode.NodeBuilder<TransitionType, ContentType>> i2 = value.iterator();
				while(i2.hasNext())
				{
					AbstractMachineNode.NodeBuilder<TransitionType, ContentType> curBuilder = i2.next();
					
					AbstractMachineNode<TransitionType, ContentType> fitNode = null;
					for(AbstractMachineNode<TransitionType, ContentType> b : nodes)
						if(b.id == curBuilder.id)
							fitNode = node;
					
					vList.add(z, fitNode);
					z++;
				}
			}
			
			for(AbstractMachineNode<TransitionType, ContentType> b : nodes)
				if(b.id == root.id)
					return new AbstractMachine<TransitionType, ContentType>(b);
			throw new IllegalArgumentException("The passed root node is not added to this builder. ");
		}
	}
	
	public static class AbstractMachineNode<TransitionType, ContentType>
	{
		public static class NodeBuilder<TransitionType, ContentType>
		{
			private LinkedList<TransitionType> keys = new LinkedList<>();
			private LinkedList<NodeBuilder<TransitionType, ContentType>> values = new LinkedList<>();
			private ContentType content;
			private boolean isEnd;
			private int id = 0;
			
			private NodeBuilder(AbstractMachine.Builder<TransitionType, ContentType> builder)
			{
				this.id = builder.nextID++;
			}
			
			public NodeBuilder<TransitionType, ContentType> content(ContentType content)
			{
				this.content = content;
				return this;
			}
			public NodeBuilder<TransitionType, ContentType> isEnd()
			{
				return this.isEnd(true);
			}
			public NodeBuilder<TransitionType, ContentType> isEnd(boolean isEnd)
			{
				this.isEnd = isEnd;
				return this;
			}
			public NodeBuilder<TransitionType, ContentType> transition(TransitionType key, NodeBuilder<TransitionType, ContentType> val)
			{
				keys.add(key);
				values.add(val);
				return this;
			}
		}
		
		public ContentType content;
		private boolean isEndState;
		private ArrayList<TransitionType> keys;
		private ArrayList<AbstractMachineNode<TransitionType, ContentType>> values;
		private int id;
		
		private AbstractMachineNode(int id, boolean isEnd, ContentType content, ArrayList<TransitionType> keys, ArrayList<AbstractMachineNode<TransitionType, ContentType>> values)
		{
			this.keys = keys;
			this.values = values;
			this.isEndState = isEnd;
			this.content = content;
			this.id = id;
		}
		
		private AbstractMachineNode<TransitionType, ContentType> insert(TransitionType key)
		{
			for(int i = 0; i < keys.size(); i++)
				if(keys.get(i).equals(key))
					return values.get(i);
			return null;
		}
		public boolean isEndState()
		{
			return isEndState;
		}
	}
	
	private AbstractMachineNode<TransitionType, ContentType> root;
	
	private AbstractMachine(AbstractMachineNode<TransitionType, ContentType> root)
	{
		this.root = root;
	}
	
	public AbstractMachineNode<TransitionType, ContentType> getRootNode()
	{
		return root;
	}
	public boolean accepts(TransitionType[] word)
	{
		AbstractMachineNode<TransitionType, ContentType> node = getNodeForWord(word);
		return node != null && node.isEndState;
	}
	public AbstractMachineNode<TransitionType, ContentType> getNodeForWord(TransitionType[] word)
	{
		AbstractMachineNode<TransitionType, ContentType> curNode = root;
		
		for(int i = 0; i < word.length && ((curNode = curNode.insert(word[i])) != null); i++) { }
		
		return curNode;
	}
}
