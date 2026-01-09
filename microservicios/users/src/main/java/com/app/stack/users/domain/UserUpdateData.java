package com.app.stack.users.domain;

import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateData {
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private UserStatus status;
    private String phone;
    private Map<String, Object> metadata;
}
