package com.newroad.data.transfer.manager;

import com.newroad.data.statistics.note.cloudresource.ResourceAnalyzer;

public class ResourceTransferAction {

  public static void main(String[] args) throws Exception {
    if (args.length == 0 || (!"-c".equals(args[0]) && !"-d".equals(args[0]))) {
      System.out.println("Please input transfer option: -c cache or -d resource !");
      System.exit(0);
    }
    if (args.length < 3) {
      System.out.println("Please input source file path and output file path!");
      System.exit(0);
    }
    ResourceAnalyzer dtHandler = new ResourceAnalyzer();
    dtHandler.searchDataByCSV(args[1], args[2]);
    // if ("-c".equals(args[0])) {
    // CacheTransferUtility ctu = new CacheTransferUtility();
    // ctu.init();
    // ctu.syncMemcachedCouchbase();
    // } else if ("-d".equals(args[0])) {
    // ResourceAnalyzer dtHandler = new ResourceAnalyzer();
    // dtHandler.searchDataByCSV("lenote_objects.csv",
    // "lenote_inconsistent_objects.csv");
    // }
  }
}
