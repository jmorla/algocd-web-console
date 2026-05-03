package com.algocd.webportal.mql;

/**
 * An abstract base class for tokenizer. This class provides a foundation
 * for implementing specialized common functionalities.
 *
 */
public abstract class BaseReader {


    /**
     * End of input character.
     * Used to denote the last defined character in a source file.
     */
    public final byte EOF = 0x1A;

    /**
     * Buffer containing characters from source file.
     */
    protected final char[] buffer;

    /**
     * Current character being observed
     */
    protected char character;

    /**
     * Character buffer index of character currently being observed.
     */
    protected int position;

    /**
     * Codepoint of character currently being observed.
     */
    protected int codepoint;

    /**
     * Constructor.
     *
     */
    protected BaseReader(char[] array) {
        buffer = array;
        position = -1;

        nextCodePoint();
    }


    /**
     * Retrieves current character observed
     *
     * @return The character value.
     */
    protected char get() {
        return character;
    }


    /**
     * Retrieves the Unicode code point of the current character
     *
     * @return The Unicode code point value.
     */
    protected int getCodePoint() {
        return codepoint;
    }

    /**
     * Advances the reader to the next character and returns it.
     * This method updates the internal state of the reader.
     *
     * @return The next character in the buffer.
     */
    protected char next() {
        nextCodePoint();

        return character;
    }


    /**
     * Reads the next Unicode code point from the buffer, updates
     * the internal state, and returns the code point.
     *
     * @return The Unicode code point read from the buffer.
     */
    protected int nextCodePoint() {
        if (buffer == null || position >= buffer.length - 1) {
            character = (char) EOF;
            codepoint = EOF;
        } else {
            character = buffer[++position];

            // Handle Unicode surrogate pairs
            if (Character.isHighSurrogate(character) && position + 1 < buffer.length) {
                char low = buffer[position + 1];
                if (Character.isLowSurrogate(low)) {
                    codepoint = Character.toCodePoint(character, low);
                    position++; // Consume the low surrogate as well
                } else {
                    codepoint = character;
                }
            } else {
                codepoint = character;
            }
        }

        return codepoint;
    }

    /**
     * Returns current positions
     *
     * @return the cursor current position
     */
    protected int getPosition() {
        return position;
    }

}
