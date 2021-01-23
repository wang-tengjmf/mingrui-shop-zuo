package com.baidu.shop.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.bto.BrandDTO;
import com.baidu.shop.entity.BrandEntity;
import com.baidu.shop.entity.CategoryBrandEntity;
import com.baidu.shop.mapper.BrandMapper;
import com.baidu.shop.mapper.CategoryBrandMapper;
import com.baidu.shop.service.BrandService;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.PinyinUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
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

    @Resource
    private CategoryBrandMapper categoryBrandMapper;

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

    @Override
    public Result<JSONObject> save(BrandDTO brandDTO) {
        brandDTO.setLetter(PinyinUtil.getUpperCase(String.valueOf(brandDTO.getName().charAt(0)), PinyinUtil.TO_FIRST_CHAR_PINYIN).charAt(0));
        try {
            BrandEntity brandEntity = BaiduBeanUtil.copyProperties(brandDTO,
                    BrandEntity.class);

            brandMapper.insertSelective(brandEntity);

            if(StringUtils.isEmpty(brandDTO.getCategories())){
                return this.setResultError("分类数据不能为空");
            }
            if(brandDTO.getCategories().contains(",")){
                List<CategoryBrandEntity> list = new ArrayList<>();
                String[] categoryArr = brandDTO.getCategories().split(",");
                Arrays.asList(categoryArr).stream().forEach(str -> {
                    CategoryBrandEntity categoryBrandEntity = new
                            CategoryBrandEntity();
                    categoryBrandEntity.setBrandId(brandEntity.getId());
                    categoryBrandEntity.setCategoryId(Integer.parseInt(str));
                    list.add(categoryBrandEntity);
                });
                categoryBrandMapper.insertList(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this.setResultSuccess();

    }
}
