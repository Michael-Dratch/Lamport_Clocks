public sealed interface NodeMessage {
    public final record Request(int time) implements NodeMessage {}
    public final record Release(int time) implements NodeMessage {}

    public final record Ack(int time) implements NodeMessage {}
    public final record Shutdown() implements NodeMessage {}
}