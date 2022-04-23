package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="2"
public class AgentEnviroment extends Agent
{
	public AgentEnviroment(int id, Simulation mySim, Agent parent)
	{
		super(id, mySim, parent);
		init();
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Setup component for the next replication
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		new ManagerEnviroment(Id.managerEnviroment, mySim(), this);
		new SchedulerOfArrivalsOnFoot(Id.schedulerOfArrivalsOnFoot, mySim(), this);
		new SchedulerOfArrivalsOnCar(Id.schedulerOfArrivalsOnCar, mySim(), this);
	}
	//meta! tag="end"
}
