package desiciontree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class EntD {
    private String attrName;
    private List<Integer> result;

    private EntD() {

    }

    /**
     *  �����������������Ӧ������ֵ�б�
     *
     *  @param  result      ����ֵ�б�
     *
     *  @param  attrName    ��Ӧ����������
     */
    EntD(List<Integer> result, String attrName) {
        this();
        this.attrName = attrName;
        this.result = result;
    }

    /**
     *  �Գ�Ա����attrName��resultΪ������
     *  �������������Ӧ��entDֵ��
     *
     *  @return ����������Ӧ��entDֵ��
     */
    Double calculateEntD() {
        Map<Integer, Integer> counter = new HashMap<>();
        for(Integer integer: result) {
            Integer key = Attribute.transferKey(integer,attrName);
            if(!counter.containsKey(key)) {
                counter.put(key, 0);
            }
            Integer times = counter.get(key);
            counter.put(key, times + 1);
        }

        Double entD = 0.0;
        for(Map.Entry<Integer, Integer> entry: counter.entrySet()) {
            Double pk = entry.getValue().doubleValue()/result.size();
            entD -= pk*(Math.log(pk)/Math.log(2));
        }
        return entD;
    }
}
