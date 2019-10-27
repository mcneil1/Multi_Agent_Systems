package mas_ontology_elements;

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
	
	public void setSize()
	{
		if(this.getSerialNumber() == 6)
		{
			this.size = "2000mAh";
		}
		else if (this.getSerialNumber() == 7)
		{
			this.size = "3000mAh";
		}
	}
}
