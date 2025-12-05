package jframework.hutils;

import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLIntegrityConstraintViolationException;


import com.google.gson.Gson;

import jframework.apiFormat.ApiSimple;


public class ReturnAPI {
    public static String getFormatSimple(Object result, Exception exception) throws Exception{
        ApiSimple apiSimple = new ApiSimple();
        apiSimple.setStatus((exception == null) ? "success": "error");
        apiSimple.setCode(getHttpCodeFromException(exception));
        apiSimple.setData(result);
        String message = "Success";
        if (exception != null) {
            message = exception.getMessage();
            apiSimple.setData(exception);
        }
        apiSimple.setMessage(message);
        Gson gson = new Gson();
        return gson.toJson(apiSimple);
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
