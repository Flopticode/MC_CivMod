package civmod.behaviour.editor;

import java.awt.Graphics;
import java.awt.Point;

import civmod.behaviour.sequences.Sequence;

public class SequenceRenderer
{
	private Sequence sequence;
	private Point position;
	
	public SequenceRenderer(Sequence sequence, Point position)
	{
		this.sequence = sequence;
		this.position = position;
	}
	
	public Sequence getSequence()
	{
		return sequence;
	}
	public Point getPosition()
	{
		return (Point)position.clone();
	}
	
	public void render(Graphics g)
	{
		
	}
}
