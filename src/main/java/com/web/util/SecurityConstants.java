package com.web.util;

public final class SecurityConstants {
    public static final String SECRET = "my_super_secret_key";
    public static final long EXPIRATION_TIME = 900_000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/api/users";
    public static final String AUTHORITIES = "AUTHORITIES";
    public static final String USER_ID = "USER_ID";
    public static final String SWAGGER_UI_HTML = "/swagger-ui.html";
    public static final String SWAGGER_UI_ALL = "/swagger-ui/**";
    public static final String SWAGGER = "/v3/api-docs";
    public static final String SWAGGER_ALL = "/v3/api-docs/**";
}
