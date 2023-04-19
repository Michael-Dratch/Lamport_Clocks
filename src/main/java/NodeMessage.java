import akka.actor.typed.ActorRef;

import java.util.List;

public sealed interface NodeMessage {
    public final record Request(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public final record Release(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public final record Ack(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public final record InitializeNodeRefs(List<ActorRef<NodeMessage>> nodeRefs) implements NodeMessage{}
    public final record Shutdown() implements NodeMessage {}
}