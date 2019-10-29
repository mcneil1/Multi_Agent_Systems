package multi_agent_systems_CW;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import mas_ontology_elements.Battery;
import mas_ontology_elements.Component;
import mas_ontology_elements.RAM;
import mas_ontology_elements.Screen;
import mas_ontology_elements.Storage;

public class Main 
{
	public static void main(String[] args)
	{
		Profile myProfile = new ProfileImpl();
		Runtime myRuntime = Runtime.instance();
		try
		{
			ContainerController myContainer = myRuntime.createMainContainer(myProfile);
			AgentController rma = myContainer.createNewAgent("rma", "jade.tools.rma.rma", null);
			rma.start();
			
			AgentController ticker = myContainer.createNewAgent("ticker", tickerAgent.class.getCanonicalName(), null);
			ticker.start();
			
			AgentController manufacturer = myContainer.createNewAgent("manufacturer", manufacturerAgent.class.getCanonicalName(), null);
			manufacturer.start();
			
			
			//suppliers
			Storage storage1 = new Storage();
			storage1.setSize("64Gb");
			storage1.setId(3);
			Storage storage2 = new Storage();
			storage2.setSize("256Gb");
			storage2.setId(4);
			
			Screen screen1 = new Screen();
			screen1.setSize("5' Screen");
			screen1.setId(1);
			Screen screen2 = new Screen();
			screen2.setSize("7' Screen");
			screen2.setId(2);
			
			Battery battery1 = new Battery();
			battery1.setSize("2000mAh");
			battery1.setId(5);
			Battery battery2 = new Battery();
			battery2.setSize("3000mAh");
			battery2.setId(6);
			
			RAM ram1 = new RAM();
			ram1.setSize("4Gb");
			ram1.setId(7);
			RAM ram2 = new RAM();
			ram2.setSize("8Gb");
			ram2.setId(8);
				
			Component[] components1 = {screen1, screen2, storage1, storage2, ram1, ram2, battery1, battery2};
			int[] prices1 = {100, 150, 25, 50, 30, 60, 70, 100};
			int deliverySpeed1 = 1;
			
			Object[] supplier1List = 
				{
						components1,
						prices1,
						deliverySpeed1
				};
			
			AgentController supplier1 = myContainer.createNewAgent("supplier1", supplierAgent.class.getCanonicalName(), supplier1List);
			supplier1.start();
			
			
			Component[] components2 = {storage1, storage2, ram1, ram2};
			int[] prices2 = {15, 40, 20, 35};
			int deliverySpeed2 = 4;
			
			Object[] supplier2List =
				{
						components2,
						prices2,
						deliverySpeed2
				};
			AgentController supplier2 = myContainer.createNewAgent("supplier2", supplierAgent.class.getCanonicalName(), supplier2List);
			//supplier2.start();
			
			
			
			//customers 
			AgentController customer1 = myContainer.createNewAgent("customer1", customerAgent.class.getCanonicalName(), null);
			customer1.start();
			
			AgentController customer2 = myContainer.createNewAgent("customer2", customerAgent.class.getCanonicalName(), null);
			customer2.start();

			AgentController customer3 = myContainer.createNewAgent("customer3", customerAgent.class.getCanonicalName(), null);
			customer3.start();
			
		}
		catch (Exception e)
		{
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}
