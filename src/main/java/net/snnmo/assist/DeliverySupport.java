package net.snnmo.assist;

import net.snnmo.exception.DbException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by TTong on 16-3-18.
 */
public class DeliverySupport {
    public static Map<DeliveryCompany, String> DeliveryNameMapping =
            new HashMap<DeliveryCompany, String>() {{
                put(DeliveryCompany.EMS, "EMS");
                put(DeliveryCompany.YOUZHENG, "邮政快递包裹");
                put(DeliveryCompany.SHENTONG, "申通快递");
                put(DeliveryCompany.YUNDA,"韵达快递");
                put(DeliveryCompany.YUANTONG,"圆通快递");
                put(DeliveryCompany.SHUNFENG,"顺丰快递");
                put(DeliveryCompany.ZHONGTONG,"中通快递");
            }};

    public static String getDeliveryName(DeliveryCompany company) throws DbException {
        if (!DeliveryNameMapping.containsKey(company)) {
            throw new DbException("快递公司名字未找到对应的映射!");
        }

        return DeliveryNameMapping.get(company);
    }
}
