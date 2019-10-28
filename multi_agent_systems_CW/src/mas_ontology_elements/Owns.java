package mas_ontology_elements;

import jade.content.Predicate;
import jade.core.AID;

public class Owns implements Predicate
{
	private AID owner;
	private Component component;
	private int deliverySpeed;
	
	public AID getOwner()
	{
		return owner;
	}
	
	public void setOwner(AID owner)
	{
		this.owner = owner;
	}
	
	public Component getComponent()
	{
		return component;
	}
	
	public void setComponent(Component component)
	{
		this.component = component;
	}
	
	public int getDeliverySpeed()
	{
		return deliverySpeed;
	}
	
	public void setDeliverySpeed(int deliverySpeed)
	{
		this.deliverySpeed = deliverySpeed;
	}
}
