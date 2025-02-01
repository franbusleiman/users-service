package com.liro.usersservice.exceptions.handlers;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.liro.usersservice.exceptions.BadRequestException;
import com.liro.usersservice.exceptions.ResourceNotFoundException;
import com.liro.usersservice.exceptions.UnauthorizedException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

import static com.liro.usersservice.exceptions.constants.ErrorCodes.*;

@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
@ControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Object> handleFeignException(FeignException ex) {

        int status = ex.status();

        if (status == 401) {
            return buildResponseEntity(new ApiError(HttpStatus.valueOf(status), "Permissions Denied", UNAUTHORIZED));
        }
        String message = extractFeignMessage(ex);
        String code = extractFeignCode(ex);

        log.error("{} error. Message: {}", code, message);
        return buildResponseEntity(new ApiError(HttpStatus.valueOf(status), message, code));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex) {
        String error = ex.getParameterName() + " parameter is missing";

        log.error("{} error. Message: {}", BAD_REQUEST, error);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, error, BAD_REQUEST));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex) {
        StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t).append(", "));

        String errorMessage = builder.substring(0, builder.length() - 2);

        log.error("{} error. Message: {}", BAD_REQUEST, errorMessage);
        return buildResponseEntity(new ApiError(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                errorMessage, BAD_REQUEST));
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({org.hibernate.exception.ConstraintViolationException.class, DataIntegrityViolationException.class})
    protected ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, DataIntegrityViolationException dataEx) {

        log.error("{} error. Message: {}", VALIDATION_ERROR, ex.getMessage());
        return buildResponseEntity(new ApiError(HttpStatus.CONFLICT, ex.getMessage(), VALIDATION_ERROR));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

        String errorMessage = "Malformed JSON request";

        log.error("{} error. Message: {}", BAD_REQUEST, errorMessage);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errorMessage, BAD_REQUEST));
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex) {
        String errorMessage = "Error writing JSON output";

        log.error("{} error. Message: {}", INTERNAL_SERVER_ERROR, errorMessage);
        return buildResponseEntity(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, errorMessage, INTERNAL_SERVER_ERROR));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<Object> handleWebExchangeBindException(MethodArgumentNotValidException e) {

        String errors = e.getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .sorted()
                .collect(Collectors.joining(", "));

        log.error("{} error. Message: {}", BAD_INPUT_REQUEST, errors);
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, errors, BAD_INPUT_REQUEST));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    protected ResponseEntity<Object> handleNotFoundExceptions(RuntimeException ex) {

        log.error("{} error. Message: {}", NOT_FOUND, ex.getMessage());
        return buildResponseEntity(new ApiError(HttpStatus.NOT_FOUND, ex.getMessage(), NOT_FOUND));
    }

    @ExceptionHandler(UnauthorizedException.class)
    protected ResponseEntity<Object> handleUnauthorizedExceptions(RuntimeException ex) {

        log.error("{} error. Message: {}", UNAUTHORIZED, ex.getMessage());
        return buildResponseEntity(new ApiError(HttpStatus.UNAUTHORIZED, ex.getMessage(), UNAUTHORIZED));
    }

    @ExceptionHandler(BadRequestException.class)
    protected ResponseEntity<Object> handleBadRequestExceptions(RuntimeException ex) {

        log.error("{} error. Message: {}", BAD_REQUEST, ex.getMessage());
        return buildResponseEntity(new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage(), BAD_REQUEST));
    }

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    private String extractFeignMessage(FeignException ex) {
        try {
            // Parsear el JSON del contenido de Feign (si existe)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(ex.contentUTF8());
            return jsonNode.has("message") ? jsonNode.get("message").asText() : ex.getMessage();
        } catch (Exception e) {
            return ex.getMessage();
        }
    }

    private String extractFeignCode(FeignException ex) {
        try {
            // Extraer el código del error desde el JSON de Feign (si existe)
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(ex.contentUTF8());
            return jsonNode.has("code") ? jsonNode.get("code").asText() : null;
        } catch (Exception e) {
            return null;
        }
    }
}