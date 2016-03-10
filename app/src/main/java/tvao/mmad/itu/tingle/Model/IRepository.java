package tvao.mmad.itu.tingle.Model;

import java.util.List;

/**
 * This interface outlines the CRUD operations used in the ThingRepository.
 * The interface is used to decouple the repository implementation from its abstraction.
 */
public interface IRepository {

    Thing get(int i);
    List<Thing> getThings();
    void addThing(Thing thing);
    void removeThing(int position);
    int size();
}
