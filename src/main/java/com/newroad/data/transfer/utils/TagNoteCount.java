package com.newroad.data.transfer.utils;

/**
 * @author tangzj1
 * @version 2.0
 * @since May 29, 2014
 */
public class TagNoteCount {

  private String tagID;

  private Integer tagNoteCount;
  
  /**
   * @param tagID
   * @param tagNoteCount
   */
  public TagNoteCount(String tagID, Integer tagNoteCount) {
    super();
    this.tagID = tagID;
    this.tagNoteCount = tagNoteCount;
  }

  public String getTagID() {
    return tagID;
  }

  public void setTagID(String tagID) {
    this.tagID = tagID;
  }

  public Integer getTagNoteCount() {
    return tagNoteCount;
  }

  public void setTagNoteCount(Integer tagNoteCount) {
    this.tagNoteCount = tagNoteCount;
  }


}
