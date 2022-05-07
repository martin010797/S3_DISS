package continualAssistants;

import OSPABA.CommonAgent;
import OSPABA.MessageForm;
import OSPABA.Process;
import OSPABA.Simulation;
import agents.AgentBeautySalon;
import simulation.Mc;

public class ProcessClosing extends Process {

    public ProcessClosing(int id, Simulation mySim, CommonAgent myAgent) {
        super(id, mySim, myAgent);
    }

    public void processStart(MessageForm message){
        //holduje do 17:00 (28800)
        double lengthOfWaiting = 28800 - mySim().currentTime();
        message.setCode(Mc.closingProcessFinished);
        hold(lengthOfWaiting,message);
    }

    public void processClosingProcessFinished(MessageForm message){
        assistantFinished(message);
    }

    @Override
    public void processMessage(MessageForm message) {
        switch (message.code())
        {
            case Mc.start:
                processStart(message);
                break;

            case Mc.closingProcessFinished:
                processClosingProcessFinished(message);
                break;
        }
    }

    @Override
    public AgentBeautySalon myAgent() {
        return (AgentBeautySalon)super.myAgent();
    }
}
