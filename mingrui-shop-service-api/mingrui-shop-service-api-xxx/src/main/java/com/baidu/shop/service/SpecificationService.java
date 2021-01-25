package com.baidu.shop.service;

import com.baidu.shop.base.Result;
import com.baidu.shop.bto.SpecGroupDTO;
import com.baidu.shop.entity.SpecGroupEntity;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @ClassName SpecificationService
 * @Description: TODO
 * @Author wangteng
 * @Date 2021/1/25
 * @Version V1.0
 **/
@Api(tags = "规格接口")
public interface SpecificationService {
    @ApiOperation(value = "通过条件查询规格组")
    @GetMapping(value = "specgroup/getSpecGroupInfo")
    Result<List<SpecGroupEntity>> getSepcGroupInfo(SpecGroupDTO specGroupDTO);
}
