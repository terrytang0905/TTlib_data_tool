package com.newroad.data.transfer.manager;

import com.newroad.data.common.db.mongo.MongoConnectionFactory;
import com.newroad.data.common.utils.PropertiesUtils;
import com.newroad.data.transfer.upgrade.DataIntegrityChecker;

public class DBIntegrityAction {

  /**
   * @Title: data upgrade stage 3
   * @Description: Check all of the latest data whether is completed or not
   * @param @param args
   * @return void
   * @throws
   */
  public static void main(String[] args) {
    // TODO Auto-generated method stub
    DataIntegrityChecker checker =
        new DataIntegrityChecker(MongoConnectionFactory.dbName1, MongoConnectionFactory.dbName2, PropertiesUtils.errorListFilePath,
            PropertiesUtils.missListFilePath,PropertiesUtils.invalidUserListFilePath);
    checker.checkDefaultDataIntegrity();
    checker.checkOplogIntegrity();
    //checker.syncOplogIntegrity();
  }

}
