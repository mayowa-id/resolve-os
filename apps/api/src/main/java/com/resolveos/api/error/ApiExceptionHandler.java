package com.resolveos.api.error;

import com.resolveos.application.identity.InsufficientOrganizationRoleException;
import com.resolveos.application.identity.MembershipRequiredException;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MembershipRequiredException.class)
    ProblemDetail handleMembershipRequired(MembershipRequiredException exception) {
        return problem(
                HttpStatus.NOT_FOUND,
                "RESOURCE_NOT_FOUND",
                "The requested resource is unavailable");
    }

    @ExceptionHandler(InsufficientOrganizationRoleException.class)
    ProblemDetail handleInsufficientRole(InsufficientOrganizationRoleException exception) {
        return problem(
                HttpStatus.FORBIDDEN,
                "INSUFFICIENT_ORGANIZATION_ROLE",
                "Your organization role does not permit this operation");
    }

    private static ProblemDetail problem(HttpStatus status, String code, String detail) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create("urn:resolveos:problem:" + code.toLowerCase().replace('_', '-')));
        problem.setTitle(status.getReasonPhrase());
        problem.setProperty("code", code);
        return problem;
    }
}
