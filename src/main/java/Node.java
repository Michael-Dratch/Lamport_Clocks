import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorRef;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

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

    @Override
    public Receive<NodeMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(NodeMessage.class, this::dispatch)
                .build();
    }

    public Behavior<NodeMessage> dispatch(NodeMessage msg){
        switch(msg) {
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

    private void handleRequest(NodeMessage.Request request){
        synchronizeClock(request.time());
        requestQueue.add(request);
    }

    private void handleRelease(NodeMessage.Release release) {
        synchronizeClock(release.time());
        this.requestQueue.removeRequestOfSender(release.sender());
    }

    private void synchronizeClock(int messageTime){
        if (clock.getTime() <= messageTime){
            clock.setTime(messageTime + 1);
        }
    }
}
