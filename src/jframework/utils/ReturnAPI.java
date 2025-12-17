package jframework.utils;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;


import com.google.gson.Gson;

import jframework.apiFormat.ApiRobuste;
import jframework.apiFormat.ApiSimple;
import jframework.tools.ModelView;

 
public class ReturnAPI {
    public static String getFormatSimple(Object result, Exception exception) throws Exception{
        ApiSimple apiSimple = new ApiSimple();
        String status = "";
        int code = getHttpCodeFromException(exception);;
        Object data = null;
        int count = 0;
        String message = "";
        if (exception != null) {
            System.out.println("exception ooooh"); 
            message = exception.getMessage();
            data = exception.getMessage();
            status = "error";
            message = "It's error";
            count = 0;
            apiSimple.setStatus("error");
        }else{
            status = "success";
            count = 1;
            message = "Success";
            if (result.getClass().getName().compareToIgnoreCase("jframework.tools.ModelView") == 0) {
                ModelView modelView = (ModelView) result;
                data = modelView.getData();
            } else 
                data = result;
        }
        apiSimple.setStatus(status);
        apiSimple.setMessage(message);
        apiSimple.setCode(code);
        apiSimple.setData(data);
        apiSimple.setCount(count);
        Gson gson = new Gson();
        return gson.toJson(apiSimple);
    }


    public static String getFormatRobuste(Object result, Exception exception) throws Exception{
        ApiRobuste apiRobuste = new ApiRobuste();
        String status = "";
        Object data = null;
        Object error = null;
        if (exception != null) {
            error = exception.getMessage();
            status = "error";
            data = null;
        }else{
            error = null;
            status = "success";
            if (result.getClass().getName().compareToIgnoreCase("jframework.tools.ModelView") == 0) {
                ModelView modelView = (ModelView) result;
                data = modelView.getData();
            } else 
                data = result;
        }
        apiRobuste.setData(data);
        apiRobuste.setError(error);
        apiRobuste.setStatus(status);
        Gson gson = new Gson();
        return gson.toJson(apiRobuste);
    }

    public static String getFormatRest(Object result, Exception exception) throws Exception{
        Object data = null;
        if (exception != null) {
            data = exception.getMessage();
        }else{
            if (result.getClass().getName().compareToIgnoreCase("jframework.tools.ModelView") == 0) {
                ModelView modelView = (ModelView) result;
                data = modelView.getData();
            } else 
                data = result;
        }
        Gson gson = new Gson();
        return gson.toJson(data);
    }

    public static int getHttpCodeFromException(Exception e) {

    if (e == null) return 200;

    Throwable cause = (e instanceof InvocationTargetException)
            ? ((InvocationTargetException)e).getCause()
            : e;

    if (cause instanceof NumberFormatException) return 400;
    if (cause instanceof IllegalArgumentException) return 400;
    if (cause instanceof java.time.format.DateTimeParseException) return 400;

    if (cause instanceof SecurityException) return 401;
    if (cause instanceof javax.naming.AuthenticationException) return 401;
    if (cause instanceof javax.security.sasl.AuthenticationException) return 401;


    if (cause instanceof AccessDeniedException) return 403;

    if (cause instanceof FileNotFoundException) return 404;

    if (cause instanceof SQLIntegrityConstraintViolationException) return 409;
        
    

    // défaut = erreur interne
    return 500;
}

}
