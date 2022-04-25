package civmod.behaviour.sequences;

import java.util.LinkedList;

import civmod.behaviour.sequences.nodes.AsyncNode;
import civmod.behaviour.sequences.nodes.AsyncNode.AsyncNodeInfo;
import civmod.behaviour.sequences.nodes.LowLevelNode;
import civmod.behaviour.sequences.nodes.LowLevelNode.LowLevelNodeInfo;
import civmod.behaviour.sequences.nodes.Node;
import civmod.behaviour.sequences.nodes.Node.NodeInfo;
import civmod.behaviour.sequences.nodes.SyncNode;
import civmod.behaviour.sequences.nodes.SyncNode.SyncNodeInfo;
import civmod.behaviour.sequences.nodes.TaskPoolNode;
import civmod.behaviour.sequences.nodes.TaskPoolNode.TaskPoolNodeInfo;
import civmod.custom.entity.HumanoidEntity;
import net.minecraft.server.level.ServerLevel;

public class SyncGroup
{
	private boolean advance = false;
	private int targetSize;
	private ActiveSequence activeSequence;
	private LinkedList<HumanoidEntity> entities;
	private HumanoidEntity leader;
	public Node curNode;
	public NodeInfo curNodeInfo;
	
	public SyncGroup(ActiveSequence activeSequence, LinkedList<HumanoidEntity> entities, Node curNode)
	{
		if(entities.size() == 0)
			throw new IllegalArgumentException("SyncGroups need at least one member.");
		
		this.leader = entities.get(0);
		this.entities = entities;
		this.activeSequence = activeSequence;
		this.targetSize = entities.size();
		
		setNode(curNode);
	}
	
	public int getSize()
	{
		return entities.size();
	}
	
	public HumanoidEntity getLeader()
	{
		return leader;
	}
	
	public LinkedList<HumanoidEntity> getMembers()
	{
		return entities;
	}
	public boolean isMember(HumanoidEntity entity)
	{
		return entities.contains(entity);
	}
	
	public void setNode(Node node)
	{
		this.curNode = node;
		
		HumanoidEntity humanoid = getLeader();
		
		if(curNode instanceof LowLevelNode)
			this.curNodeInfo = new LowLevelNodeInfo((LowLevelNode)curNode, (ServerLevel)humanoid.level, humanoid.blockPosition());
		else if(curNode instanceof SyncNode)
			this.curNodeInfo = new SyncNodeInfo((SyncNode)node, activeSequence);
		else if(curNode instanceof TaskPoolNode)
			this.curNodeInfo = new TaskPoolNodeInfo((TaskPoolNode)curNode, (ServerLevel)humanoid.level, humanoid.blockPosition());
		else if(curNode instanceof AsyncNode)
			this.curNodeInfo = new AsyncNodeInfo((AsyncNode)curNode);
		else
			throw new IllegalStateException("Invalid node type.");
	}
	
	public void update(HumanoidEntity callingEntity)
	{
		if(callingEntity == leader)
		{
			if(advance && entities.size() == targetSize)
			{
				if(curNode.nextNode == null)
				{
					dissolve();
					return;
				}
				else
					setNode(curNode.nextNode);
				
				advance = false;
			}
			
			boolean nodeDone;
			
			if(curNode instanceof LowLevelNode)
				nodeDone = ((LowLevelNode)curNode).tick((LowLevelNodeInfo)curNodeInfo, entities.getFirst());
			else if(curNode instanceof SyncNode)
				nodeDone = ((SyncNode)curNode).tick((SyncNodeInfo)curNodeInfo, this);
			else if(curNode instanceof TaskPoolNode)
				nodeDone = ((TaskPoolNode)curNode).tick((TaskPoolNodeInfo)curNodeInfo, this);
			else if(curNode instanceof AsyncNode)
				nodeDone = ((AsyncNode)curNode).tick((AsyncNodeInfo)curNodeInfo, this);
			else
				throw new IllegalStateException("Invalid node type.");
			
			if(nodeDone)
				this.advance();
		}
	}
	
	public void advance()
	{
		advance = true;
	}
	
	public void dissolve()
	{
		for(HumanoidEntity entity : entities)
			entity.syncGroup = null;
		
		this.activeSequence.removeSyncGroup(this);
	}
	public boolean merge(SyncGroup syncGroup)
	{
		if(syncGroup == this)
			return false;
		
		for(HumanoidEntity entity : syncGroup.entities)
		{
			entity.syncGroup = this;
			this.entities.add(entity);
		}
		
		syncGroup.dissolve();
		
		return true;
	}
	public SyncGroup[] divide(int subgroupCount)
	{
		SyncGroup[] newGroups = new SyncGroup[subgroupCount];
		
		@SuppressWarnings("unchecked")
		LinkedList<HumanoidEntity>[] groupEntities = new LinkedList[subgroupCount];
		
		for(int index = 0; index < entities.size(); index++)
		{
			HumanoidEntity entity = entities.get(index);
			
			if(groupEntities[index % subgroupCount] == null)
				groupEntities[index % subgroupCount] = new LinkedList<>();
				
			groupEntities[index % subgroupCount].add(entity);
		}
		
		for(int index = 0; index < newGroups.length; index++)
		{
			newGroups[index] = new SyncGroup(this.activeSequence, groupEntities[index], this.curNode);
			activeSequence.addSyncGroup(newGroups[index]);
			
			for(HumanoidEntity entity : groupEntities[index])
			{
				entity.syncGroup = newGroups[index];
			}
		}
		
		this.activeSequence.removeSyncGroup(this);
		
		return newGroups;
	}
	public boolean removeMember(HumanoidEntity member)
	{
		if(entities.remove(member))
		{
			member.syncGroup = null;
			return true;
		}
		return false;
	}
	
	public String getTaskStr()
	{
		return this.curNodeInfo.toString();
	}
}
