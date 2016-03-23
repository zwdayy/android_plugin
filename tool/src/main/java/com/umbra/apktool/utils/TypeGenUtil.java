package com.umbra.apktool.utils;

public class TypeGenUtil
{
  public static String newType(String type)
  {
    return type.substring(0, type.length() - 1) + "_CF;";
  }
}