import akka.actor.Actor;
import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorRef;
import scala.Int;

import java.util.*;

public class Node extends AbstractBehavior<NodeMessage> {

    public static Behavior<NodeMessage> create() {
        return Behaviors.setup(context -> {
            return new Node(context);
        });
    }

    public void setSystemNodes(List<ActorRef<NodeMessage>> nodes){
        this.systemNodes = nodes;
    }

    private Node(ActorContext context) {
        super(context);
        clock = new LamportClock();
    }

    private LamportClock clock;
    private List<ActorRef<NodeMessage>> systemNodes = null;
    private RequestQueue requestQueue = new RequestQueue();

    private HashMap<ActorRef<NodeMessage>, Integer> lastMsgTimeLog = new HashMap<>();

    @Override
    public Receive<NodeMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(NodeMessage.class, this::dispatch)
                .build();
    }

    public Behavior<NodeMessage> dispatch(NodeMessage msg){
        switch(msg) {
            case NodeMessage.InitializeNodeRefs init:
                initializeNodeRefs();
                break;
            case NodeMessage.Request request:
                handleRequest(request);
                break;
            case NodeMessage.Release release:
                handleRelease(release);
                break;
            case NodeMessage.Ack ack:
                synchronizeClock(ack.time());
                //ack behavior
                break;
            case NodeMessage.Shutdown shutdown:
                // perform all necessary shutdown behavior
                return Behaviors.stopped();
        }
        return this;
    }

    private void initializeNodeRefs(List<ActorRef<NodeMessage>> nodeRefs) {
        for (ActorRef<NodeMessage> ref : nodeRefs){
            this.lastMsgTimeLog.put(ref, 0);
        }
    }

    private void handleRequest(NodeMessage.Request request){
        synchronizeClock(request.time());
        requestQueue.add(request);
        this.lastMsgTimeLog.put(request.sender(), request.time());
    }

    private void handleRelease(NodeMessage.Release release) {
        synchronizeClock(release.time());
        this.requestQueue.removeRequestOfSender(release.sender());
        this.lastMsgTimeLog.put(release.sender(), release.time());
    }

    private void handleAcknowledge(NodeMessage.Ack ack){
        synchronizeClock(ack.time());
        this.lastMsgTimeLog.put(ack.sender(), ack.time());
    }

    private void synchronizeClock(int messageTime){
        if (clock.getTime() <= messageTime){
            clock.setTime(messageTime + 1);
        }
    }
}
