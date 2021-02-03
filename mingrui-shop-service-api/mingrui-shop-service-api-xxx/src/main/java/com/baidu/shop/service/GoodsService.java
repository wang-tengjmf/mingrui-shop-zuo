package com.baidu.shop.service;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.Result;
import com.baidu.shop.bto.SkuDTO;
import com.baidu.shop.bto.SpuDTO;
import com.baidu.shop.entity.SpuDetailEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @ClassName GoodsService
 * @Description: TODO
 * @Author wangteng
 * @Date 2021/1/25
 * @Version V1.0
 **/
@Api(tags = "商品接口")
public interface GoodsService {
    @ApiOperation(value = "获取spu信息")
    @GetMapping(value = "goods/getSpuInfo")
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO);
    @ApiOperation(value = "新建商品")
    @PostMapping(value = "goods/add")
    Result<JSONObject> saveGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "修改商品")
    @PutMapping(value = "goods/save")
    Result<JSONObject> editGoods(@RequestBody SpuDTO spuDTO);

    @ApiOperation(value = "获取spu详情信息")
    @GetMapping(value = "goods/getSpuDetailBydSpu")
    public Result<SpuDetailEntity> getSpuDetailBydSpu(Integer spuId);
    @ApiOperation(value = "获取sku信息")
    @GetMapping(value = "goods/getSkuBySpuId")
    Result<List<SkuDTO>>getSkuBySpuId(Integer spuId);

    @ApiOperation(value = "删除商品")
    @DeleteMapping(value = "goods/del")
    Result<JSONObject> delGoods(Integer spuId);


}
