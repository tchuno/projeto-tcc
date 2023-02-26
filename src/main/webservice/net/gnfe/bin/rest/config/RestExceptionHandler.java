package net.gnfe.bin.rest.config;


import net.gnfe.bin.domain.service.ExceptionService;
import net.gnfe.bin.rest.exception.HTTP401Exception;
import net.gnfe.bin.rest.exception.UsuarioRestException;
import net.gnfe.bin.rest.response.vo.ApiError;
import net.gnfe.util.DummyUtils;
import net.gnfe.util.ddd.MessageKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.faces.validator.ValidatorException;
import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @Autowired private ExceptionService exceptionService;

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String error = "Parâmetros de entrada inválidos: Malformed JSON request";
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, ex), ex);
    }

    @ExceptionHandler(HTTP401Exception.class)
    public ResponseEntity<Object> handleHTTP401Exception(HttpServletRequest httpServletRequest, HTTP401Exception ex) {
        ApiError apiError = new ApiError(ex.getStatus(), getMsg(ex), ex);
        return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(UsuarioRestException.class)
    public ResponseEntity<Object> handleUsuarioRestException(HttpServletRequest httpServletRequest, UsuarioRestException ex) {
        ApiError apiError = new ApiError(ex.getStatus(), getMsg(ex), ex);
        return buildResponseEntity(apiError, ex);
    }

    private String getMsg(MessageKeyException ex) {
        return exceptionService.getMessage(ex);
    }

    @ExceptionHandler(ValidatorException.class)
    public ResponseEntity<Object> handleValidatorException(HttpServletRequest httpServletRequest, ValidatorException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), ex);
		return buildResponseEntity(apiError, ex);
    }

    @ExceptionHandler(MessageKeyException.class)
    public ResponseEntity<Object> handleMessageKeyException(HttpServletRequest httpServletRequest, MessageKeyException ex) {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, getMsg(ex), ex);
		return buildResponseEntity(apiError, ex);
    }


    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Object> handleControllerException(HttpServletRequest httpServletRequest, Throwable ex) {
        ex.printStackTrace();
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex), ex);
    }

    @ResponseBody
    private ResponseEntity<Object> buildResponseEntity(ApiError apiError, Throwable exception) {
        DummyUtils.systrace(apiError);
        exception.printStackTrace();
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }
}
