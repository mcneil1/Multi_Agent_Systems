package multi_agent_systems_CW;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class customerAgent extends Agent
{
	private AID tickerAgent;
	private AID manufacturer;
	
	@Override
	protected void setup()
	{
		//add agents to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("customer");
		sd.setName(getLocalName() + "-customer-agent");
		dfd.addServices(sd);
		try
		{
			DFService.register(this, dfd);
		}
		catch (FIPAException e)
		{
			e.printStackTrace();
		}
		
		addBehaviour(new TickerWaiter(this));
		
	}
	
	protected void takedown()
	{
		//Deregister from yellow pages
		try
		{
			DFService.deregister(this);
		}
		catch (FIPAException e)
		{
			e.printStackTrace();
		}
	}
	
	public class TickerWaiter extends CyclicBehaviour
	{
		public TickerWaiter(Agent a)
		{
			super(a);
		}
		
		@Override
		public void action()
		{
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchContent("new day"),
					MessageTemplate.MatchContent("terminate"));
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null)
			{
				if(tickerAgent == null)
				{
					tickerAgent = msg.getSender();
				}
				if(msg.getContent().equals("new day"))
				{
					/*
					 * Add customer behaviours here
					 */
				}
				else
				{
					//termination message to end simulation
					myAgent.doDelete();
				}
			}
			else
			{
				block();
			}
		}
	}
	
	/*
	 * Implement behaviours below
	 */
}
