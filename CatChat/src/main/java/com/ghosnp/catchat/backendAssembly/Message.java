package com.ghosnp.catchat.backendAssembly;

import java.io.Serializable;
import java.util.Date;

public record Message(String fromWho, String sendToWho, String message, Date date)
        implements Serializable {
    public String toString() {
        return "[" + fromWho + "] send [" + message + "] to ["
                + sendToWho + "] at [" + date.toString() + "]";
    }
}
