package mas_ontology_elements;

import jade.content.AgentAction;
import jade.core.AID;

public class Sell implements AgentAction 
{
	private AID buyer;
	private Component component;
	private int deliveryDate;
	private int quantity;
	private int price;
	
	public AID getBuyer() 
	{
		return buyer;
	}
	
	public void setBuyer(AID buyer) 
	{
		this.buyer = buyer;
	}
	
	
	public Component getComponent()
	{
		return component;
	}
	
	public void setComponent(Component component)
	{
		this.component = component;
	}
	
	
	public int getDeliveryDate()
	{
		return deliveryDate;
	}
	
	public void setDeliveryDate(int deliveryDate)
	{
		this.deliveryDate = deliveryDate;
	}
	
	public int getQuantity()
	{
		return quantity;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
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
