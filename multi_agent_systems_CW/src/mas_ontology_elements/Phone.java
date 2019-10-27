package mas_ontology_elements;

import jade.content.Concept;
import jade.content.onto.annotations.Slot;

public class Phone implements Concept
{
	private String type;
	private Component screen;
	private Component storage;
	private Component ram;
	private Component battery;
	
	
	@Slot(mandatory = true)
	public String getType()
	{
		return type;
	}
	
	public void setType(String type)
	{
		this.type = type;
	}
	
	
	
	@Slot(mandatory = true)
	public Component getScreen()
	{
		return screen;
	}
	
	public void setScreen()
	{
		if(this.type == "Small phone")
		{
			this.screen.setSerialNumber(1);
		}
		else if(this.type == "Phablet")
		{
			this.screen.setSerialNumber(2);
		}
	}
	
	
	
	@Slot(mandatory = true)
	public Component getStorage()
	{
		return storage;
	}
	
	public void setStorage(int serialNumber)
	{
		if(serialNumber == 3)
		{
			this.storage.setSerialNumber(serialNumber);
		}
		else if(serialNumber == 4)
		{
			this.storage.setSerialNumber(serialNumber);
		}
	}
	
	
	
	
	@Slot(mandatory = true)
	public Component getRam()
	{
		return ram;
	}
	
	public void setRam(int serialNumber)
	{
		if(serialNumber == 5)
		{
			this.ram.setSerialNumber(serialNumber);
		}
		else if(serialNumber == 6)
		{
			this.ram.setSerialNumber(serialNumber);
		}
	}
	
	
	
	
	@Slot(mandatory = true)
	public Component getBattery()
	{
		return battery;
	}
	
	public void setBattery()
	{
		if(this.type == "Small phone")
		{
			this.battery.setSerialNumber(7);
		}
		else if(this.type == "Phablet")
		{
			this.battery.setSerialNumber(8);
		}
	}
}
