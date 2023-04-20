import akka.actor.typed.ActorSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AkkaDemo {
    // The only IO we're doing here is console IO, if that fails we can't really recover
    public static void main(String[] args) throws IOException {
        System.out.println("Running Java version");
        System.out.println(args[0]);

        int nodeCount = 3;


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
//        orc.terminate();
    }
}