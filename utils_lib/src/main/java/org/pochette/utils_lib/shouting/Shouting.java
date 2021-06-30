package org.pochette.utils_lib.shouting;

/**
 * Shouting is the interface to exchange Shouts
 * <p>
 * The upstairs class must implement Shouting, so the downstairs class can call it
 * The reference to the upstairs class must be communicated to the downstairs class.
 * </p>
 */

@SuppressWarnings("unused")
public interface Shouting {
	void shoutUp(Shout tShoutToCeiling);
}
