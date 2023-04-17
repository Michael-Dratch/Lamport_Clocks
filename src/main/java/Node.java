import akka.actor.typed.Behavior;
import akka.actor.typed.javadsl.AbstractBehavior;
import akka.actor.typed.javadsl.ActorContext;
import akka.actor.typed.javadsl.Behaviors;
import akka.actor.typed.javadsl.Receive;
import akka.actor.typed.ActorRef;

import java.util.ArrayList;
import java.util.List;

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
    private List<NodeMessage> requestQueue = new ArrayList<>();

    @Override
    public Receive<NodeMessage> createReceive() {
        return newReceiveBuilder()
                .onMessage(NodeMessage.class, this::dispatch)
                .build();
    }

    public Behavior<NodeMessage> dispatch(NodeMessage msg){
        switch(msg) {
            case NodeMessage.Request request:
                synchronizeClock(request.time());
                requestQueue.add(request);

               // getContext().getLog().info("Acknow " + request);
                break;
            case NodeMessage.Release release:
                synchronizeClock(release.time());
                //release behavior
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

    private void synchronizeClock(int messageTime){
        if (clock.getTime() <= messageTime){
            clock.setTime(messageTime + 1);
        }
    }
}
