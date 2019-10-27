package mas_ontology_elements;

import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

public class Screen extends Component
{
	private String size;
	
	@Slot(mandatory = true)
	@AggregateSlot(cardMax = 1)
	public String getSize()
	{
		return size;
	}
	
	public void setSize()
	{
		if(this.getSerialNumber() == 1)
		{
			this.size = "5' Screen";
		}
		else if (this.getSerialNumber() == 2)
		{
			this.size = "7' Screen";
		}
	}
	
	
}
