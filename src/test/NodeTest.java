import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

public class NodeTest {
    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Before
    public void setUp() {
        ActorRef<NodeMessage> node = testKit.spawn(Node.create());
    }
}



