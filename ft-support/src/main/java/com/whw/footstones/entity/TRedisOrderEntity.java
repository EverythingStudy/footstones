package com.whw.footstones.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 规则表
 * </p>
 *
 * @author chenlinya
 * @since 2021-10-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_redis_order")
public class TRedisOrderEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 规则城市
     */
    private String orderNum;

    /**
     * 操作时间
     */
    private LocalDateTime createTime;

    /**
     * 是否删除
     */
    private Boolean isDeleted;


}
