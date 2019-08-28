package com.lianwukeji.iot.device.simulator.entity.odata;

import lombok.Getter;

/**
 * @author caozheng
 * @time 2019-05-09 10:23
 **/
public class ResolveResult {
    public static final ResolveResult UnknownDataTypeResult;
    public static final ResolveResult UnknownDataVersionResult;

    static{
        UnknownDataTypeResult = new ResolveResult();
        UnknownDataTypeResult.type = ResultType.UnknownDataType;

        UnknownDataVersionResult = new ResolveResult();
        UnknownDataVersionResult.type = ResultType.UnknownDataVersion;

    }

    @Getter
    private BinaryData data;
    @Getter
    private ResultType type;
    @Getter
    private String errorMessage;

    private ResolveResult(){

    }

    public static ResolveResult success(BinaryData data){
        ResolveResult result = new ResolveResult();
        result.type = ResultType.Success;
        result.data = data;

        return result;
    }

    public static ResolveResult invalidData(String errorMessage){
        ResolveResult result = new ResolveResult();
        result.type = ResultType.InvalidData;
        result.errorMessage = errorMessage;

        return result;
    }

}
