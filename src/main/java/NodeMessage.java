import akka.actor.typed.ActorRef;

public sealed interface NodeMessage {
    public final record Request(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}
    public final record Release(ActorRef<NodeMessage> sender, int time) implements NodeMessage {}

    public final record Ack(int time) implements NodeMessage {}
    public final record Shutdown() implements NodeMessage {}
}