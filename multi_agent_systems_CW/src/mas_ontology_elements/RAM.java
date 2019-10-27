package mas_ontology_elements;

import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

public class RAM extends Component
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
		if(this.getSerialNumber() == 4)
		{
			this.size = "4Gb";
		}
		else if (this.getSerialNumber() == 5)
		{
			this.size = "8Gb";
		}
	}
}
