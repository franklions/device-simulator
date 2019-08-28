package com.lianwukeji.iot.device.simulator.entity.odata;

/**
 * @author caozheng
 * @time 2019-05-09 10:53
 **/
public class ODataResolveException extends Exception {
    private final ODataMessage oDataMessage;

    public ODataResolveException(ODataMessage oDataMessage) {
        this.oDataMessage = oDataMessage;
    }

    public ODataResolveException(ODataMessage oDataMessage, String message) {
        super(message);
        this.oDataMessage = oDataMessage;
    }

    public ODataResolveException(ODataMessage oDataMessage, String message, Throwable cause) {
        super(message, cause);
        this.oDataMessage = oDataMessage;
    }

    public ODataResolveException(ODataMessage oDataMessage, Throwable cause) {
        super(cause);
        this.oDataMessage = oDataMessage;
    }

    public ODataResolveException(ODataMessage oDataMessage, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.oDataMessage = oDataMessage;
    }

    public ODataMessage getODataMessage(){
        return oDataMessage;
    }
}
