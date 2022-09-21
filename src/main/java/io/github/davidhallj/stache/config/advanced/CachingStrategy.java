package io.github.davidhallj.stache.config.advanced;

// TODO implement this
public enum CachingStrategy {
    /**
     *  Will cache each request, even if it has the same parameters.
     *
     *  Order absolutely matters.
     */
    PLAYBACK_MODE,
    /**
     * First request for a given set of parameters gets cached, and that cached file will
     * always be returned
     * TODO naming of this?
     * Note: this is the current behavior until an implementation of PLAYBACK_MODE can be supported
     */
    STATELESS
    //etc
}
