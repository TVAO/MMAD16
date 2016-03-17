package tvao.mmad.itu.tingle.Model;

import java.util.List;
import java.util.UUID;

/**
 * This interface outlines the CRUD operations used in the ThingRepository.
 * The interface is used to decouple the repository implementation from its abstraction.
 */
public interface IRepository {

    void addThing(Thing thing);
    Thing getThing(UUID id);
    List<Thing> getThings();
    void updateThing(Thing thing);;
    boolean removeThing(UUID id);
    int size();
}
