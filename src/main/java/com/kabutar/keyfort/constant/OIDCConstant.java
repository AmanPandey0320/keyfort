package com.kabutar.keyfort.constant;

public class OIDCConstant {
    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private OIDCConstant() {
        // This class should not be instantiated.
    }

    /**
     * The URL of the OpenID Provider, which is also its unique identifier.
     */
    public static final String ISSUER = "issuer";

    /**
     * The URL of the provider's OAuth 2.0 Authorization Endpoint.
     */
    public static final String AUTHORIZATION_ENDPOINT = "authorization_endpoint";

    /**
     * The URL of the provider's OAuth 2.0 Token Endpoint.
     */
    public static final String TOKEN_ENDPOINT = "token_endpoint";

    /**
     * The URL of the provider's OAuth 2.0 Token Endpoint.
     */
    public static final String EXCHANGE_TOKEN_ENDPOINT = "exchange_token_endpoint";

    /**
     * The URL of the provider's UserInfo Endpoint.
     */
    public static final String USERINFO_ENDPOINT = "userinfo_endpoint";

    /**
     * The URL of the provider's JSON Web Key Set (JWKS) document.
     */
    public static final String JWKS_URI = "jwks_uri";

    /**
     * A JSON array of OAuth 2.0 scope values that this provider supports.
     */
    public static final String SCOPES_SUPPORTED = "scopes_supported";

    /**
     * A JSON array of the `response_type` values that this provider supports.
     */
    public static final String RESPONSE_TYPES_SUPPORTED = "response_types_supported";

    /**
     * A JSON array of the `Subject Identifier` types that this provider supports.
     */
    public static final String SUBJECT_TYPES_SUPPORTED = "subject_types_supported";

    /**
     * A JSON array of the JWS signing algorithms (`alg` values) supported by the
     * OP for the ID Token to encode the Claims in a JWT.
     */
    public static final String ID_TOKEN_SIGNING_ALG_VALUES_SUPPORTED = "id_token_signing_alg_values_supported";

    /**
     * A JSON array of Client Authentication methods supported by this Token Endpoint.
     */
    public static final String TOKEN_ENDPOINT_AUTH_METHODS_SUPPORTED = "token_endpoint_auth_methods_supported";
}
