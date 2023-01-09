package net.gnfe.bin.rest.response.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.http.HttpStatus;

import java.util.Date;

@ApiModel(value = "ApiError")
public class ApiError {

    @ApiModelProperty(notes = "Timestamp de quando ocorreu o erro.")
    private Date timestamp;

    @ApiModelProperty(notes = "HTTP status.")
    private HttpStatus status;

    @ApiModelProperty(notes = "Msg a ser exibida para o usuário.")
    private String message;

    @ApiModelProperty(notes = "Informações adicionais em caso de erro.")
    private String debugMessage;

    private ApiError() {
        timestamp = new Date();
    }

    public ApiError(HttpStatus status) {
        this();
        this.status = status;
    }

    public ApiError(HttpStatus status, Throwable ex) {
        this();
        this.status = status;
        String message = ex.getMessage();
        this.message = "Erro inesperado: " + message;
        this.debugMessage = "Erro inesperado: " + message;
    }

    public ApiError(HttpStatus status, String message, Throwable ex) {
        this();
        this.status = status;
        this.message = message;
        this.debugMessage = ex.getLocalizedMessage();
    }

    public HttpStatus getStatus() {
        return status;
    }

    public Date getTimestamp() {
		return timestamp;
	}

	public String getMessage() {
        return message;
    }

    public String getDebugMessage() {
        return debugMessage;
    }
}