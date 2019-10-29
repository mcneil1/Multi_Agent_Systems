package mas_ontology_elements;

import jade.content.Predicate;
import jade.core.AID;

public class Owns implements Predicate
{
	private AID owner;
	private Component component;
	private int price;
	
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
	
	public int getPrice()
	{
		return price;
	}
	
	public void setPrice(int price)
	{
		this.price = price;
	}

}
