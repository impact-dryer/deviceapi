package com.impactdryer.deviceapi.devicemanagment.web;

import com.impactdryer.deviceapi.devicemanagment.domain.InvalidMacAddressException;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.MultipleRootDevicesFoundException;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.NoRootDeviceFoundException;
import com.impactdryer.deviceapi.devicemanagment.infrastructure.NoUplinkFoundException;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(NoRootDeviceFoundException.class)
    public ProblemDetail handleNoRootDeviceFoundException(NoRootDeviceFoundException ex) {
        return createProblemDetail(ex, HttpStatusCode.valueOf(404), "No Root Device Found");
    }

    @ExceptionHandler(NoUplinkFoundException.class)
    public ProblemDetail handleNoUplinkDeviceFoundException(NoUplinkFoundException ex) {
        return createProblemDetail(ex, HttpStatusCode.valueOf(404), "No Uplink Device Found");
    }

    @ExceptionHandler(MultipleRootDevicesFoundException.class)
    public ProblemDetail handleMultipleRootDevicesFoundException(MultipleRootDevicesFoundException ex) {
        return createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Multiple Root Devices Found");
    }

    @ExceptionHandler(InvalidMacAddressException.class)
    public ProblemDetail handleInvalidMacAddressException(InvalidMacAddressException ex) {
        return createProblemDetail(ex, HttpStatus.BAD_REQUEST, "Invalid MAC Address");
    }

    @ExceptionHandler(ValidationException.class)
    public ProblemDetail handleValidationException(ValidationException ex) {
        return createProblemDetail(
                ex, HttpStatus.BAD_REQUEST, "Validation Error " + ex.getCause().getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleDefaultException(Exception ex) {
        return createProblemDetail(ex, HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error");
    }

    private ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String title) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        problemDetail.setTitle(title);
        return problemDetail;
    }
}
