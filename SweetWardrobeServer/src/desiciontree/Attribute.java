package desiciontree;

import database.Clothes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Attribute {
    static final List<String> attrList = new ArrayList<>();

    /**
     *  ��ʼ����̬�������������б�
     */
    Attribute() {
        Collections.addAll(attrList, "����", "����¶�", "����¶�",
                "���ʪ��", "���ʪ��", "������", "��С����",
                "����", "����", "��װ", "Ь��");
    }

    /**
     *  �ж�attrName�����ǲ�����������ֵ��
     *
     *  @param  attrName    ��Ҫ�жϵ���������
     *
     *  @return ���ǣ�����True;���򣬷���False
     */
    static private Boolean successValue(String attrName) {
        return (attrName.equals("����¶�") || attrName.equals("����¶�") ||
                attrName.equals("���ʪ��") || attrName.equals("���ʪ��") ||
                attrName.equals("������") || attrName.equals("��С����"));
    }

    /**
     *  ������ֵ����һ��������д���
     *  ������������ֵ���л��֡�
     *
     *  @param  integer     ��Ҫ���������ֵ
     *
     *  @param  attrName    ��Ӧ����������
     *
     *  @return ���ش���֮�������ֵ
     */
    static Integer transferKey(Integer integer, String attrName) {
        Integer key;
        if(!Attribute.successValue(attrName)) {
            key = integer;
        }
        else {
            Integer mod;
            switch(attrName) {
                case "����¶�": case "����¶�": mod = 5; break;
                case "���ʪ��": case "���ʪ��": mod = 20; break;
                case "������": case "��С����": mod = 3; break;
                default: mod = 0;
            }
            key = integer - integer % mod;
        }
        return key;
    }

    static Boolean isClothes(String attrName) {
        return attrName.equals("����") || attrName.equals("����") ||
                attrName.equals("��װ") || attrName.equals("Ь��");
    }

    static Boolean isVoidClothes(String attrName, Integer index) {
        return isClothes(attrName) && index.equals(Clothes.defaultClothes);
    }
}
