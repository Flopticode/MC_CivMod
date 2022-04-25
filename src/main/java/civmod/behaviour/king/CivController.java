package civmod.behaviour.king;

import civmod.AbstractMachine;
import civmod.AbstractMachine.AbstractMachineNode;
import civmod.behaviour.HumanoidInventory;
import civmod.behaviour.sequences.Sequence;
import civmod.behaviour.sequences.Sequences;

public class CivController
{
	public HumanoidInventory civInventory = new HumanoidInventory();
	
	/**
	 * Der Automat, der die Aufgaben der Bevölkerung steuert. Die Übergänge sind
	 * Integer, die jeweils die ID der SyncGroups repräsentieren. Sollten also am Ende
	 * einer Sequenz mehrere SyncGroups übrig bleiben, so können diese getrennt voneinander
	 * weitere Aufgaben erledigen.
	 */
	public AbstractMachine<Integer, Sequence> sequenceController;
	
	public CivController()
	{
		AbstractMachineNode.NodeBuilder<Integer, Sequence> b1, b2;
		
		AbstractMachine.Builder<Integer, Sequence> mb = new AbstractMachine.Builder<Integer, Sequence>();
		b1 = mb.node(Sequences.SEQ_GRP_1);
		b2 = mb.node(Sequences.SEQ_GRP_1);
		
		b1.transition(1, b2);
		b2.transition(1, b1);
		
 		mb.build(b1); 
	}
	
	public Sequence getSequence(int numFreeWorkers)
	{
		return numFreeWorkers >= Sequences.SEQ_GRP_1.getNumEntities() ? Sequences.SEQ_GRP_1 : null;
	}
}
