/*
 * Copyright 2015 Giiwa, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.giiwa.core.bean;

import java.util.Random;
import java.util.UUID;

import org.giiwa.core.base.*;
import org.giiwa.core.conf.Global;

import com.mongodb.BasicDBObject;

// TODO: Auto-generated Javadoc
/**
 * The {@code UID} Class used to create unique id, or sequence, random string
 * 
 * @author joe
 *
 */
@DBMapping(collection = "gi_config")
public class UID extends Bean {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * increase and get the unique sequence number by key.
   *
   * @param key
   *          the key
   * @return long of the unique sequence
   */
  public synchronized static long next(String key) {

    long prefix = Global.l("system.code", 1) * 10000000000000L;

    try {

      long v = -1;
      UID u = Bean.load(new BasicDBObject(X._ID, key), UID.class);
      if (u == null) {
        v = 1;

        String linkid = UID.random();
        Bean.insertCollection(V.create(X._ID, key).set("var", v + 1L).set("linkid", linkid), UID.class);
        u = Bean.load(new BasicDBObject(X._ID, key), UID.class);
        if (u == null) {
          log.error("create unique sequence error");
          return -1;
        } else if (!linkid.equals(u.getString("linkid"))) {
          // created by other node in cluster system
          return next(key);
        }

      } else {
        v = Bean.toLong(u.get("var"), 1);
        while (Bean.updateCollection(new BasicDBObject(X._ID, key).append("var", v), V.create("var", v + 1L),
            UID.class) < 0) {
          v++;
        }
      }

      return prefix + v;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return -1;
  }

  /**
   * create integer sequence for the name.
   * 
   * @param name
   *          the name of the sequence
   * @return integer
   */
  public synchronized static int nextInt(String name) {

    try {

      int v = -1;
      UID u = Bean.load(new BasicDBObject(X._ID, name), UID.class);
      if (u == null) {
        v = 1;

        String linkid = UID.random();
        Bean.insertCollection(V.create(X._ID, name).set("var", v + 1).set("linkid", linkid), UID.class);
        u = Bean.load(new BasicDBObject(X._ID, name), UID.class);
        if (u == null) {
          log.error("create unique sequence error");
          return -1;
        } else if (!linkid.equals(u.getString("linkid"))) {
          // created by other node in cluster system
          return nextInt(name);
        }

      } else {
        v = X.toInt(u.get("var"), 1);
        while (Bean.updateCollection(new BasicDBObject(X._ID, name).append("var", v), V.create("var", v + 1L),
            UID.class) < 0) {
          v++;
        }
      }

      return v;
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }

    return -1;
  }

  /**
   * generate a global random string.
   * 
   * @return the string
   */
  public static String random() {
    return UUID.randomUUID().toString();
  }

  /**
   * convert the long data to a BASE32 string.
   *
   * @param hash
   *          the hash
   * @return the string
   */
  public static String id(long hash) {
    // System.out.println(hash);
    // System.out.println(Long.toHexString(hash));
    // System.out.println(H64.toString(hash));
    // System.out.println(H32.toString(hash));
    return H32.toString(hash);
    // return Long.toHexString(hash);
  }

  /**
   * generate the unique id by the parameter <br>
   * if the parameter are same, the id will be same, the "id" is H32 of
   * hash(64bit) of parameters.
   *
   * @param ss
   *          the parameters
   * @return string
   */
  public static String id(Object... ss) {
    StringBuilder sb = new StringBuilder();
    for (Object s : ss) {
      if (sb.length() > 0)
        sb.append("/");
      sb.append(s);
    }
    return id(hash(sb.toString()));
  }

  /**
   * global id.
   *
   * @return String
   */
  public static String uuid() {
    return UUID.randomUUID().toString();
  }

  /**
   * Hash (64bits) of string.
   *
   * @param s
   *          the parameter string
   * @return the long
   */
  public static long hash(String s) {
    if (s == null) {
      return 0;
    }

    int h = 0;
    int l = 0;
    int len = s.length();
    char[] val = s.toCharArray();
    for (int i = 0; i < len; i++) {
      h = 31 * h + val[i];
      l = 29 * l + val[i];
    }
    return ((long) h << 32) | ((long) l & 0x0ffffffffL);
  }

  /**
   * generate a random string with the length.
   *
   * @param length
   *          the length of the random string
   * @return the string
   */
  public static String random(int length) {

    Random rand = new Random(System.currentTimeMillis());
    StringBuilder sb = new StringBuilder();
    while (length > 0) {
      sb.append(chars[rand.nextInt(chars.length - 1)]);
      length--;
    }
    return sb.toString();
  }

  public static String random(int length, String sources) {
    if (sources == null || sources.length() == 0) {
      sources = new String(digitals);
    }
    int codesLen = sources.length();
    Random rand = new Random(System.currentTimeMillis());
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(sources.charAt(rand.nextInt(codesLen - 1)));
    }
    return sb.toString();
  }

  /**
   * generate a digital string with the length.
   *
   * @param length
   *          the length of the digital string
   * @return the string
   */
  public static String digital(int length) {
    Random rand = new Random(System.currentTimeMillis());
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < length; i++) {
      sb.append(digitals[rand.nextInt(digitals.length - 1)]);
    }
    return sb.toString();
  }

  static final char[] digitals = "0123456789".toCharArray();
  static final char[] chars    = "0123456789abcdefghjiklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

}
