package net.snnmo.assist;

/**
 * Created by cc on 16/2/14.
 */
public enum OrderStatus {
    PENDING,    // 刚刚创建 0
    DELETED,    // 已删除  1
    CANCEL,     // 已取消  2
    RECEIVED,   // 已签收 (已签收的订单一定是已经付款完毕的)   3
    CASHED,      // 已付款 4
    SENDED,      // 已发货 5
    //CREATE,
    INFO,
    //PAYMENT,
    SIGNED
}
