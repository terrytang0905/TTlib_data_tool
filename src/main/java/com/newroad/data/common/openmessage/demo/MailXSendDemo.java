package com.newroad.data.common.openmessage.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.newroad.data.common.openmessage.utils.ConfigLoader;
import com.newroad.data.common.openmessage.config.AppConfig;
import com.newroad.data.common.openmessage.lib.MAILXSend;

public class MailXSendDemo {

  private static Logger logger = LoggerFactory.getLogger(MessageXSendDemo.class);

  public static AppConfig config = ConfigLoader.load(ConfigLoader.ConfigType.Mail);
  
  private static String mailProject="";

  public static void sendMailX(String address,String senderName,String subject){
    MAILXSend submail = new MAILXSend(config);
    submail.addTo(address, senderName);
//              submail.setSender("no-reply@submail.cn","SUBMAIL");
    submail.setProject(mailProject);
    submail.addVar("name", "leo");
    submail.addVar("age", "32");
    submail.addLink("developer", "http://submail.cn/chs/developer");
    submail.addLink("store", "http://submail.cn/chs/store");
    submail.addHeaders("X-Accept", "zh-cn");
    submail.addHeaders("X-Mailer", "YOYOPlay");
    submail.xsend();
  }
  
  public static void main(String[] args) {

    MAILXSend submail = new MAILXSend(config);
    submail.addTo("leo@submail.cn", "leo");
//		submail.setSender("no-reply@submail.cn","SUBMAIL");
    submail.setProject("uigGk1");
    submail.addVar("name", "leo");
    submail.addVar("age", "32");
    submail.addLink("developer", "http://submail.cn/chs/developer");
    submail.addLink("store", "http://submail.cn/chs/store");
    submail.addHeaders("X-Accept", "zh-cn");
    submail.addHeaders("X-Mailer", "leo App");
    submail.xsend();

  }
}
