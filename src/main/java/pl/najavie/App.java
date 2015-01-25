package pl.najavie;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import pl.najavie.actors.AnimalOwner;


/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        ActorSystem system = ActorSystem.create("system");
        ActorRef animalOwner = system.actorOf(Props.create(AnimalOwner.class),"animalOwner");
        animalOwner.tell("start", ActorRef.noSender());
    }
}
