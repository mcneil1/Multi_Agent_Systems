package multi_agent_systems_CW;

import java.util.ArrayList;

import jade.content.Concept;
import jade.content.ContentElement;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import mas_ontology.ECommerceOntology;
import mas_ontology_elements.Order;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;


public class manufacturerAgent extends Agent
{
	private Codec codec = new SLCodec();
	private Ontology ontology = ECommerceOntology.getInstance();
	
	private ArrayList<AID> customers = new ArrayList<>();
	private ArrayList<AID> suppliers = new ArrayList<>();
	private ArrayList<Order> openOrders = new ArrayList<>();
	private AID tickerAgent;
	private int day = 0;
	
	@Override 
	protected void setup()
	{
		getContentManager().registerLanguage(codec);
		getContentManager().registerOntology(ontology);
		
		//add agent to yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("manufacturer");
		sd.setName(getLocalName() + "-manufacturer-agent");
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
					day++;
					/*
					 * Add customer behaviours here
					 */
					
					//spawn new sequential for day's activity
					SequentialBehaviour dailyActivity = new SequentialBehaviour();
					//sub-behaviours will execute in the order they are added
					dailyActivity.addSubBehaviour(new FindAgents(myAgent));
					dailyActivity.addSubBehaviour(new AcceptOrder(myAgent));
					dailyActivity.addSubBehaviour(new EndDay(myAgent));
					
					myAgent.addBehaviour(dailyActivity);
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
	
	
	public class FindAgents extends OneShotBehaviour
	{
		public FindAgents(Agent a)
		{
			super(a);
		}
		
		@Override 
		public void action()
		{
			DFAgentDescription customerTemplate = new DFAgentDescription();
			ServiceDescription csd = new ServiceDescription();
			csd.setType("customer");
			customerTemplate.addServices(csd);
			
			DFAgentDescription supplierTemplate = new DFAgentDescription();
			ServiceDescription ssd = new ServiceDescription();
			ssd.setType("supplier");
			supplierTemplate.addServices(ssd);
			
			try
			{
				customers.clear();
				DFAgentDescription[] custAgent = DFService.search(myAgent, customerTemplate);
				for(int i = 0; i<custAgent.length; i++)
				{
					customers.add(custAgent[i].getName());
				}
				
				suppliers.clear();
				DFAgentDescription[] supplierAgent = DFService.search(myAgent, supplierTemplate);
				for(int i = 0; i<supplierAgent.length; i++)
				{
					suppliers.add(supplierAgent[i].getName());
				}
			}
			catch (FIPAException fe)
			{
				fe.printStackTrace();
			}
		}
	}
	
	/*
	 * The AcceptOrder behaviour receives all order proposals from the customerAgents
	 * it decides which to accept (based on highest price) and adds that order to the 
	 * openOrder list. each customer is either sent a REFUSE or ACCEPT_PROPOSAL reply.
	 */
	public class AcceptOrder extends Behaviour
	{

		private int numOrders;
		private Order bestOrder;
		private ArrayList<Order> customersOrders = new ArrayList<>();
		
		public AcceptOrder(Agent a)
		{
			super(a);
		}
		
		@Override
		public void action() 
		{
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
			ACLMessage order = myAgent.receive(mt);
			if(order != null)
			{
				numOrders++;
				try
				{
					ContentElement ce = null;
					
					ce = getContentManager().extractContent(order);
					if(ce instanceof Action)
					{
						Concept action = ((Action)ce).getAction();
						if(action instanceof Order)
						{
							Order custOrder = (Order)action;
							customersOrders.add(custOrder);
							if(bestOrder == null)
							{
								bestOrder = custOrder;
							}
							else if(custOrder.getPrice() > bestOrder.getPrice())
							{
								bestOrder = custOrder;
							}
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
				block();
			}
			
			//Send replies to each customer
			if(numOrders == customers.size())
			{
				openOrders.add(bestOrder);
				System.out.println("Manufacturer has accepted an order from " + bestOrder.getCustomer());
				System.out.print("");
				
				for (int i = 0; i < customersOrders.size(); i++)
				{
					if(customersOrders.get(i) == bestOrder)
					{
						ACLMessage reply = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
						reply.addReceiver(customersOrders.get(i).getCustomer());
						reply.setConversationId("order-reply");
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName());
						
						Order ord = customersOrders.get(i);
						
						Action sendReply = new Action();
						sendReply.setAction(ord);
						sendReply.setActor(customersOrders.get(i).getCustomer());
						
						try
						{
							getContentManager().fillContent(reply, sendReply);
							send(reply);
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
						ACLMessage reply = new ACLMessage(ACLMessage.REFUSE);
						reply.addReceiver(customersOrders.get(i).getCustomer());
						reply.setConversationId("order-reply");
						reply.setLanguage(codec.getName());
						reply.setOntology(ontology.getName());
						
						Order ord = customersOrders.get(i);
						
						Action sendReply = new Action();
						sendReply.setAction(ord);
						sendReply.setActor(customersOrders.get(i).getCustomer());
						
						try
						{
							getContentManager().fillContent(reply, sendReply);
							send(reply);
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
			}
		}

		@Override
		public boolean done() 
		{
			return numOrders == customers.size();
		}
		
	}
	
	
	public class EndDay extends OneShotBehaviour
	{
		public EndDay(Agent a)
		{
			super(a);
		}
		
		@Override
		public void action()
		{
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.addReceiver(tickerAgent);
			msg.setContent("done");
			myAgent.send(msg);
			
			
			//Send messages to all suppliers and customers
			ACLMessage customerDone = new ACLMessage(ACLMessage.INFORM);
			customerDone.setContent("done");
			for(AID customer : customers)
			{
				customerDone.addReceiver(customer);
			}
			myAgent.send(customerDone);
			
			ACLMessage supplierDone = new ACLMessage(ACLMessage.INFORM);
			supplierDone.setContent("done");
			for(AID supplier : suppliers)
			{
				supplierDone.addReceiver(supplier);
			}
			myAgent.send(supplierDone);
			
		}
	}
}
