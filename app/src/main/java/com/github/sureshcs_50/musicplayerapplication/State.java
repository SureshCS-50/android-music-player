package com.github.sureshcs_50.musicplayerapplication;

/**
 * Created by adminaccount on 20/12/17.
 */

public enum State {
    PLAYING,
    STOPPED,
    PAUSED,
    PREPARING,
    // in slow network preparing taking more time than usual,
    // when user click pause button we should pause player onPrepared(). and update UI
    PAUSE_ON_PREPARED
}
