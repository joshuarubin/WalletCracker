package com.rubixconsulting.walletcracker;

import java.util.Arrays;
import java.util.List;

public class ObscurableString {
  protected final static String TAG = "DeviceInfoParser";
  public final String value;
  private String _field;
  static Boolean _fieldsLock = false;
  static List<String> _fields;
  static Boolean _obscure = false;

  static void setObscurableFields(String fields[]) {
    synchronized(_fieldsLock) {
      _fields = Arrays.asList(fields);
    }
  }

  static void setObscure(Boolean obscure) {
    synchronized(_obscure) {
      _obscure = obscure;
    }
  }

  public ObscurableString(String v) {
    value = v;
  }

  public ObscurableString(String field, String v) {
    _field = field;
    value = v;
  }

  public String toString() {
    if (_field == null) {
      return value;
    }

    synchronized(_obscure) {
      if (!_obscure) {
        return value;
      }

      return obscuredValue();
    }
  }

  public String obscuredValue() {
    synchronized(_fieldsLock) {
      if ((_fields != null) && (_fields.contains(_field))) {
        return value.replaceAll("[0-9a-zA-Z]", "X");
      }
    }

    return value;
  }
}