import akka.actor.typed.ActorRef;

import java.util.List;

public sealed interface NodeMessage {
    public record Request(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public record Release(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public record Ack(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public record InitializeNodeRefs(List<ActorRef<NodeMessage>> nodeRefs) implements NodeMessage{}
    public record Start() implements NodeMessage {}
    public record Shutdown() implements NodeMessage {}
}