package agents;

import OSPABA.*;
import simulation.*;
import managers.*;
import continualAssistants.*;

//meta! id="36"
public class AgentMakeUpArtist extends Agent
{
	public AgentMakeUpArtist(int id, Simulation mySim, Agent parent)
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
		new ManagerMakeUpArtist(Id.managerMakeUpArtist, mySim(), this);
		new ProcessMakeUp(Id.processMakeUp, mySim(), this);
		new ProcessSkinCleaning(Id.processSkinCleaning, mySim(), this);
		addOwnMessage(Mc.makeUp);
		addOwnMessage(Mc.skinCleaning);
	}
	//meta! tag="end"
}
