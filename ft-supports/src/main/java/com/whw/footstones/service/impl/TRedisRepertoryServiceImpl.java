package com.whw.footstones.service.impl;

import com.whw.footstones.entity.TRedisRepertoryEntity;
import com.whw.footstones.dao.TRedisRepertoryMapper;
import com.whw.footstones.service.TRedisRepertoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 库存表 服务实现类
 * </p>
 *
 * @author chenlinya
 * @since 2021-10-09
 */
@Service
public class TRedisRepertoryServiceImpl extends ServiceImpl<TRedisRepertoryMapper, TRedisRepertoryEntity> implements TRedisRepertoryService {
    @Autowired
    TRedisRepertoryMapper tRedisRepertoryMapper;

    @Override
    public void update(TRedisRepertoryEntity r) {
        tRedisRepertoryMapper.updateById(r);
    }

    @Override
    public TRedisRepertoryEntity selectById(Long id) {
        return tRedisRepertoryMapper.selectById(id);
    }
}
