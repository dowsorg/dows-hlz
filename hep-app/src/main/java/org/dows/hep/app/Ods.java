//package org.dows.hep.app;
//
//import lombok.AllArgsConstructor;
//import lombok.Builder;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.util.List;
//import java.util.Stack;
//import java.util.concurrent.atomic.AtomicInteger;
//
///**
// * @author runsix
// */
//public class Ods {
//  @Data
//  @NoArgsConstructor
//  @AllArgsConstructor
//  @Builder
//  public static class OdsMethod {
//    private String methodName;
//    //参数列表名
//    private List<String> paramNameList;
//    // 方法签名
//    private String methodSign;
//    private String key;
//    private Object value;
//    private OdsMethod odsMethod;
//  }
//
//  public static void main(String str) {
//    String var = "=$";
//    String varSplit = "&";
//    String methodStart = "(";
//    String methodEnd = ")";
//    Stack<String> stringStack = new Stack<>();
//    for (int i = 0; i <= str.length()-1; i++) {
//      int nameStartIndex;
//      int nameEndIndex;
//    methodEnd.charAt()
//    }
//  }
//
//  private static List<OdsMethod> parse(AtomicInteger atomicInteger, List<OdsMethod> odsMethodList, String input, int startIndex, int endIndex) {
//    String var = "=$";
//    char varSplit = ',';
//    String methodStart = "(";
//    String methodEnd = ")";
//    boolean isTmp = input.charAt(startIndex) == 't';
//    int paramStartIndex = startIndex;
//    OdsMethod odsMethod = new OdsMethod();
//    for (int i = startIndex; i <= endIndex; i++) {
//      if (input.charAt(i) == varSplit) {
//        if (isTmp) {
//
//        } else {
//          List<String> paramNameList = odsMethod.getParamNameList();
//          paramNameList.add(input.substring(paramStartIndex, i));
//        }
//      } else {
//
//      }
//    }
//  }
//}
