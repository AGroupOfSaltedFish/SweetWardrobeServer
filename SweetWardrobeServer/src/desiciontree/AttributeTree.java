package desiciontree;

import desiciontree.TreeNode.LeafNode;
import desiciontree.TreeNode.Node;
import desiciontree.TreeNode.RootNode;

import java.util.*;

/**
 *  ��DecisionTree����ã�
 *  ���캯�������������ݼ�ѧϰ���������Ժ�
 *  ��ȡ����Ҫѧϰ��������
 *  ͨ���ݹ鷽��buildTreeRecursion������������
 *
 *  ��Ա������
 *      table   �洢<����-����>��
 *      result  ��Ҫѧϰ�������б�
 */
class AttributeTree {
    private Map<String, List<Integer>> table;
    private List<Integer> result;

    private AttributeTree() {
        table = new HashMap<>();
        result = new ArrayList<>();
    }

    /**
     *  ���ݶ�����DecisionTree������<����-����>ͼ��
     *  ��һ����ѡ����Ҫѧϰ����������result��
     *
     *  @param  table       ������<����-����>�洢ͼ��
     *
     *  @param  attrToLearn ѧϰ���û��������ƣ�
     */
    AttributeTree(Map<String, List<Integer>> table, String attrToLearn) {
        result = table.get(attrToLearn);
        table.remove(attrToLearn);
        this.table = table;
    }

    /**
     *  �Եݹ����ʽ������������
     *  ��ѧϰ�����Ѿ��㹻�������������ѧϰ��ɣ��򷵻�Ҷ�ڵ㣬
     *  ����ݹ�ط������������������ڵ㡣
     *
     *  @return ���������������ĸ��ڵ�Node
     */
    Node buildTreeRecursion() {
        Boolean resultSame = true;
        for(int i = 0; i < result.size() - 1; ++i) {
            if(!result.get(i).equals(result.get(i + 1))) {
                resultSame = false;
                break;
            }
        }
        if(resultSame) {
            return new LeafNode(result.get(0));
        }

        if(table.keySet().isEmpty()) {
            return new LeafNode(findMaxResult());
        }
        Boolean attrSame = true;
        for(String attrName: table.keySet()) {
            List<Integer> line = table.get(attrName);
            for(int i = 0; i < line.size() - 1; ++i) {
                if(!line.get(i).equals(line.get(i + 1))) {
                    attrSame = false;
                    break;
                }
            }
        }
        if(attrSame) {
            return new LeafNode(findMaxResult());
        }

        String attrName = selectDivideAttr();
        assert attrName != null;
        System.out.println("��ѻ�������Ϊ: " + attrName);
        System.out.print("ʣ���������Ϊ: ");
        for(String name: table.keySet()) {
            System.out.print(name + "\t");
        }
        System.out.println();

        RootNode node = new RootNode(attrName);
        Map<Integer, AttributeTree> function = divideSample(attrName);
        for(Integer value: function.keySet()) {
            node.addSubTree(value, function.get(value).buildTreeRecursion());
        }
        return node;
    }

    /**
     *  �ҳ�����ѧϰ�����е�������������
     *
     *  @return ������������������Result�е�����
     */
    private Integer findMaxResult() {
        Map<Integer, Integer> counter = new HashMap<>();
        for(Integer integer: result) {
            if(counter.containsKey(integer)) {
                Integer value = counter.get(integer) + 1;
                counter.put(integer, value);
            }
            else {
                counter.put(integer, 1);
            }
        }

        Integer flag = -1, max = -1;
        for(int i = 0; i < result.size(); ++i) {
            if(counter.get(result.get(i)) > max) {
                max = counter.get(result.get(i));
                flag = i;
            }
        }
        return result.get(flag);
    }

    /**
     *  �ӵ�ǰ�������ҳ���Ϣ���������������ߵ����ԣ�
     *  ����������ѧϰ���������������ߵ��������ơ�
     *
     *  @return ������ѻ�����������
     */
    String selectDivideAttr() {
        Map<String, Double> record = new HashMap<>();
        for(String attrToDivide: table.keySet()) {
            GainRate gain = new GainRate(table, result, attrToDivide);
            record.put(attrToDivide, gain.calculateGain());
        }

        Double maxValue = Collections.max(record.values());
        String selectedAttr = null;
        for(Map.Entry<String, Double> entry: record.entrySet()) {
            if(entry.getValue().equals(maxValue)) {
                selectedAttr = entry.getKey();
            }
        }
        return selectedAttr;
    }

    /**
     *  ��ѧϰ����result�����������
     *  private�����������ڵݹ�����ж��Ӷ������������ӡ�
     *
     *  @param  sample  ��Ҫ��ӵ���������
     */
    private void addToResult(Integer sample) {
        result.add(sample);
    }

    /**
     *  �����Լ�table������������ݣ�
     *  private�����������ڵݹ�����ж��Ӷ�������������ݵ���ӡ�
     *
     *  @param  superTable      �������������superTable
     *
     *  @param  attrToIgnore    ����Ҫ�������Ӷ���table�е���������attrToIgnore
     *
     *  @param  index           Ϊ�˱��ڵ�������ȡ��indexΪѭ�������𲽽����ݼ���superTable��
     */
    private void addToTable(Map<String, List<Integer>> superTable,
                            String attrToIgnore, Integer index) {
        for(String attr: table.keySet()) {
            if(!attr.equals(attrToIgnore)) {
                table.get(attr).add(superTable.get(attr).get(index));
            }
        }
    }

    /**
     *  ���ݲ���-��ѻ����������ƣ�
     *  ���������ݻ���Ϊ������֣�
     *  �����ָ������ݷ�װ���Ӷ���
     *  ����<ֵ-����>��Ӧͼ��
     *
     *  @param  attrName    ��ѻ�����������
     *
     *  @return ����ѻ�������Ϊ���ݵ�<ֵ-�Ӷ���>��Ӧͼ
     */
    private Map<Integer, AttributeTree> divideSample(String attrName) {
        List<Integer> line = table.get(attrName);
        Map<Integer, AttributeTree> function = new HashMap<>();
        for(int i = 0; i < line.size(); ++i) {
            Integer key = Attribute.transferKey(line.get(i), attrName);
            if(!function.containsKey(key)) {
                function.put(key, new AttributeTree());
            }
            function.get(key).addToResult(result.get(i));
            function.get(key).addToTable(this.table, attrName, i);
        }
        return function;
    }
}
