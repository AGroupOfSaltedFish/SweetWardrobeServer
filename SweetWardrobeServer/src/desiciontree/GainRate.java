package desiciontree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  �����������ݶ�������attrToDivide����Ϣ�������������
 *
 *  ��Ա����:
 *      function        ���Լ��в�ֵͬ�����Ӧ������ֵ�Ķ�Ӧ��ϵ
 *      attrToDivide    ������Ϣ������������ʵ���������
 */
class GainRate {
    private Map<Integer, List<Integer>> function;
    private String attrToDivide;

    private GainRate() {
        function = new HashMap<>();
    }


    GainRate(Map<String, List<Integer>> table, List<Integer> result,
             String attrToDivide) {
        this.attrToDivide = attrToDivide;
        List<Integer> line = table.get(attrToDivide);
        function = new HashMap<>();
        for(int i = 0; i < line.size(); ++i) {
            Integer category = Attribute.transferKey(line.get(i), attrToDivide);
            if(!function.containsKey(category)) {
                function.put(category, new ArrayList<>());
            }
            function.get(category).add(result.get(i));
        }
    }


    Double calculateGain() {
        List<Integer> validSample = new ArrayList<>();
        for(Map.Entry<Integer, List<Integer>> entry: function.entrySet()) {
            if(!Attribute.isVoidClothes(attrToDivide, entry.getKey())) {
                validSample.addAll(entry.getValue());
            }
        }
        Double gain = new EntD(validSample, attrToDivide).calculateEntD();
        for(Map.Entry<Integer, List<Integer>> entry: function.entrySet()) {
            if(!Attribute.isVoidClothes(attrToDivide, entry.getKey())) {
                gain -= new EntD(entry.getValue(), attrToDivide).calculateEntD();
            }
        }
        return gain;
    }
}
