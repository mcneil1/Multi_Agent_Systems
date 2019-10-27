package mas_ontology_elements;

import jade.content.onto.annotations.AggregateSlot;
import jade.content.onto.annotations.Slot;

public class Storage extends Component
{
	private String size;
	
	@Slot(mandatory = true)
	@AggregateSlot(cardMax =1)
	public String getSize()
	{
		return size;
	}
	
	public void setSize()
	{
		if(this.getSerialNumber() == 3)
		{
			this.size = "64Gb";
		}
		else if (this.getSerialNumber() == 4)
		{
			this.size = "256Gb";
		}
	}
	
}
