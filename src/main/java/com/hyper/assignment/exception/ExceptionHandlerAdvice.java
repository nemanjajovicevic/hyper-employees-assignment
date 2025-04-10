package com.hyper.assignment.exception;

import com.hyper.assignment.dto.ApiResponse;
import com.hyper.assignment.dto.EmployeeResponseDto;
import com.hyper.assignment.util.Error;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static com.hyper.assignment.util.DateTimeUtil.DATE_TIME_FORMAT;
import static com.hyper.assignment.util.DateTimeUtil.getFormattedLocalDateTimeNow;
import static com.hyper.assignment.util.Error.EMPLOYEE_NOT_FOUND_ERROR_1100;
import static com.hyper.assignment.util.Error.EMPLOYEE_VALIDATION_ERROR_1200;
import static com.hyper.assignment.util.Status.ERROR;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionHandlerAdvice {

    @ExceptionHandler({EmployeeNotFoundException.class})
    public ResponseEntity<ApiResponse<?>> handleEmployeeNotFoundException(EmployeeNotFoundException e) {
        ApiResponse<?> responseException = generateErrorResponseAndLogError(e.getMessage(), EMPLOYEE_NOT_FOUND_ERROR_1100);
        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({TeamNotFoundException.class})
    public ResponseEntity<ApiResponse<?>> handleTeamNotFoundException(TeamNotFoundException e) {
        ApiResponse<?> responseException = generateErrorResponseAndLogError(e.getMessage(), EMPLOYEE_NOT_FOUND_ERROR_1100);
        return new ResponseEntity<>(responseException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ApiResponse<?> responseException = generateErrorResponseAndLogError(e.getMessage(), EMPLOYEE_VALIDATION_ERROR_1200);
        return new ResponseEntity<>(responseException, HttpStatus.BAD_REQUEST);
    }

    private ApiResponse<?> generateErrorResponseAndLogError(String message, Error error) {
        String errorMessage = String.format("%s: %s", error.getMessage(), message);
        log.error(errorMessage);

        ApiResponse.ErrorDto errorDto = ApiResponse.ErrorDto.builder()
                .code(error.getCode())
                .message(error.getMessage())
                .build();

        return ApiResponse.builder()
                .timestamp(getFormattedLocalDateTimeNow(DATE_TIME_FORMAT))
                .status(ERROR)
                .error(errorDto)
                .build();
    }

}
