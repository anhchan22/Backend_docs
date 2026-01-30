package com.qlsv.dkmh.controller;

import com.nimbusds.jose.JOSEException;
import com.qlsv.dkmh.dto.request.AuthenticationReqest;
import com.qlsv.dkmh.dto.request.IntrospectRequest;
import com.qlsv.dkmh.dto.response.ApiResponse;
import com.qlsv.dkmh.dto.response.AuthenticationResponse;
import com.qlsv.dkmh.dto.response.IntrospectResponse;
import com.qlsv.dkmh.service.AuthenticationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;
    @PostMapping("/login")
    ApiResponse<AuthenticationResponse> authenticationResponseApiResponse(@RequestBody AuthenticationReqest request) throws JOSEException {
        var rs = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(rs)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticationIntrospect(@RequestBody IntrospectRequest request) throws JOSEException, ParseException {
        var rs = authenticationService.introspectResponse(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(rs)
                .build();
    }
}
