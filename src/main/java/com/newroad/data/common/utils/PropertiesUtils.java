package com.newroad.data.common.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertiesUtils {

  private static Properties propertie;
  // mongoDB data transfer
  public static String userListFolderPath=null;
  public static String errorListFilePath=null;
  public static String missListFilePath=null;
  public static String invalidUserListFilePath=null;
  public static String reloadUserListFolderPath=null;
  public static String incrementTime=null;
  public static String oplogDataList=null;
  
  static {
    propertie = new Properties();
    try {
      String filePath = "config.properties";
      propertie.load(PropertiesUtils.class.getClassLoader().getResourceAsStream(filePath));
      userListFolderPath=propertie.getProperty("user.file.path");
      errorListFilePath=propertie.getProperty("error.file.path");
      missListFilePath=propertie.getProperty("miss.user.file.path");
      invalidUserListFilePath=propertie.getProperty("invalid.user.file.path");
      reloadUserListFolderPath=propertie.getProperty("reload.user.file.path");
      incrementTime=propertie.getProperty("increment.time");
      oplogDataList=propertie.getProperty("oplog.data.list");
    } catch (FileNotFoundException ex) {
      ex.printStackTrace();
    } catch (IOException ex) {
      ex.printStackTrace();
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
