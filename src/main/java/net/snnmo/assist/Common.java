package net.snnmo.assist;

import com.google.common.base.CharMatcher;
import com.google.gson.Gson;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import net.snnmo.dao.IOrderDAO;
import net.snnmo.entity.OrderEntity;
import org.apache.commons.beanutils.PropertyUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import javax.ws.rs.core.MediaType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by cc on 16/2/14.
 */
public class Common {


    public static String randomNumbers(int numberCount) {

        if (numberCount > 10 ) numberCount = 10;

        String rancomNumbers = null;

        do {

            rancomNumbers  = UUID.randomUUID().toString().replaceAll("[^0-9]+", "");

            if (rancomNumbers.indexOf('0') == 0)
                rancomNumbers = CharMatcher.is('0').trimFrom(rancomNumbers);

            if (rancomNumbers.length() >= numberCount) {
                rancomNumbers = rancomNumbers.substring(0, numberCount);
            }

        } while (rancomNumbers.length() < numberCount);

        return rancomNumbers;
    }

    public static boolean sendMail(String content) {
        // API Base URL:
        // https://api.mailgun.net/v3/mg.snnmo.com

        // API Key
        // key-a5179b50c49cbbea7aedcf1b12165d70

//        curl -s --user 'api:key-3ax6xnjp29jd6fds4gc373sgvjxteol0' \
//        https://api.mailgun.net/v3/mg.snnmo.com/messages \
//        -F from='Excited User <excited@samples.mailgun.org>' \
//        -F to='devs@mailgun.net' \
//        -F subject='Hello' \
//        -F text='Testing some Mailgun awesomeness!'

        // curl -s --user 'api:key-a5179b50c49cbbea7aedcf1b12165d70'
        // https://api.mailgun.net/v3/mg.snnmo.com/messages
        // -F from='Chunhui Zhang <mailgun@snnmo.com>'
        // -F to='18500183080@163.com'
        // -F to='chunhui2001@gmail.com'
        // -F subject='Hello Mailgun'
        // -F text='Testing some Mailgun awesomeness!'





        return true;
    }


    public static void SendSimpleMessage(String domainName, String apiKey, String username
            , String from, String[] to, String subject, String content, String contentType) {

//        Runnable task = () -> {
            Client client = Client.create();
            client.addFilter(new HTTPBasicAuthFilter("api", apiKey));

            WebResource webResource =
                    client.resource(
                            "https://api.mailgun.net/v3/mg." + domainName +
                                    "/messages");

            MultivaluedMapImpl formData = new MultivaluedMapImpl();

            formData.add("from", username + from == null ? " <mailgun@" + from + ">" : " <" + from + ">");

            for (String email : to) {
                formData.add("to", email);
            }

            formData.add("subject", subject);
            formData.add(contentType, content);

            ClientResponse res = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                    post(ClientResponse.class, formData);
//        };
//
//        Thread thread = new Thread(task);
//        thread.start();
    }


    public static Date addMinutesToDate(int minutes, Date date){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = date.getTime();
        return new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
    }



    public static Map<String, Object> processOrder(OrderEntity order, IOrderDAO orderDao)
            throws IllegalAccessException{

        if (order == null) return null;

        Map<String, Object> orderMap = null;

        Collection<OrderEntity> orderList = new ArrayList<>();

        orderList.add(order);

        Collection<Map<String, Object>> result = processOrderList(orderList, orderDao);

        orderMap = (Map<String, Object>)result.toArray()[0];

        return  orderMap;
    }


