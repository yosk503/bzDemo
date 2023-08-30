package com.example.demo.util.bzUtil;

import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class AllotApplyId {
    public static void main(String[] args) {
        urgentAllot("02",5);
    }

    /**
     * 开始分单
     */
    public static void urgentAllot(String type,int fixedNumber){
        //封装成以 客户为key，是否参与过分单和单据为value的map
        Map<String, Map<String, List<String>>> applyListMap = getApplyId();
        //以applyId为主键 客户名以及是否已经分单作为value
        Map<String, Map<String, String>> applyIdMap = getApplyIdMap(applyListMap);
        //数据库中每个用户的单据总量
        Map<String, Integer> nowHaveCountMap = getNowHaveCount();
        //1.固定值分配 直接获取固定值
        //2.平均后分配  计算平均值只获取整数部分
        //3.分配后平均  计算平均值只获取整数部分
        int average = getAverage(nowHaveCountMap, applyIdMap.size(), nowHaveCountMap.size(), type, fixedNumber);
        //分配之后每个用户应该达到的预估值
        Map<String, Integer> predictCountMap = getFixed(nowHaveCountMap, average, type);
        Map<String, Map<String, List<String>>> finalMap = new HashMap<>();
        for (String key : nowHaveCountMap.keySet()) {
            Map<String, List<String>> map = new HashMap<>();
            List<String> historyCountList = new ArrayList<>();
            historyCountList.add(nowHaveCountMap.get(key).toString());
            map.put("historyCount", historyCountList);
            map.put("applyId", new ArrayList<>());
            finalMap.put(key, map);
        }
        for (int i = 0; i < applyListMap.size(); i++) {
            //获取单量最多 且还参与分配的客户 此处的分配指的是参与，并不一定能分出去
            String custCode = getMaxCust(applyListMap);
            List<String> allotFlagList = new ArrayList<>();
            allotFlagList.add("true");
            applyListMap.get(custCode).put("allotFlag", allotFlagList);
            //获取finalMap单量最少的分配人员 核心算法
            String allot = getAllotFixed(finalMap, i + 1, applyListMap.get(custCode).get("applyId").size(), predictCountMap);
            if (StringUtils.isNotEmpty(allot)) {
                //不为空就要分配，且将状态改为已分配
                List<String> applyIdList = applyListMap.get(custCode).get("applyId");
                finalMap.get(allot).get("applyId").addAll(applyListMap.get(custCode).get("applyId"));
                for (String key : applyIdList) {
                    applyIdMap.get(key).put("allotFlag", "true");
                }
            }
        }
        //上面主要处理相同客户需要分配给同一个排班人员 能分配多少就分配多少
        //下面针对不同的算法分别进行处理，不在管是否同一个客户，只以单据作为主体
        Map<String, List<Map<String, String>>> resultMap = new HashMap<>();
        if ("00".equals(type)) {
            //1.固定值分配 固定值 就是刚开始设置的数值
            resultMap = getFixedAllotMap(finalMap, applyIdMap, average);
        } else if ("01".equals(type)) {
            //2.平均后分配 这里传递的平均值只传递整数部分
            resultMap = getBeforeAverageResult(finalMap, applyIdMap, average);
        } else if ("02".equals(type)) {
            //3.分配后平均
            resultMap = getAfterAverageResult(finalMap, applyIdMap, average);
        }
        resultMap.forEach((key, list) -> {
            System.out.println("Key: " + key);
            System.out.println("List: ");
            list.stream()
                    .map(innerMap -> innerMap.entrySet().stream()
                            .map(innerEntry -> innerEntry.getKey() + ": " + innerEntry.getValue())
                            .collect(Collectors.joining(", ")))
                    .forEach(System.out::println);
            System.out.println();
        });
    }

    /**
     * 获取以单据为key的map
     */
    public static Map<String, Map<String, String>> getApplyIdMap(Map<String, Map<String, List<String>>> applyMap) {
        Map<String, Map<String, String>> returnMap = new HashMap<>();
        for (String key : applyMap.keySet()) {
            List<String> list = applyMap.get(key).get("applyId");
            for (String s : list) {
                Map<String, String> map = new HashMap<>();
                map.put("custCode", key);
                map.put("allotFlag", "false");
                returnMap.put(s, map);
            }
        }
        return returnMap;
    }

    /**
     * 获取当前还未分单的单据
     */
    public static Map<String, String> getNotAllotApply(Map<String, Map<String, String>> applyIdMap) {
        Map<String, String> map = new HashMap<>();
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("allotFlag");
            if ("false".equals(allotFlag)) {
                map.put(key, applyIdMap.get(key).get("custCode"));
            }
        }
        return map;
    }

    /**
     * 根据分单规则第一次获取平均值
     */
    public static int getAverage(Map<String, Integer> nowHaveCountMap, int count, int averageNum, String type, int fixedNumber) {
        //固定值
        int average = 0;
        if (averageNum != 0) {
            if ("00".equals(type)) {
                //直接查数据库
                average = fixedNumber;
            } else if ("01".equals(type)) {
                //平均后分配
                average = count / averageNum;
            } else if ("02".equals(type)) {
                //分配后平均
                int nowCount = 0;
                for (String key : nowHaveCountMap.keySet()) {
                    nowCount = nowCount + nowHaveCountMap.get(key);
                }
                average = (count + nowCount) / averageNum;
            }
        }
        return average;
    }

    /**
     * 固定值分配
     */
    public static Map<String, List<Map<String, String>>> getFixedAllotMap(Map<String, Map<String, List<String>>> finalMap, Map<String, Map<String, String>> applyIdMap, int averageCount) {
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("allotFlag");
            if ("false".equals(allotFlag)) {
                for (String allot : finalMap.keySet()) {
                    int count = finalMap.get(allot).get("applyId").size();
                    if (count < averageCount) {
                        List<String> list = finalMap.get(allot).get("applyId");
                        list.add(key);
                        applyIdMap.get(key).put("allotFlag", "true");
                        break;
                    }
                }
            }
        }
        return getFinallyMapResult(finalMap, applyIdMap);
    }

    /**
     * 平均后分配 先平均在分配，批次平均
     * 重新计算还需要分配的排班人员有几个，还未分配的单据有几个
     * 第一次分配：将未分配的单据直接给单据量最少的用户，且分配完以后的单据量不能超过重新计算后的平均值
     * 第二次分配：将未分配的单据直接给单据量最少的用户，且分配完以后的单据量不能超过重新计算后的平均值+1，防止平均值只取了整数部分，导致单据没有分配完
     */
    public static Map<String, List<Map<String, String>>> getBeforeAverageResult(Map<String, Map<String, List<String>>> finalMap, Map<String, Map<String, String>> applyIdMap, int average) {
        //将未分配的单据直接给单据量最少的用户
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("applyId");
            if ("false".equals(allotFlag)) {
                String allot = getAllot(finalMap, 1, average);
                if (StringUtils.isNotEmpty(allot)) {
                    List<String> applyIdList = finalMap.get(allot).get("applyId");
                    applyIdList.add(key);
                    finalMap.get(allot).put("applyId", applyIdList);
                    applyIdMap.get(key).put("allotFlag", "true");
                }
            }
        }
        //分配所有未分配的单据
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("allotFlag");
            if ("false".equals(allotFlag)) {
                String allot = getAllot(finalMap, 1, average + 1);
                if (StringUtils.isNotEmpty(allot)) {
                    List<String> applyIdList = finalMap.get(allot).get("applyId");
                    applyIdList.add(key);
                    finalMap.get(allot).put("applyId", applyIdList);
                    applyIdMap.get(key).put("allotFlag", "true");
                }
            }
        }
        return getFinallyMapResult(finalMap, applyIdMap);
    }

    /**
     * 分配后平均 全部包括历史都实现分配后的结果平均
     * 重新计算还需要分配的排班人员有几个，还未分配的单据有几个
     * 第一次分配：将未分配的单据直接给单据量最少的用户，且分配完以后的单据量不能超过重新计算后的平均值
     * 第二次分配：将未分配的单据直接给单据量最少的用户，且分配完以后的单据量不能超过重新计算后的平均值+1，防止平均值只取了整数部分，导致单据没有分配完
     */
    public static Map<String, List<Map<String, String>>> getAfterAverageResult(Map<String, Map<String, List<String>>> finalMap, Map<String, Map<String, String>> applyIdMap, int average) {


        //每个人应该达到的单据总数量
        //将未分配的单据直接给单据量最少的用户
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("applyId");
            if ("false".equals(allotFlag)) {
                String allot = getAllotTwo(finalMap, 1, average);
                if (StringUtils.isNotEmpty(allot)) {
                    List<String> applyIdList = finalMap.get(allot).get("applyId");
                    applyIdList.add(key);
                    finalMap.get(allot).put("applyId", applyIdList);
                    applyIdMap.get(key).put("allotFlag", "true");
                }
            }
        }
        //分配所有未分配的单据
        for (String key : applyIdMap.keySet()) {
            String allotFlag = applyIdMap.get(key).get("allotFlag");
            if ("false".equals(allotFlag)) {
                String allot = getAllotTwo(finalMap, 1, average + 1);
                if (StringUtils.isNotEmpty(allot)) {
                    List<String> applyIdList = finalMap.get(allot).get("applyId");
                    applyIdList.add(key);
                    finalMap.get(allot).put("applyId", applyIdList);
                    applyIdMap.get(key).put("allotFlag", "true");
                }
            }
        }

        return getFinallyMapResult(finalMap, applyIdMap);
    }

    /**
     * 封装返回分单成功以及分单失败的单据
     * 对应的分配人，客户编号，单据编号
     */
    public static Map<String, List<Map<String, String>>> getFinallyMapResult(Map<String, Map<String, List<String>>> finalMap, Map<String, Map<String, String>> applyIdMap) {
        Map<String, List<Map<String, String>>> returnMap = new HashMap<>();
        List<Map<String, String>> successList = new ArrayList<>();
        List<Map<String, String>> failList = new ArrayList<>();
        for (String key : finalMap.keySet()) {
            List<String> list = finalMap.get(key).get("applyId");
            for (int i = 0; i < list.size(); i++) {
                Map<String, String> containsMap = new HashMap<>();
                containsMap.put("custCode", applyIdMap.get(list.get(i)).get("custCode"));
                containsMap.put("allot", key);
                containsMap.put("applyId", list.get(i));
                successList.add(containsMap);
            }
        }
        for (String key : applyIdMap.keySet()) {
            String custCode = applyIdMap.get(key).get("custCode");
            String allotFlag = applyIdMap.get(key).get("allotFlag");
            if ("false".equals(allotFlag)) {
                Map<String, String> containsMap = new HashMap<>();
                containsMap.put("custCode", custCode);
                containsMap.put("allot", "");
                containsMap.put("applyId", key);
                failList.add(containsMap);
            }
        }
        returnMap.put("success", successList);
        returnMap.put("fail", failList);
        return returnMap;
    }

    /**
     * 获取多个客户在该批次单据量最多的客户
     * 并且该客户还未参与过分单，也可能是参与了，但没有分配出去（参与了，但是选择不到合适的排班人员）
     */
    public static String getMaxCust(Map<String, Map<String, List<String>>> applyMap) {
        String maxKey = "";
        int maxValue = 0;
        for (String key : applyMap.keySet()) {
            Map<String, List<String>> map = applyMap.get(key);
            String allotFlag = map.get("allotFlag").get(0);
            List<String> list = map.get("applyId");
            int size = list.size();
            if (size > maxValue && allotFlag.equals("false")) {
                maxValue = size;
                maxKey = key;
            }
        }
        return maxKey;

    }

    /**
     * 根据分配到排班人员手中的单量，选择单量最少的排班人员
     */
    public static String getAllotTwo(Map<String, Map<String, List<String>>> finalMap, int number, int integerNum) {
        String minKey = null;
        int minCount = Integer.MAX_VALUE;
        for (String key : finalMap.keySet()) {
            int count = finalMap.get(key).get("applyId").size();
            int historyCount = Integer.parseInt(finalMap.get(key).get("historyCount").get(0));
            if (count < minCount && (number + count <= integerNum - historyCount)) {
                minCount = count;
                minKey = key;
            }
        }
        return minKey;
    }

    /**
     * 根据分配到排班人员手中的单量，选择单量最少的排班人员
     */
    public static String getAllot(Map<String, Map<String, List<String>>> finalMap, int number, int integerNum) {
        String minKey = null;
        int minCount = Integer.MAX_VALUE;
        for (String key : finalMap.keySet()) {
            int count = finalMap.get(key).get("applyId").size();
            if (count < minCount && (number + count <= integerNum)) {
                minCount = count;
                minKey = key;
            }
        }
        return minKey;
    }

    /**
     * 需要获取  哪个分配人手中目前差的单据量最多且相加不会超过的
     */
    public static String getAllotFixed(Map<String, Map<String, List<String>>> finalMap, int i, int number, Map<String, Integer> predictCountMap) {
        String maxKey = "";
        int maxValue = 0;
        for (String key : finalMap.keySet()) {
            Map<String, List<String>> map = finalMap.get(key);
            int historyCount = Integer.parseInt(map.get("historyCount").get(0));
            int nowCount = map.get("applyId").size();
            int count = predictCountMap.get(key) - historyCount - nowCount; //相差的单据量
            if (count > 0 && count >= maxValue && i < predictCountMap.size()) {
                maxValue = count;
                maxKey = key;
            } else if (count >= 0 && number + nowCount + historyCount <= predictCountMap.get(key)) {
                maxValue = count;
                maxKey = key;
            }
        }
        return maxKey;
    }

    /**
     * 获取每个分配人的固定值
     */
    public static Map<String, Integer> getFixed(Map<String, Integer> nowHaveCountMap, int average, String type) {
        Map<String, Integer> map = new HashMap<>();
        if ("02".equals(type)) {
            for (String key : nowHaveCountMap.keySet()) {
                map.put(key, average);
            }
        } else {
            for (String key : nowHaveCountMap.keySet()) {
                map.put(key, nowHaveCountMap.get(key) + average);
            }
        }
        return map;
    }

    /**
     * 当前数据库中具有的单据总量
     */
    public static Map<String, Integer> getNowHaveCount() {
        Map<String, Integer> map = new HashMap<>();
        map.put("张三", 10);
        map.put("李四", 6);
        map.put("王五", 3);
        map.put("赵六", 1);
        return map;
    }

    /**
     * 测试生成数据
     */
    public static Map<String, Map<String, List<String>>> getApplyId() {
        Map<String, Map<String, List<String>>> returnMap = new HashMap<>();
        // 客户1
        returnMap.put("客户1", createMapWithApplyIdList("DKSQ123", "DKSQ124", "DKSQ125", "DKSQ126", "DKSQ127", "DKSQ128", "DKSQ129", "DKSQ130", "DKSQ131"));
        // 客户2
        returnMap.put("客户2", createMapWithApplyIdList("DKSQ223", "DKSQ224", "DKSQ225", "DKSQ226", "DKSQ227", "DKSQ228", "DKSQ229"));
        // 客户3
        returnMap.put("客户3", createMapWithApplyIdList("DKSQ323", "DKSQ324", "DKSQ325"));
        // 客户4
        returnMap.put("客户4", createMapWithApplyIdList("DKSQ423", "DKSQ424", "DKSQ425"));
        // 客户5
        returnMap.put("客户5", createMapWithApplyIdList("DKSQ523", "DKSQ524", "DKSQ525"));
        // 客户6
        returnMap.put("客户6", createMapWithApplyIdList("DKSQ623", "DKSQ624"));
        // 客户7
        returnMap.put("客户7", createMapWithApplyIdList("DKSQ723", "DKSQ724"));
        // 客户7
        returnMap.put("客户8", createMapWithApplyIdList("DKSQ823", "DKSQ824"));
        // 客户7
        returnMap.put("客户9", createMapWithApplyIdList("DKSQ923", "DKSQ924"));
        // 客户7
        returnMap.put("客户10", createMapWithApplyIdList("DKSQ1023", "DKSQ1024"));
        return returnMap;
    }

    /**
     * 测试生成数据的公共方法
     */
    private static Map<String, List<String>> createMapWithApplyIdList(String... applyIds) {
        Map<String, List<String>> map = new HashMap<>();
        List<String> applyIdList = new ArrayList<>();
        Collections.addAll(applyIdList, applyIds);
        map.put("applyId", applyIdList);
        map.put("allotFlag", Collections.singletonList("false"));
        return map;
    }


}
