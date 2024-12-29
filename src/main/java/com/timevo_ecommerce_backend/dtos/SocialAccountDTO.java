package com.timevo_ecommerce_backend.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class SocialAccountDTO {
    @JsonProperty("first_name")
    protected String firstName;

    @JsonProperty("last_name")
    protected String lastName;

    @JsonProperty("name")
    protected String name;

    @JsonProperty("avatar")
    protected String avatar;

    @JsonProperty("facebook_account_id")
    protected String facebookAccountId;

    @JsonProperty("google_account_id")
    protected String googleAccountId;

    public boolean isFacebookAccountIdValid() {
        return facebookAccountId != null && !facebookAccountId.isEmpty();
    }

    public boolean isGoogleAccountIdValid() {
        return googleAccountId != null && !googleAccountId.isEmpty();
    }

    public boolean isSocialLogin() {
        return (facebookAccountId != null && !facebookAccountId.isEmpty()) ||
                (googleAccountId != null && !googleAccountId.isEmpty());
    }
}
