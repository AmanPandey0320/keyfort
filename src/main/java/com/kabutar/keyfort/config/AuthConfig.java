package com.kabutar.keyfort.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "security.auth")
public class AuthConfig {
    private boolean isAuthzProtectionEnabled;
    private List<PreAuthnUrl> preAuthnUrls;

    public boolean isAuthzProtectionEnabled() {
        return isAuthzProtectionEnabled;
    }

    public void setisAuthzProtectionEnabled(boolean isAuthzProtectionEnabled) {
        this.isAuthzProtectionEnabled = isAuthzProtectionEnabled;
    }

    public List<PreAuthnUrl> getPreAuthnUrls() {
        return preAuthnUrls;
    }

    public void setPreAuthnUrls(List<PreAuthnUrl> preAuthnUrls) {
        this.preAuthnUrls = preAuthnUrls;
    }

    @Override
    public String toString() {
        return "AuthConfig [isAuthzProtectionEnabled=" + isAuthzProtectionEnabled
                + ", preAuthnUrls=" + preAuthnUrls + "]";
    }

    public static class PreAuthnUrl {
        private String path;
        private String method;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        @Override
        public String toString() {
            return "PreAuthUrls [path=" + path + ", method=" + method + "]";
        }
    }
}
