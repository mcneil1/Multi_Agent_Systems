package mas_ontology_elements;

import jade.content.Concept;
import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

public class Battery extends Component
{
private String size;
	
	@Slot(mandatory = true)
	@AggregateSlot(cardMax = 1)
	public String getSize()
	{
		return size;
	}
	
	public void setSize(String size)
	{
		this.size = size;
	}
}
