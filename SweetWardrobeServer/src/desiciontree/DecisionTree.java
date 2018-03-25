package desiciontree;

import database.*;
import desiciontree.TreeNode.*;

import java.io.*;
import java.util.*;

/**
 *  ������ģ��Ķ����࣬
 *  ���캯����userIdΪ������database�ж�ȡ�û���Ϣ��
 *  ͨ������studyAttribute������Ҫѧϰ�����Բ�������������
 *  ......
 *
 *  ��Ա������
 *      attrSelMode     ��ѷ�������ѡ��ģʽ(��δʵ��)
 *      table           <����-�б�>��ʽ�洢���ݵ�
 *      root            �������������ĸ��ڵ�
 *      attrList        ��̬�������б�
 */
public class DecisionTree {
    private Integer attrSelMode;    //��ѷ�������ѡ��ģʽ��0��ʾ����Ϣ����Ⱥ�����1��ʾ����Ϣ�����ʺ���
    private Map<String, List<Integer>> table;
    private Node root;

    private DecisionTree() {
        new Attribute();
        attrSelMode = 0;
    }

    /**
     *  ��userIdΪ�����������ݿ��ж�ȡ������ݣ�
     *  ����<����-�б�>����ʽ�洢
     *
     *  @param userId   �û�Id����Ϊ���������ݿ��ж�ȡ�������
     *
     */
    public DecisionTree(Integer userId) {
        this();
        Main userMain = new Main();
        UserInfo userData = userMain.getUserInfoById(userId);

        List<List<Integer>> weatherData = new ArrayList<>();
        List<List<Integer>> clothesData = new ArrayList<>();
        for(Weather weather: userData.getClothesInfo().getWeatherHistory()) {
            weatherData.add(weather.formatWeather());
        }

        for(Suit suit: userData.getClothesInfo().getSuitHistory()) {
            clothesData.add(suit.getClothesIdList());
        }

        List<List<Integer>> data = null;
        try {
            List<List<Integer>> data1 = deepCopy(weatherData);
            List<List<Integer>> data2 = deepCopy(clothesData);
            data = data1;
            assert data1.size() == data2.size();
            for(int i = 0; i < data.size(); ++i) {
                data.get(i).addAll(data2.get(i));
            }

        } catch(Exception e) {
            e.printStackTrace();
            assert false;
        }

        table = listToMap(data);
    }

    public DecisionTree(Integer userId, Integer attrSelMode) {
        this(userId);
        this.attrSelMode = attrSelMode;
    }

    public void setAttrSelMode(Integer attrSelMode) {
        this.attrSelMode = attrSelMode;
    }

    public Integer recommandation(String attrToLearn, Map<String, Integer> params) {
        root = buildTree(attrToLearn);
        Node p = root;
        while(!p.isLeaf()) {
            RootNode pr = (RootNode)p;
            String divideAttr = pr.getDivideAttr();
            Integer data = params.get(divideAttr);
            Integer key = Attribute.transferKey(data, divideAttr);
            p = pr.accessSubTree(key);
        }
        return ((LeafNode)p).getClothesId();
    }

    /**
     * ��������������root���и�ֵ
     *
     * @param   attrName   ��Ҫѧϰ����������
     *
     * @return  �����ɵľ��������ڵ�
     *
     */
    private Node buildTree(String attrName) {
        assert Attribute.isClothes(attrName);
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.buildTreeRecursion();
    }

    /**
     *  ��ByteArray�ķ���ʵ��Java����ȿ���
     *  ��Ҫ��try...catch...������ܳ��ֵ��쳣
     *
     *  @param  src     ��Ҫ��������List
     *
     *  @return ������õ��µ�List������
     *
     */
    private static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }


    /**
     *  ��weather��clothes�йص����ݸı���֯��ʽ��
     *  ��<����-�б�>�Ķ�Ӧ��洢����
     *
     *  @param  data    ����������������йص������б�
     *
     *  @return <����-�б�>�Ķ�Ӧͼ�ṹ
     *
     */
    private static Map<String, List<Integer>> listToMap(List<List<Integer>> data) {
        Map<String, List<Integer>> table = new HashMap<>();
        for(String name: Attribute.attrList) {
            table.put(name, new ArrayList<>());
        }

        for(List<Integer> line: data) {
            for(int i = 0; i < line.size(); ++i) {
                table.get(Attribute.attrList.get(i)).add(line.get(i));
            }
        }
        return table;
    }

    /**
     *  �ֶ�����weather��clothes�е����ݣ����ڲ���
     */
    private void setTable() {
        table = new HashMap<>();
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter lines of data: ");
        Integer lines = sc.nextInt();
        for(String attrName: Attribute.attrList) {
            System.out.print("Enter " + attrName + ": ");
            table.put(attrName, new ArrayList<>());
            for(int i = 0; i < lines; ++i) {
                table.get(attrName).add(sc.nextInt());
            }
        }
    }

    /**
     *  �����û��������ƣ�ƥ����������������������ԣ�
     *  ���ڶ�attrTree�з���selectDivideAttr�Ĳ���
     *
     *  @param   attrName    ����ƥ����û���������
     *
     *  @return  ������attrName�������������
     */
    private String experiment(String attrName) {
        AttributeTree attrTree = new AttributeTree(table, attrName);
        return attrTree.selectDivideAttr();
    }

    public static void main(String[] args) {
        DecisionTree tree = new DecisionTree();
        System.out.print("��������Ҫѧϰ������: ");
        String attrToLearn = new Scanner(System.in).next();
        tree.setTable();
        String result = tree.experiment(attrToLearn);
        System.out.println("ѡ������: " + result);
    }
}