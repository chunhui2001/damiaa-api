package net.snnmo.dao;

import net.snnmo.entity.QrcodeEntity;

import java.util.Collection;

/**
 * Created by cc on 16/4/6.
 */
public interface IQrcodeDAO {
    // 保存二维码
    public int save(Collection<QrcodeEntity> qrcodeList);
    public QrcodeEntity list();
    public int set(long qrcodeId, String belongTo);
    public Collection<QrcodeEntity> random(int count);
    public QrcodeEntity get(long qrcodeid);
}
