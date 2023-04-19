import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorRef;

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

    private NodeMessage.Request lastRequest = null;

    @Override
    public Receive<NodeMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(NodeMessage.class, this::dispatch)
                .build();
    }

    public Behavior<NodeMessage> dispatch(NodeMessage msg){
        switch(msg) {
            case NodeMessage.InitializeNodeRefs init:
                initializeNodeRefs(init.nodeRefs());
                break;
            case NodeMessage.Start start:
                start();
                break;
            case NodeMessage.Request request:
                handleRequest(request);
                break;
            case NodeMessage.Release release:
                handleRelease(release);
                break;
            case NodeMessage.Ack ack:
                handleAcknowledge(ack);
                break;
            case NodeMessage.Shutdown shutdown:
                return Behaviors.stopped();
        }
        return this;
    }

    private void initializeNodeRefs(List<ActorRef<NodeMessage>> nodeRefs) {
        for (ActorRef<NodeMessage> ref : nodeRefs){
            this.lastMsgTimeLog.put(ref, 0);
        }
    }

    private void start(){
        this.lastRequest = new NodeMessage.Request(this.getContext().getSelf(), this.clock.getTime());
        notifyAllNodes(this.lastRequest);
        this.clock.increment();
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
        if (isMyTurnToAcquireResource()){
            acquireResource();
            releaseResource();
        }
    }

    private void handleAcknowledge(NodeMessage.Ack ack){
        synchronizeClock(ack.time());
        this.lastMsgTimeLog.put(ack.sender(), ack.time());
        if (isMyTurnToAcquireResource()){
            acquireResource();
            releaseResource();
        }
    }

    public void notifyAllNodes(NodeMessage msg){
        this.lastMsgTimeLog.forEach((key, value) -> {
            key.tell(msg);
        });
    }

    private void releaseResource(){
        notifyAllNodes(new NodeMessage.Release(this.getContext().getSelf(), this.clock.getTime()));
        this.clock.increment();
        requestResource();
    }

    private void requestResource(){
        this.lastRequest = new NodeMessage.Request(this.getContext().getSelf(), this.clock.getTime());
        notifyAllNodes(this.lastRequest);
        this.clock.increment();

    }

    private void acquireResource(){
        this.clock.increment();
    }

    private void synchronizeClock(int messageTime){
        if (clock.getTime() <= messageTime){
            clock.setTime(messageTime + 1);
        }
    }
    private boolean isMyTurnToAcquireResource(){
        return isMyRequestFirst() && haveHeardFromAllNodesSinceLastRequest();
    }
    private boolean isMyRequestFirst(){
        return this.requestQueue.first().sender() == this.getContext().getSelf();
    }

    private boolean haveHeardFromAllNodesSinceLastRequest() {
        for (Integer time : this.lastMsgTimeLog.values()){
            if (time <= this.lastRequest.time()) {
                return false;
            }
        }
        return true;
    }
}
