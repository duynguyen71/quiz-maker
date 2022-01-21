package com.nkd.quizmaker.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse {

    private Object data;

    private Integer status;

    private Integer statusText;

    private String message;

    public BaseResponse(Object data, Integer status) {
        this.data = data;
    }

    public static Object successData(Object data) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(data);
        return  baseResponse;
    }
    public static Object successData(Object data,String message) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(data);
        baseResponse.setMessage(message);
        return  baseResponse;
    }

    public static Object notFound(String message) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(message);
        baseResponse.setStatus(401);
        return  baseResponse;
    }
    public static Object badRequest(String message){
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setMessage(message);
        baseResponse.setStatus(400);
        return  baseResponse;
    }
}
