package com.baidu.shop.service.impl;

import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.bto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.service.BrandService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName BrandServiceImpl
 * @Description: TODO
 * @Author wangteng
 * @Date 2021/1/22
 * @Version V1.0
 **/
@RestController

public class BrandServiceImpl extends BaseApiService implements BrandService{
    @Resource
    private BrandMapper brandMapper;

    @Override
    public Result<PageInfo<BrandEntity>> getBrandInfo(BrandDTO brandDTO) {

        PageHelper.startPage(brandDTO.getPage(),brandDTO.getRows());

        Example example = new Example(BrandEntity.class);
        if(!StringUtils.isEmpty(brandDTO.getOrder()))example.setOrderByClause(brandDTO.getOrderBy());
        if(!StringUtils.isEmpty(brandDTO.getName())){
            example.createCriteria().andLike("name","%" + brandDTO.getName() +
                    "%");
        }

        List<BrandEntity> list = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> pageInfo = new PageInfo<>(list);

        List<BrandEntity> list1 = brandMapper.selectByExample(example);
        PageInfo<BrandEntity> pageInfo1= new PageInfo<>(list1);
        return this.setResultSuccess(pageInfo1);
    }
}
