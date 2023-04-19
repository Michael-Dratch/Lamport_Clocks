import akka.actor.typed.ActorRef;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

public class RequestQueue extends TreeSet<NodeMessage.Request> {
    public RequestQueue(){
        super(new Comparator<NodeMessage.Request>() {
            @Override
            public int compare(NodeMessage.Request r1, NodeMessage.Request r2) {
                if (isRequestTimesEqual(r1, r2)){
                    return r1.sender().compareTo(r2.sender());
                    }
                else {
                    return Integer.compare(r1.time(), r2.time());
                }
            }
        });
    }

    private static boolean isRequestTimesEqual(NodeMessage.Request r1, NodeMessage.Request r2) {
        return Integer.compare(r1.time(), r2.time()) == 0;
    }

    public void removeRequestOfSender(ActorRef<NodeMessage> sender){
        Iterator<NodeMessage.Request> iterator = this.iterator();
        while (iterator.hasNext()){
            NodeMessage.Request request = iterator.next();
            if (request.sender() == sender){
                this.remove(request);
                break;
            }
        }
    }
}
