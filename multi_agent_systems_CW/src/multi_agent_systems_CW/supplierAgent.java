package multi_agent_systems_CW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jade.content.ContentElement;
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
import mas_ontology.ECommerceOntology;
import mas_ontology_elements.Battery;
import mas_ontology_elements.Component;
import mas_ontology_elements.Owns;
import mas_ontology_elements.RAM;
import mas_ontology_elements.Screen;
import mas_ontology_elements.Storage;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;

public class supplierAgent extends Agent
{
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	
	private AID tickerAgent;
	private AID manufacturer;
	
	private HashMap<Integer, Integer> itemsForSale = new HashMap<>();
	private int deliverySpeed = 0;
	
	@Override
	protected void setup()
	{
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
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
					CyclicBehaviour ol = new OwnsListener();
					myAgent.addBehaviour(ol);
					ArrayList<Behaviour> cyclicBehaviours = new ArrayList<>();
					cyclicBehaviours.add(ol);
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
					itemsForSale.put(screen.getId(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.Storage"))
				{
					Storage store = new Storage();
					store = (Storage) comp[i];
					itemsForSale.put(store.getId(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.RAM"))
				{
					RAM ram = new RAM();
					ram = (RAM) comp[i];
					itemsForSale.put(ram.getId(), price[i]);
				}
				if(comp[i].getClass().toString().equals("class mas_ontology_elements.Battery"))
				{
					Battery battery = new Battery();
					battery = (Battery) comp[i];
					itemsForSale.put(battery.getId(), price[i]);
				}
			}
			deliverySpeed = delivery;
			

				
		}
		
	}
	
	
	public class OwnsListener extends CyclicBehaviour
	{

		@Override
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.QUERY_IF);
			ACLMessage msg = myAgent.receive(mt);
			if(msg != null)
			{
				try
				{
					ContentElement ce  = null;
					
					ce = getContentManager().extractContent(msg);
					if (ce instanceof Owns)
					{
						Owns owns = (Owns) ce;
						Component comp = owns.getComponent();
						
						ACLMessage reply = new ACLMessage(ACLMessage.INFORM);
						reply.addReceiver(msg.getSender());
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName());
						
						if(itemsForSale.containsKey(comp.getId()))
						{
							owns.setPrice(itemsForSale.get(comp.getId()));
							owns.setDeliverySpeed(deliverySpeed);
							getContentManager().fillContent(reply, owns);
							send(reply);
							
							System.out.println(owns.getDeliverySpeed());
						}
						else
						{
							send(reply);
						}
					}
				}
				catch (CodecException ce) {
					ce.printStackTrace();
				}
				catch (OntologyException oe) {
					oe.printStackTrace();
				}
			}
			else
			{
				block();
			}
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
