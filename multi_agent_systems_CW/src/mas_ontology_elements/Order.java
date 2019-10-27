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
	
	private AID getCustomer()
	{
		return customer;
	}
	
	private void setCustomer(AID customer)
	{
		this.customer = customer;
	}
	
	
	private Phone getPhone()
	{
		return phone;
	}
	
	private void setPhone(Phone phone)
	{
		this.phone = phone;
	}
	
	
	private int getDueDate()
	{
		return dueDate;
	}
	
	private void setDueDate(int dueDate)
	{
		this.dueDate = dueDate;
	}
	
	
	private int getLateFee()
	{
		return lateFee;
	}
	
	private void setLateFee(int lateFee)
	{
		this.lateFee = lateFee;
	}
	
	
	private int getQuantity()
	{
		return quantity;
	}
	
	private void setQuantity(int quaantity)
	{
		this.quantity = quantity;
	}
	
	
	private int getPrice()
	{
		return price;
	}
	
	private void setPrice(int price)
	{
		this.price = price;
	}
}
