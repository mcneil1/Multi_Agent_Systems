package multi_agent_systems_CW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import mas_ontology_elements.Battery;
import mas_ontology_elements.Component;
import mas_ontology_elements.RAM;
import mas_ontology_elements.Screen;
import mas_ontology_elements.Storage;

public class supplierAgent extends Agent
{
	private AID tickerAgent;
	private AID manufacturer;
	
	private HashMap<String, Integer> itemsForSale = new HashMap<>();
	private int deliverySpeed = 0;
	
	@Override
	protected void setup()
	{
		//add agents to the yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("supplier");
		sd.setName(getLocalName() + "-supplier-agent");
		dfd.addServices(sd);
		try
		{
			DFService.register(this, dfd);
		}
		catch (FIPAException e)
		{
			e.printStackTrace();
		}
		
		//get manufacturer
		DFAgentDescription manufacturerTemplate = new DFAgentDescription();
		ServiceDescription manufacSD = new ServiceDescription();
		manufacSD.setType("manufacturer");
		manufacturerTemplate.addServices(manufacSD);
		try
		{
			DFAgentDescription[] agent = DFService.search(this, manufacturerTemplate);
			for(int i = 0; i<agent.length; i++)
			{
				manufacturer = agent[i].getName();
			}
		}
		catch(FIPAException e)
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
					myAgent.addBehaviour(new GetStock());
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					myAgent.addBehaviour(new EndDayListener(myAgent, cyclicBehaviours));
					
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
	
	
	public class GetStock extends OneShotBehaviour
	{

		Object[] args = getArguments();
		Component[] comp = (Component[]) args[0];
		int[] price = (int[]) args[1];
		int delivery = (int) args[2];
		
		@Override
		public void action() 
		{
			for (int i = 0; i < comp.length; i++)
			{
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.Screen"))
				{
					Screen screen = new Screen();
					screen = (Screen) comp[i];
					itemsForSale.put(screen.getSize(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.Storage"))
				{
					Storage store = new Storage();
					store = (Storage) comp[i];
					itemsForSale.put(store.getSize(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.RAM"))
				{
					RAM ram = new RAM();
					ram = (RAM) comp[i];
					itemsForSale.put(ram.getSize(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.Battery"))
				{
					Battery battery = new Battery();
					battery = (Battery) comp[i];
					itemsForSale.put(battery.getSize(), price[i]);
				}
			}
			deliverySpeed = delivery;
			
		}
		
	}
	
	
	public class EndDayListener extends CyclicBehaviour
	{
		private List<Behaviour> toRemove;
		
		public EndDayListener(Agent a, List<Behaviour> toRemove)
		{
			super(a);
			this.toRemove = toRemove;
		}
		
		@Override
		public void action()
		{
			MessageTemplate mt = MessageTemplate.MatchContent("done");
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null)
			{
				if(msg.getSender().equals(manufacturer))
				{
					//we are finished
					ACLMessage tick = new ACLMessage(ACLMessage.INFORM);
					tick.setContent("done");
					tick.addReceiver(tickerAgent);
					myAgent.send(tick);
					
					//remove behaviours
					for(Behaviour b : toRemove)
					{
						myAgent.removeBehaviour(b);
					}
					myAgent.removeBehaviour(this);
				}
			}
			else
			{
				block();
			}
		}
	}
}
