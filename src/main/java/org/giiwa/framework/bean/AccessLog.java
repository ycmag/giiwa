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
package org.giiwa.framework.bean;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicLong;

import org.giiwa.core.bean.Bean;
import org.giiwa.core.bean.Beans;
import org.giiwa.core.bean.DBMapping;
import org.giiwa.core.bean.UID;
import org.giiwa.core.bean.X;
import org.giiwa.core.conf.Local;
import org.giiwa.core.task.Task;

import com.mongodb.BasicDBObject;

// TODO: Auto-generated Javadoc
/**
 * record the web access log.
 * <br>
 * collection="gi_accesslog"
 * 
 * @author joe
 * 
 */
@DBMapping(collection = "gi_accesslog")
public class AccessLog extends Bean {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  static AtomicLong         seq              = new AtomicLong(0);
  static String             node             = Local.getConfig().getString("node");

  /**
   * Count.
   *
   * @param q
   *          the q
   * @return the long
   */
  public static long count(BasicDBObject q) {
    return Bean.count(q, AccessLog.class);
  }

  public String getUrl() {
    return this.getString("url");
  }

  /**
   * Creates the AccessLog.
   * 
   * @param ip
   *          the ip address
   * @param url
   *          the url
   * @param v
   *          the values
   */
  public static void create(final String ip, final String url, final V v) {
    new Task() {

      @Override
      public void onExecute() {
        long created = System.currentTimeMillis();
        String id = UID.id(ip, url, created, node, seq.incrementAndGet());
        Bean.insert(v.set(X._ID, id).set("ip", ip).set("url", url).set("created", created), AccessLog.class);
      }

    }.schedule(0);
  }

  /**
   * Load.
   *
   * @param q
   *          the q
   * @param order
   *          the order
   * @param s
   *          the s
   * @param n
   *          the n
   * @return the beans
   */
  public static Beans<AccessLog> load(BasicDBObject q, BasicDBObject order, int s, int n) {
    return Bean.load(q, order, s, n, AccessLog.class);
  }

  /**
   * Cleanup.
   */
  public static void cleanup() {
    Bean.delete(
        new BasicDBObject().append("created", new BasicDBObject().append("$lt", System.currentTimeMillis() - X.AMONTH)),
        AccessLog.class);
  }

  /**
   * Delete all.
   */
  public static void deleteAll() {
    Bean.delete(new BasicDBObject(), AccessLog.class);
  }

  /**
   * Distinct.
   *
   * @param name
   *          the name
   * @return Map
   */
  public static Map<Object, Long> distinct(String name) {
    List<Object> list = Bean.distinct(name, new BasicDBObject("status", 200), AccessLog.class);
    Map<Object, Long> m = new TreeMap<Object, Long>();
    for (Object v : list) {
      long d = Bean.count(new BasicDBObject(name, v).append("status", 200), AccessLog.class);
      m.put(v, d);
    }

    return m;
  }

}
