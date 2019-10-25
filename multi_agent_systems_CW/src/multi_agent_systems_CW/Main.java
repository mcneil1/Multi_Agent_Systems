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
