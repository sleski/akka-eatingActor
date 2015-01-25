package pl.najavie.actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.japi.pf.FI;
import akka.japi.pf.ReceiveBuilder;
import pl.najavie.utils.Status;
import scala.PartialFunction;
import scala.runtime.BoxedUnit;


/**
 * Created by krzysztof on 1/25/15.
 */
public class Animal extends AbstractActor {

    private int eatCounter = 0;
    private Status currentStatus = Status.HUNGRY;


    private PartialFunction<Object,BoxedUnit> full;
    private PartialFunction<Object,BoxedUnit> hungry;
    private FI.UnitApply<Object> otherAction = o -> System.out.println("NOOOOO !! !! !! I'm not going to "+o.toString());

    public static Props props() {
        return Props.create(Animal.class,Animal::new);
    }

    public Animal() {

        full = ReceiveBuilder.matchEquals("sleep",message -> {
            System.out.println(self().path() + " I'm going to sleep");
            eatCounter--;
            if(eatCounter == 0) {
                context().become(hungry);
                currentStatus = Status.HUNGRY;
            }
            sender().tell(currentStatus,self());
        }).matchAny(otherAction).build();

        hungry = ReceiveBuilder.matchEquals("eat",message -> {
            System.out.println(self().path() + " I'm going to eat! ");
            eatCounter++;
            if (eatCounter == 5) {
                context().become(full);
                currentStatus = Status.FULL;
            }
            sender().tell(currentStatus,self());
        }).matchAny(otherAction).build();

        receive(ReceiveBuilder.matchEquals("start", message -> {
            System.out.println("Starting animal: "+self().path());
            context().become(hungry);
            currentStatus = Status.HUNGRY;
            sender().tell(currentStatus, self());

        }).build());
    }
}
