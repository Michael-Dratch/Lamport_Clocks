import akka.actor.typed.ActorRef;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;

import java.util.ArrayList;

public class Orchestrator extends AbstractBehavior<OrchMessage> {
    public static Behavior<OrchMessage> create() {
        return Behaviors.setup(context -> new Orchestrator(context));
    }

    private Orchestrator(ActorContext ctxt) {
        super(ctxt);
        countTerminatedNodes = 0;
    }

    private ArrayList<ActorRef<NodeMessage>> nodeRefs;
    @Override
    public Receive<OrchMessage> createReceive() {
      return newReceiveBuilder()
          .onMessage(OrchMessage.class, this::dispatch)
          .build();
    }


    public Behavior<OrchMessage> dispatch(OrchMessage msg) {
        switch (msg) {
            case OrchMessage.Start start:
                handleStart(start);
                break;
            case OrchMessage.ShutDown shutDown:
                handleShutdown();
                break;
            case OrchMessage.NodeTerminated nodeTerminated:
                handleTerminatedNodes();
                break;
            case OrchMessage.ShutDownComplete complete:
                return Behaviors.stopped();
            default:
                break;
        }
        return this;
    }

    private int countTerminatedNodes;

    private void handleStart(OrchMessage.Start start) {
        getContext().getLog().info("[Orchestrator] spawning nodes ");
        this.nodeRefs = createNodes(start.nodeCount());
        notifyAllNodes(new NodeMessage.InitializeNodeRefs(this.nodeRefs));
        getContext().getLog().info("[Orchestrator] starting nodes ");
        notifyAllNodes(new NodeMessage.Start());
    }

    private ArrayList<ActorRef<NodeMessage>> createNodes(int nodeCount) {
        ArrayList<ActorRef<NodeMessage>> nodeRefs = new ArrayList<>();
        for (int count = 0; count < nodeCount; count++){
            var nodeRef = this.getContext().spawn(Node.create(), "NODE_" + count);
            nodeRefs.add(nodeRef);
            this.getContext().watchWith(nodeRef, new OrchMessage.NodeTerminated());
        }
        return nodeRefs;
    }

    private void handleShutdown(){
        notifyAllNodes(new NodeMessage.Shutdown());
    }

    private void handleTerminatedNodes(){
        countTerminatedNodes++;
        if (countTerminatedNodes == this.nodeRefs.size()){
            this.getContext().getLog().info("Shutdown Complete");
            this.getContext().getSelf().tell(new OrchMessage.ShutDownComplete());
        }
    }
    private void notifyAllNodes(NodeMessage msg){
        for (ActorRef<NodeMessage> node : this.nodeRefs){
            node.tell(msg);
        }
    }
}