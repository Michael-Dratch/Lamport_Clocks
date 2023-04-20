public interface OrchMessage {
    public record Start(int nodeCount) implements OrchMessage {}
    public record ShutDown() implements OrchMessage {}

    public record NodeTerminated() implements OrchMessage {}
    public record ShutDownComplete() implements OrchMessage {}
}
