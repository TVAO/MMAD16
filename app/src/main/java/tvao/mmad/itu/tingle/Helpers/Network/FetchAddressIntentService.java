package tvao.mmad.itu.tingle.Helpers.Network;

/**
 * Lookup service used to get the address of the user's location to know where an item was registered.
 * A street address is fetched by using the Geocoder class in the Android framework location API
 * to convert geographic coordinates (latitude/longitude) to corresponding addresses , vice versa (reverse geocoding).
 *
 * The intent service handles an intent asynchronously on a worker thread, and stops itself when it runs out of work.
 * The intent extras provide the data needed by the service, including a Location object for conversion to an address,
 * and a ResultReceiver object to handle the results of the address lookup.
 * The service uses a Geocoder to fetch the address for the location, and sends the results to the ResultReceiver.
 */
public class FetchAddressIntentService {



}
