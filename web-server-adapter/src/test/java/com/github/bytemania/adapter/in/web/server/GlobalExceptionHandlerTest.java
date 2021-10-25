package com.github.bytemania.adapter.in.web.server;

import com.github.bytemania.adapter.in.web.server.impl.ValidationException;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

class GlobalExceptionHandlerTest {

    private static final String PATH = "/path";
    private static final HttpServletRequest httpServletRequest = Mockito.mock(HttpServletRequest.class);
    private static final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();
    private static LogCaptor logCaptor;

    @BeforeAll
    static void beforeAll() {
        given(httpServletRequest.getPathInfo()).willReturn(PATH);
        given(httpServletRequest.getRequestURL()).willReturn(new StringBuffer("http://localhost"));
        given(httpServletRequest.getQueryString()).willReturn("param1=one");
        logCaptor = LogCaptor.forClass(GlobalExceptionHandler.class);
    }

    @AfterAll
    static void afterAll() {
        logCaptor.close();
    }

    @BeforeEach
    void beforeEach() {
        logCaptor.clearLogs();
    }

    @Test
    @DisplayName("Should create a response in ValidationException")
    void shouldCreateAResponseInValidationException() {
        ValidationException validationException = new ValidationException("Validation error message");
        var response = globalExceptionHandler.handleValidationError(validationException, httpServletRequest);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getError()).isEqualTo("Validation Error");
        assertThat(response.getBody().getMessage()).isEqualTo(validationException.getMessage());
        assertThat(response.getBody().getPath()).isEqualTo(PATH);
        assertThat(logCaptor.getLogs()).hasSize(1);
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error processing the Servlet url: http://localhost?param1=one");
    }

    @Test
    @DisplayName("Should create a response in IllegalStateException")
    void shouldCreateAResponseIllegalStateException() {
        IllegalStateException illegalStateException = new IllegalStateException("Illegal State Exception error message");
        var response = globalExceptionHandler.handleIllegalStateException(illegalStateException, httpServletRequest);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        assertThat(response.getBody().getError()).isEqualTo("Service Error");
        assertThat(response.getBody().getMessage()).isEqualTo(illegalStateException.getMessage());
        assertThat(response.getBody().getPath()).isEqualTo(PATH);
        assertThat(logCaptor.getLogs()).hasSize(1);
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error processing the Servlet url: http://localhost?param1=one");
    }

    @Test
    @DisplayName("Should create a response in Throwable")
    void shouldCreateAResponseThrowable() {
        Throwable throwable = new RuntimeException("Runtime Exception error message");
        var response = globalExceptionHandler.handleThrowable(throwable, httpServletRequest);
        assertThat(response).isNotNull();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getError()).isEqualTo("Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo(throwable.getMessage());
        assertThat(response.getBody().getPath()).isEqualTo(PATH);
        assertThat(logCaptor.getLogs()).hasSize(1);
        assertThat(logCaptor.getWarnLogs())
                .hasSize(1)
                .containsExactly("Error processing the Servlet url: http://localhost?param1=one");
    }

}