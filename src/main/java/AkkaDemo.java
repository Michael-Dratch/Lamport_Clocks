import akka.actor.typed.ActorSystem;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class AkkaDemo {
    // The only IO we're doing here is console IO, if that fails we can't really recover
    public static void main(String[] args) throws IOException {
        System.out.println("Running Java version");
        var orc = ActorSystem.create(Orchestrator.create(), "java-akka");
        var done = false;
        var console = new BufferedReader(new InputStreamReader(System.in));
        while (!done) {
            var command = console.readLine();
            orc.tell(command);
            if (command.equals("shutdown")) {
                done = true;
                orc.terminate();
            }
        }
    }
}