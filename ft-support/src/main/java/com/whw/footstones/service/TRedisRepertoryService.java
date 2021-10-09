package com.whw.footstones.service;

import com.whw.footstones.entity.TRedisRepertoryEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 库存表 服务类
 * </p>
 *
 * @author chenlinya
 * @since 2021-10-09
 */
public interface TRedisRepertoryService extends IService<TRedisRepertoryEntity> {
     void update(TRedisRepertoryEntity r);

}
