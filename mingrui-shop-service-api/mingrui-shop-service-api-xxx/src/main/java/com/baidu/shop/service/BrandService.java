package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.bto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @ClassName BrandService
 * @Description: TODO
 * @Author wangteng
 * @Date 2021/1/22
 * @Version V1.0
 **/

@Api(tags = "品牌接口")
public interface BrandService {
    @ApiOperation(value = "获取品牌信息")
    @GetMapping(value = "brand/getBrandInfo")
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO);
}
