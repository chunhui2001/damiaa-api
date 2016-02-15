package net.snnmo.dao;

import net.snnmo.assist.PayMethod;
import net.snnmo.entity.AddressEntity;
import net.snnmo.entity.GoodsEntity;
import net.snnmo.entity.OrderEntity;
import net.snnmo.entity.UserEntity;

import java.util.Map;

/**
 * Created by cc on 16/2/15.
 */
public interface IOrderDAO {
    public OrderEntity create(UserEntity user, PayMethod payMethod, AddressEntity addr
            , Map<GoodsEntity, Integer> goodsList) ;
}
