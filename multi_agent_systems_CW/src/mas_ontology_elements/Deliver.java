package mas_ontology_elements;

import jade.content.AgentAction;

public class Deliver implements AgentAction
{
	private Order order;
	
	private Order getOrder()
	{
		return order;
	}
	
	private void setOrder(Order order)
	{
		this.order = order;
	}
}
