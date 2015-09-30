package com.newroad.data.statistics.datamodel;

import java.io.Serializable;

/**
 * SuperNote user feed back 
 * @author: tangzj
 * @data : 2014-6-4
 * @since : 1.5
 */
public class UserFeedBack implements Serializable{
  /**
   * 
   */
  private static final long serialVersionUID = 3507522944424521970L;
  /**
   * email or phone number
   */
  private String contact;
  /**
   * feed back content
   */
  private String content;

  public String getContact() {
    return contact;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }
}
