package simulation;

import OSPABA.*;
import agents.*;

public class MySimulation extends Simulation
{
	public MySimulation()
	{
		init();
	}

	@Override
	public void prepareSimulation()
	{
		super.prepareSimulation();
		// Create global statistcis
	}

	@Override
	public void prepareReplication()
	{
		super.prepareReplication();
		// Reset entities, queues, local statistics, etc...
	}

	@Override
	public void replicationFinished()
	{
		// Collect local statistics into global, update UI, etc...
		super.replicationFinished();
	}

	@Override
	public void simulationFinished()
	{
		// Dysplay simulation results
		super.simulationFinished();
	}

	//meta! userInfo="Generated code: do not modify", tag="begin"
	private void init()
	{
		setAgentModel(new AgentModel(Id.agentModel, this, null));
		setAgentEnviroment(new AgentEnviroment(Id.agentEnviroment, this, agentModel()));
		setAgentBeautySalon(new AgentBeautySalon(Id.agentBeautySalon, this, agentModel()));
		setAgentParking(new AgentParking(Id.agentParking, this, agentModel()));
		setAgentReceptionist(new AgentReceptionist(Id.agentReceptionist, this, agentBeautySalon()));
		setAgentHairstylist(new AgentHairstylist(Id.agentHairstylist, this, agentBeautySalon()));
		setAgentMakeUpArtist(new AgentMakeUpArtist(Id.agentMakeUpArtist, this, agentBeautySalon()));
	}

	private AgentModel _agentModel;

public AgentModel agentModel()
	{ return _agentModel; }

	public void setAgentModel(AgentModel agentModel)
	{_agentModel = agentModel; }

	private AgentEnviroment _agentEnviroment;

public AgentEnviroment agentEnviroment()
	{ return _agentEnviroment; }

	public void setAgentEnviroment(AgentEnviroment agentEnviroment)
	{_agentEnviroment = agentEnviroment; }

	private AgentBeautySalon _agentBeautySalon;

public AgentBeautySalon agentBeautySalon()
	{ return _agentBeautySalon; }

	public void setAgentBeautySalon(AgentBeautySalon agentBeautySalon)
	{_agentBeautySalon = agentBeautySalon; }

	private AgentParking _agentParking;

public AgentParking agentParking()
	{ return _agentParking; }

	public void setAgentParking(AgentParking agentParking)
	{_agentParking = agentParking; }

	private AgentReceptionist _agentReceptionist;

public AgentReceptionist agentReceptionist()
	{ return _agentReceptionist; }

	public void setAgentReceptionist(AgentReceptionist agentReceptionist)
	{_agentReceptionist = agentReceptionist; }

	private AgentHairstylist _agentHairstylist;

public AgentHairstylist agentHairstylist()
	{ return _agentHairstylist; }

	public void setAgentHairstylist(AgentHairstylist agentHairstylist)
	{_agentHairstylist = agentHairstylist; }

	private AgentMakeUpArtist _agentMakeUpArtist;

public AgentMakeUpArtist agentMakeUpArtist()
	{ return _agentMakeUpArtist; }

	public void setAgentMakeUpArtist(AgentMakeUpArtist agentMakeUpArtist)
	{_agentMakeUpArtist = agentMakeUpArtist; }
	//meta! tag="end"
}
