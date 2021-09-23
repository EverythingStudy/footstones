package com.whw.footstones.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author chenlinya
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_order")
public class TOrder0Entity implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long orderId;

    private String orderNo;

    private String createName;

    private BigDecimal price;


}
