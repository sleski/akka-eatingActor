package pl.najavie.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.actor.Props;
import akka.japi.pf.ReceiveBuilder;
import com.google.common.collect.Maps;
import pl.najavie.utils.Status;
import scala.PartialFunction;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by krzysztof on 1/25/15.
 */
public class AnimalOwner extends AbstractActor {

    private Map<ActorRef,Integer> animalMap = Maps.newConcurrentMap();

    private List<ActorRef> animals = IntStream.range(0,10).mapToObj(element -> getContext().actorOf(Props.create(Animal.class))).collect(Collectors.toList());

    public static Props props() {
        return Props.create(AnimalOwner.class,AnimalOwner::new);
    }

    @Override
    public void preStart() throws Exception {
        animals.parallelStream().forEach(animal -> animalMap.put(animal,0));
        super.preStart();
    }

    public AnimalOwner() {
        receive(start().orElse(feed()).orElse(sleep()));
    }

    private PartialFunction feed() {
        return ReceiveBuilder.matchEquals(Status.HUNGRY, status-> {
            int eatCounter = animalMap.get(sender());
            sender().tell("eat",self());
            animalMap.replace(sender(),++eatCounter);
        }).build();
    }

    private PartialFunction sleep() {
        return ReceiveBuilder.matchEquals(Status.FULL, status -> {
            Integer eatCounter = animalMap.get(sender());
            if(eatCounter == 10) {
                //jeśli zwierzątko jadlo 10 razy, idzie do rzeźni...
                System.out.println("Killing animal: "+sender().path());
                sender().tell(PoisonPill.getInstance(),self());
                animalMap.remove(sender());
            } else {
                sender().tell("sleep",self());
            }

        }).build();
    }

    private PartialFunction start() {
        return ReceiveBuilder
                .matchEquals("start", status-> animalMap.keySet()
                        .parallelStream()
                        .forEach(animal -> animal.tell("start", self())))
                .build();
    }





}
