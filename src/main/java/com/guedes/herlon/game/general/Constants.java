package com.guedes.herlon.game.general;

/**
 * Abstract class to encapsulate some constants used on software execution.
 * @author herlon-guedes
 * @since 08/10/2020
 */
public abstract class Constants {
    public static final String FILE_LINE_ELEMENT_SPLITTER = "\t";
    public static final String SPARE_CHARACTER = "/";
    public static final String FAULT_CHARACTER = "F";
    public static final String STRIKE_CHARACTER = "X";
    public static final int MAX_NUMBER_OF_PINS = 10;
    public static final long FAULT_NUMBER_KNOCKED_DOWN_PINS = 0L;
    public static final int MAX_NUMBER_OF_FRAMES = 10;
    public static final int NON_LAST_FRAME_MAX_NUMBER_THROWS = 2;
    public static final int LAST_FRAME_MAX_NUMBER_THROWS = 3;
}
