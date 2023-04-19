import akka.actor.testkit.typed.javadsl.TestKitJunitResource;
import akka.actor.testkit.typed.javadsl.TestProbe;
import akka.actor.typed.ActorRef;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.Assert.*;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class RequestQueueTest {


    TestProbe<NodeMessage> probe;

    @ClassRule
    public static final TestKitJunitResource testKit = new TestKitJunitResource();

    @Before
    public void setUp(){
        this.probe = testKit.createTestProbe();
    }


    @Test
    public void testIsEmpty(){
    }


