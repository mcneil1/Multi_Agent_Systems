package multi_agent_systems_CW;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

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
			
			AgentController supplier1 = myContainer.createNewAgent("supplier1", supplierAgent.class.getCanonicalName(), null);
			supplier1.start();
			
			//customers 
			AgentController customer1 = myContainer.createNewAgent("customer1", customerAgent.class.getCanonicalName(), null);
			customer1.start();
			
			AgentController customer2 = myContainer.createNewAgent("customer2", customerAgent.class.getCanonicalName(), null);
			customer2.start();

			AgentController customer3 = myContainer.createNewAgent("customer3", customerAgent.class.getCanonicalName(), null);
			customer3.start();
			
			/*
			 * add agents below
			 */
		}
		catch (Exception e)
		{
			System.out.println("Exception starting agent: " + e.toString());
		}
	}
}
