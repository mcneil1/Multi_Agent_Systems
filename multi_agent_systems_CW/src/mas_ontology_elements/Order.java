package mas_ontology_elements;

import jade.content.AgentAction;
import jade.core.AID;

public class Order implements AgentAction
{
	private AID customer;
	private Phone phone;
	private int dueDate;
	private int lateFee;
	private int quantity;
	private int price;
	
	public AID getCustomer()
	{
		return customer;
	}
	
	public void setCustomer(AID customer)
	{
		this.customer = customer;
	}
	
	
	public Phone getPhone()
	{
		return phone;
	}
	
	public void setPhone(Phone phone)
	{
		this.phone = phone;
	}
	
	
	public int getDueDate()
	{
		return dueDate;
	}
	
	public void setDueDate(int dueDate)
	{
		this.dueDate = dueDate;
	}
	
	
	public int getLateFee()
	{
		return lateFee;
	}
	
	public void setLateFee(int lateFee)
	{
		this.lateFee = lateFee;
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
