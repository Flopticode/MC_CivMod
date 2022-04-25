package civmod;

public class BinarySearchTree<T extends Comparable<T>>
{
	private static class BSTNode<T extends Comparable<T>>
	{
		public T content;
		public BSTNode<T> lower = null;
		public BSTNode<T> higher = null;
		public BSTNode<T> parent = null;
		
		public BSTNode(T content)
		{
			this.content = content;
		}
		
		public int getSubNodeCount()
		{
			return (higher == null ? 0 : 1) + (lower == null ? 0 : 1);
		}
		public void remove()
		{
			switch(getSubNodeCount())
			{
				case 0:
					if(parent.higher == this)
						parent.higher = null;
					else
						parent.lower = null;
				break;
				case 1:
					if(parent.higher == this)
						parent.higher = (lower == null ? higher : lower);
					else
						parent.lower = (higher == null ? lower : higher);
				break;
				case 2:
					BSTNode<T> curNode = parent.higher;
					while(curNode.lower != null)
						curNode = curNode.lower;
					
					curNode.parent = parent;
					curNode.lower = lower;
					curNode.higher = higher;
					
					if(parent.higher == this)
						parent.higher = curNode;
					else
						parent.lower = curNode;
				break;
				default:
					/* This case is not legal in a binary search tree. */
			}
		}
	}
	
	public BSTNode<T> root = null;
	
	public BinarySearchTree()
	{
		
	}
	
	public boolean contains(T content)
	{
		if(root == null)
			return false;
		
		BSTNode<T> curNode = root;
		
		while(true) /* Loops until the node was either removed or not found. The function quits due to return statements. */
		{
			int cmp = curNode.content.compareTo(content);
			
			if(cmp > 0) /* content is greater than curNode */
			{
				if(curNode.higher != null)
					curNode = curNode.higher; /* Continue searching for leaf node in higher sub-tree */
				else
					return false;
			}
			else if(cmp == 0)
				return true; /* An element that equals the new element already exists in BST */
			else if(cmp < 0) /* content is lower than curNode */
			{
				if(curNode.lower != null)
					curNode = curNode.lower; /* Continue searching for leaf node in lower sub-tree */
				else
					return false;
			}
		}
	}
	
	public boolean remove(T content)
	{
		if(root == null)
			return false;
		
		BSTNode<T> curNode = root;
		
		while(true) /* Loops until the node was either removed or not found. The function quits due to return statements. */
		{
			int cmp = curNode.content.compareTo(content);
			
			if(cmp > 0) /* content is greater than curNode */
			{
				if(curNode.higher != null)
					curNode = curNode.higher; /* Continue searching for leaf node in higher sub-tree */
				else
				{
					curNode.higher.remove();
					return true; /* Element was removed successfully */
				}
			}
			else if(cmp == 0)
				return false; /* An element that equals the new element already exists in BST */
			else if(cmp < 0) /* content is lower than curNode */
			{
				if(curNode.lower != null)
					curNode = curNode.lower; /* Continue searching for leaf node in lower sub-tree */
				else
				{
					curNode.lower.remove();
					return true; /* Element was removed successfully */
				}
			}
		}
	}
	
	private boolean add(BSTNode<T> node)
	{
		if(root == null)
		{
			node.parent = null;
			root = node;
			return true;
		}
		
		T content = node.content;
		
		BSTNode<T> curNode = root;
		
		while(true) /* Loops until the node was added. The function quits due to return statements. */
		{
			int cmp = curNode.content.compareTo(content);
			
			if(cmp > 0) /* content is greater than curNode */
			{
				if(curNode.higher != null)
					curNode = curNode.higher; /* Continue searching for leaf node in higher sub-tree */
				else
				{
					curNode.higher = node;
					node.parent = curNode;
					return true; /* Element was added successfully */
				}
			}
			else if(cmp == 0)
				return false; /* An element that equals the new element already exists in BST */
			else if(cmp < 0) /* content is lower than curNode */
			{
				if(curNode.lower != null)
					curNode = curNode.lower; /* Continue searching for leaf node in lower sub-tree */
				else
				{
					curNode.lower = node;
					node.parent = curNode;
					return true; /* Element was added successfully */
				}
			}
		}
	}
	public boolean add(T element)
	{
		return this.add(new BSTNode<T>(element));
	}
}
