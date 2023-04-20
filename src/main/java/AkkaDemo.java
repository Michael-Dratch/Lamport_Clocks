import akka.actor.typed.ActorSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AkkaDemo {
    public static void main(String[] args) throws IOException {

        int nodeCount = Integer.valueOf(args[0]);


        var orc = ActorSystem.create(Orchestrator.create(), "java-akka");
        var done = false;
        var console = new BufferedReader(new InputStreamReader(System.in));

        orc.tell(new OrchMessage.Start(nodeCount));

        while (!done) {
            var command = console.readLine();
            if (command.length()==0) {
                done = true;
                terminateSystem(orc);
            }
        }
    }

    private static void terminateSystem(ActorSystem<OrchMessage> orc) {
        orc.tell(new OrchMessage.ShutDown());
    }
}