package io.github.tesla.gateway.netty.filter.help;

public interface JWTFilterConstant {
    String KE_ID_NAME = "ke_id";
    String UUS_ID_NAME = "uus_id";
    String KE_ID_COOKIE_NAME = KE_ID_NAME;
    String UUS_ID_COOKIE_NAME = UUS_ID_NAME;

    int EXPIRY_SECONDS = 600;
    String DOMAIN_COOKIE_BKJK = "*.bkjk.com";
}