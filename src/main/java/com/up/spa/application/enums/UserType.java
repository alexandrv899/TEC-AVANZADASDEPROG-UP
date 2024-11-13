package com.up.spa.application.enums;

public enum UserType {

  CLIENT("client"),
  PROFESSIONAL("professional");

  private final String value;

  UserType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  public static UserType fromValue(String value) {
    for (UserType userType : UserType.values()) {
      if (userType.getValue().equalsIgnoreCase(value)) {
        return userType;
      }
    }
    throw new IllegalArgumentException("Unknown UserType: " + value);
  }
}
