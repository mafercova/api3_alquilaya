package com.api.alquilaya.exceptions;

import jakarta.validation.ConstraintViolationException;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarValidacion(MethodArgumentNotValidException exception) {
        Map<String, String> errores = new TreeMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        exception.getBindingResult().getGlobalErrors().forEach(error ->
            errores.putIfAbsent(error.getObjectName(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Uno o más campos contienen valores inválidos."
        );
        problem.setTitle("Error de validación");
        problem.setProperty("errors", errores);
        return problem;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail manejarRestriccion(ConstraintViolationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Uno o más parámetros contienen valores inválidos."
        );
        problem.setTitle("Error de validación");
        problem.setProperty("errors", exception.getConstraintViolations().stream()
            .collect(TreeMap::new,
                (errores, violation) -> errores.put(violation.getPropertyPath().toString(), violation.getMessage()),
                TreeMap::putAll));
        return problem;
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ProblemDetail manejarValidacionParametros(HandlerMethodValidationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Uno o más parámetros contienen valores inválidos."
        );
        problem.setTitle("Error de validación");
        return problem;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail manejarFormato(HttpMessageNotReadableException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "El cuerpo de la solicitud está mal formado o contiene valores no válidos."
        );
        problem.setTitle("Solicitud inválida");
        return problem;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ProblemDetail manejarEstado(ResponseStatusException exception) {
        HttpStatus status = HttpStatus.valueOf(exception.getStatusCode().value());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            status, exception.getReason() == null ? status.getReasonPhrase() : exception.getReason()
        );
        problem.setTitle(status.getReasonPhrase());
        return problem;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail manejarConflicto(DataIntegrityViolationException exception) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, "La operación entra en conflicto con los datos existentes o sus relaciones."
        );
        problem.setTitle("Conflicto de datos");
        return problem;
    }
}
