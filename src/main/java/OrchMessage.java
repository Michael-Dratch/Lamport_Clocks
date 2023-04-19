import akka.actor.typed.ActorRef;


public interface OrchMessage {
    public record start(int nodeCount) implements OrchMessage {}
    public record shutDown() implements OrchMessage {}
}