    public static Collection<Map<String, Object>> processOrderList(Collection<OrderEntity> orderList, IOrderDAO orderDao)
            throws IllegalAccessException{

        Collection<Map<String, Object>> orderListMap = new ArrayList<>();

        for (OrderEntity o : orderList) {
            Map<String, Object> currentMap  = new HashMap<>();
            Field[] attributes = o.getClass().getDeclaredFields();

            for (Field f : attributes) {

                try {

                    if (f.getName().toLowerCase().equals("createtime")) {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH点mm分");
                        currentMap.put(f.getName(), format.format(PropertyUtils.getProperty(o, f.getName())));
                    } else
                        currentMap.put(f.getName(), PropertyUtils.getProperty(o, f.getName()));

                    if (f.getName().toLowerCase().equals("status")) {
                        switch (PropertyUtils.getProperty(o, f.getName()).toString().toUpperCase()) {
                            case "PENDING":
                                currentMap.put("statusText", "待付款");
                                break;
                            case "CANCEL":
                                currentMap.put("statusText", "已取消");
                                break;
                            case "RECEIVED":
                                currentMap.put("statusText", "已签收");
                                break;
                            case "CASHED":
                                currentMap.put("statusText", "已付款");
                                break;
                            case "SENDED":
                                currentMap.put("statusText", "已发货");
                                break;
                            case "SIGNED":
                                currentMap.put("statusText", "已签收");
                                break;
                            default:
                                currentMap.put("statusText", PropertyUtils.getProperty(o, f.getName()));
                        }
                    }

                    if (f.getName().toLowerCase().equals("deliverycompany")) {

                        if (PropertyUtils.getProperty(o, f.getName()) == null) {
                            currentMap.put("deliveryCompanyName", null);
                        } else {
                            switch (PropertyUtils.getProperty(o, f.getName()).toString().toUpperCase()) {
                                case "EMS":
                                    currentMap.put("deliveryCompanyName", "邮政EMS");
                                    break;
                                case "YOUZHENG":
                                    currentMap.put("deliveryCompanyName", "邮政快递");
                                    break;
                                case "SHENTONG":
                                    currentMap.put("deliveryCompanyName", "申通快递");
                                    break;
                                case "YUNDA":
                                    currentMap.put("deliveryCompanyName", "韵达快递");
                                    break;
                                case "YUANTONG":
                                    currentMap.put("deliveryCompanyName", "圆通快递");
                                    break;
                                case "SHUNFENG":
                                    currentMap.put("deliveryCompanyName", "顺丰快递");
                                    break;
                                case "ZHONGTONG":
                                    currentMap.put("deliveryCompanyName", "中通快递");
                                    break;
                                default:
                                    currentMap.put("deliveryCompanyName", PropertyUtils.getProperty(o, f.getName()));
                            }
                        }
                    }

                } catch (NoSuchMethodException e) {

                } catch (InvocationTargetException e) {
                    // e.printStackTrace();
                }

            }

            currentMap.put("listOfItems", orderDao.items(o.getId()));

            orderListMap.add(currentMap);
        }

        return  orderListMap;
    }


    public static String UnifiedOrder(String url, OrderEntity orderEntity) throws Exception {

        Boolean isTest = "ofnVVw9aVxkxSfvvW373yuMYT7fs".equals(orderEntity.getOpenId());
        String endpoints_unified_order = url;//"http://localhost:8009/unifiedorder";
        Map<String, Object> params = new HashMap<>() ;

        params.put("body", "AA精米 特级米 现磨现卖");
        params.put("out_trade_no", orderEntity.getId());
        params.put("total_fee", isTest ? (1 + "") : (((int)orderEntity.getOrderMoney() * 100) + ""));
        params.put("userid", orderEntity.getUserId());
        params.put("openid", orderEntity.getOpenId());

        Map<String, Object> newParams = new HashMap<>();
        newParams.put("orderParams", params);

        String result = HttpClient.post(endpoints_unified_order, newParams, null);

        //{"error":false,"message":"创建预支付订单成功。",
        //"data":{"return_code":"SUCCESS","return_msg":"OK",
        // "appid":"wxbfbeee15bbe621e6","mch_id":"1315577401","nonce_str":"DrBvIFx8MXwajH93",
        // "sign":"C6D2007370207A4A242138B143543D72","result_code":"SUCCESS",
        // "prepay_id":"wx20160725234543b216f91f380164454830","trade_type":"JSAPI"}}

        Gson gson = new Gson();
        Map<String, Object> jsonResult = gson.fromJson(result, Map.class);

        ApiResult sendResult = new ApiResult();

        if ((Boolean)jsonResult.get("error")) {
            return null;
        }

        Map<String, Object> data = (Map<String, Object>)jsonResult.get("data");

//        sendResult.setData(data.get("prepay_id").toString());

        return data.get("prepay_id").toString();
    }
}
