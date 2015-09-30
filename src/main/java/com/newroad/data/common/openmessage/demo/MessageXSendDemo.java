package com.newroad.data.common.openmessage.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.openmessage.config.AppConfig;
import com.newroad.data.common.openmessage.lib.MESSAGEXsend;
import com.newroad.data.common.openmessage.utils.ConfigLoader;

public class MessageXSendDemo {

  private static Logger logger = LoggerFactory.getLogger(MessageXSendDemo.class);

  public static AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Message);

  /*
   * public static void main(String[] args) { AppConfig config =
   * ConfigLoader.load(ConfigLoader.ConfigType.Message); MESSAGEXsend submail = new
   * MESSAGEXsend(config); submail.addTo("18121025090"); submail.setProject("4cjge3");
   * submail.addVar("user","阿汤哥"); submail.addVar("pname","文字探密"); submail.addVar("price","499");
   * submail.addVar("pcs","1"); submail.addVar("phone", "13564367362"); submail.xsend(); }
   */

  public static void sendSystemSms(String mobileTo, String userName, String orderId, String pName, String price, String pcs,
      String userPhone) {
    logger.info("Send system message to mobile " + mobileTo);
    MESSAGEXsend submail = new MESSAGEXsend(config);
    submail.addTo(mobileTo);
    submail.setProject("4cjge3");
    submail.addVar("user", userName);
    submail.addVar("orderid", orderId);
    submail.addVar("pname", pName);
    submail.addVar("price", price);
    submail.addVar("pcs", pcs);
    submail.addVar("phone", userPhone);
    submail.xsend();
  }

  public static void sendClientSms(String mobileTo, String userName, String orderId, String pName, String price, String pcs) {
    logger.info("Send client message to mobile " + mobileTo);
    MESSAGEXsend clientSubmail = new MESSAGEXsend(config);
    clientSubmail.addTo(mobileTo);
    clientSubmail.setProject("dnVjp1");
    clientSubmail.addVar("user", userName);
    clientSubmail.addVar("orderid", orderId);
    clientSubmail.addVar("pname", pName);
    clientSubmail.addVar("price", price);
    clientSubmail.addVar("pcs", pcs);
    clientSubmail.xsend();
  }
}
