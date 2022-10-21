package org.miditranser.data;

/**
 * mark an object that can be added to mider code
 */
public interface Addable {
    String generateMiderCode(CalculateDurationConfiguration cdc);
}
