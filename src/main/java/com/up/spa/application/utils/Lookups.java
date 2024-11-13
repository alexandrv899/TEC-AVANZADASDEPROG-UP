package com.up.spa.application.utils;

import org.bson.Document;

public class Lookups {

  /**
   * Creates a safe lookup stage document for MongoDB aggregation
   */
  public static Document lookup(String from, String localField, String foreignField, String as) {
    return new Document("$lookup", new Document()
        .append("from", from)
        .append("localField", localField)
        .append("foreignField", foreignField)
        .append("as", as));
  }

}
