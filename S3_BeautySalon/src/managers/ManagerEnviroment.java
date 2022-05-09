package managers;

import OSPABA.*;
import simulation.*;
import agents.*;
import continualAssistants.*;

//meta! id="2"
public class ManagerEnviroment extends Manager
{
	public ManagerEnviroment(int id, Simulation mySim, Agent myAgent)
	{
		super(id, mySim, myAgent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication

		if (petriNet() != null)
		{
			petriNet().clear();
		}
	}

	//meta! sender="SchedulerOfArrivalsOnFoot", id="22", type="Finish"
	public void processFinishSchedulerOfArrivalsOnFoot(MessageForm message)
	{
		message.setCode(Mc.customerArrived);
		message.setAddressee(mySim().findAgent(Id.agentModel));
		notice(message);
	}

	//meta! sender="SchedulerOfArrivalsOnCar", id="24", type="Finish"
	public void processFinishSchedulerOfArrivalsOnCar(MessageForm message)
	{
		message.setCode(Mc.customerArrived);
		message.setAddressee(mySim().findAgent(Id.agentModel));
		notice(message);
	}

	public void processInit(MessageForm message){
		//TODO aktualne nech chodia iba autom
		//prichody peso
		/*MyMessage messageOnFoot = new MyMessage(mySim());
		messageOnFoot.setAddressee(myAgent().findAssistant(Id.schedulerOfArrivalsOnFoot));
		startContinualAssistant(messageOnFoot);*/
		//prichody autom
		MyMessage messageOnCar = new MyMessage(mySim());
		messageOnCar.setAddressee(myAgent().findAssistant(Id.schedulerOfArrivalsOnCar));
		startContinualAssistant(messageOnCar);
	}

	//meta! userInfo="Process messages defined in code", id="0"
	public void processDefault(MessageForm message)
	{
		switch (message.code())
		{
			case Mc.init:{
				processInit(message);
				break;
			}
		}
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	public void init()
	{
	}

	@Override
	public void processMessage(MessageForm message)
	{
		switch (message.code())
		{
		case Mc.finish:
			switch (message.sender().id())
			{
			case Id.schedulerOfArrivalsOnFoot:
				processFinishSchedulerOfArrivalsOnFoot(message);
			break;

			case Id.schedulerOfArrivalsOnCar:
				processFinishSchedulerOfArrivalsOnCar(message);
			break;
			}
		break;

		default:
			processDefault(message);
		break;
		}
	}
	//meta! tag="end"

	@Override
	public AgentEnviroment myAgent()
	{
		return (AgentEnviroment)super.myAgent();
	}

}
