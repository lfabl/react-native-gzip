package com.gzip;

import androidx.annotation.NonNull;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.module.annotations.ReactModule;
import java.io.UnsupportedEncodingException;

import com.facebook.react.bridge.ReadableArray;

@ReactModule(name = GzipModule.NAME)
public class GzipModule extends ReactContextBaseJavaModule {
  public static final String NAME = "Gzip";
  public static final String ER_FAILURE = "ERROR_FAILED";

  public GzipModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  /**
   * Decompress the bytes.
   */
  @ReactMethod
  public void inflate(@NonNull final String data, @NonNull final Promise promise) {
    try {
      final byte[] inputBytes = Base64.decode(data, Base64.DEFAULT);

      String text = "";
      try {
        text = new String(inputBytes, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
      }

      promise.resolve(decompress(text.getBytes("UTF-8")));
    } catch (final Throwable ex) {
      promise.reject(ER_FAILURE, ex);
    }
  }

  /**
   * Compress bytes.
   */
  @ReactMethod
  public void deflate(@NonNull final String data, @NonNull final Promise promise) {
    try {
      promise.resolve(Base64.encodeToString(compress(data), Base64.NO_WRAP));
    } catch (final Throwable ex) {
      promise.reject(ER_FAILURE, ex);
    }
  }

  public static byte[] compress(String string) throws IOException {
    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
    GZIPOutputStream gos = new GZIPOutputStream(os);
    gos.write(string.getBytes());
    gos.close();
    byte[] compressed = os.toByteArray();
    os.close();
    return compressed;
  }

  public static String decompress(byte[] compressed) throws IOException {
    final int BUFFER_SIZE = 32;
    ByteArrayInputStream is = new ByteArrayInputStream(compressed);
    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
    StringBuilder string = new StringBuilder();
    byte[] data = new byte[BUFFER_SIZE];
    int bytesRead;
    while ((bytesRead = gis.read(data)) != -1) {
      string.append(new String(data, 0, bytesRead));
    }
    gis.close();
    is.close();
    return string.toString();
  }

}
