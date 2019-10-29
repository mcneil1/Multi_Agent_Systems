package multi_agent_systems_CW;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
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
import mas_ontology_elements.Order;
import mas_ontology_elements.Phone;
import mas_ontology_elements.RAM;
import mas_ontology_elements.Screen;
import mas_ontology_elements.Storage;


public class customerAgent extends Agent
{
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	
	private AID tickerAgent;
	private AID manufacturer;
	private ArrayList<Order> openOrders = new ArrayList<>();
	private int day = 0;
	
	@Override
	protected void setup()
	{
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
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
					day++;
					/*
					 * 
					 * 
					 * Add customer behaviours here
					 * 
					 * 
					 */
					myAgent.addBehaviour(new SendOrder());
					myAgent.addBehaviour(new AcceptRefuseListener());
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
	
	/*
	 * 
	 * 
	 * Implement behaviours below
	 * 
	 * 
	 */
	
	public class SendOrder extends OneShotBehaviour
	{
		private double phoneType = Math.random();
		private double ramType = Math.random();
		private double storageType = Math.random();
		private Storage storage = new Storage();
		private Screen screen = new Screen();
		private Battery battery = new Battery();
		private RAM ram = new RAM();
		
		public void action()
		{
			//make phone to be purchased
			Phone phone = new Phone();
			if(phoneType < 0.5)
			{
				phone.setType("Small phone");
				
				screen.setSize("5' Screen");
				screen.setId(1);
				phone.setScreen(screen);
				
				battery.setSize("2000mAh");
				battery.setId(5);
				phone.setBattery(battery);
			}
			else
			{
				phone.setType("Phablet");
				
				screen.setSize("7' Screen");
				screen.setId(2);
				phone.setScreen(screen);
				
				battery.setSize("3000mAh");
				battery.setId(6);
				phone.setBattery(battery);
			}
			
			if(storageType < 0.5)
			{
				storage.setSize("64Gb");
				storage.setId(3);
				phone.setStorage(storage);
			}
			else
			{
				storage.setSize("256Gb");
				storage.setId(4);
				phone.setStorage(storage);
			}
			
			if(ramType < 0.5)
			{
				ram.setSize("4Gb");
				ram.setId(7);
				phone.setRam(ram);
			}
			else
			{
				ram.setSize("8Gb");
				ram.setId(8);
				phone.setRam(ram);
			}
			
			//Prepare message that will include order
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.addReceiver(manufacturer);
			msg.setLanguage(codec.getName());
			msg.setOntology(ontology.getName());
			
			//produce int variables for order content randomly
			int dueDate = (int) Math.floor(1 + 10 * Math.random());
			int quantity = (int) Math.floor(1 + 50 * Math.random());
			int price = (int) Math.floor(100 + 500 * Math.random());
			int lateFee = (int) (Math.floor(1 + 50 * Math.random()) * quantity) ;
			
			Order order = new Order();
			order.setCustomer(myAgent.getAID());
			order.setPhone(phone);
			order.setDueDate(dueDate + day);
			order.setLateFee(lateFee);
			order.setPrice(price);
			order.setQuantity(quantity);
			
			Action myOrder = new Action();
			myOrder.setAction(order);
			myOrder.setActor(manufacturer);	
			
			try
			{
				getContentManager().fillContent(msg, myOrder);
				send(msg);
			}
			catch (CodecException ce) 
			{
				ce.printStackTrace();
			}
			catch (OntologyException oe) 
			{
				oe.printStackTrace();
			} 
			
		}
	}
	
	
	public class AcceptRefuseListener extends OneShotBehaviour
	{
		@Override
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchConversationId("order-reply");
			ACLMessage reply = myAgent.receive(mt);
			if(reply != null)
			{
				if(reply.getPerformative() == ACLMessage.ACCEPT_PROPOSAL)
				{
					try
					{
						ContentElement ce = null;
						
						ce = getContentManager().extractContent(reply);
						if(ce instanceof Action)
						{
							Concept action = ((Action)ce).getAction();
							if(action instanceof Order)
							{
								Order order = (Order)action;
								openOrders.add(order);
								
							}
						}
					}
					catch (CodecException ce) 
					{
						ce.printStackTrace();
					}
					catch (OntologyException oe) 
					{
						oe.printStackTrace();
					}
				}
				else
				{
					return;
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
