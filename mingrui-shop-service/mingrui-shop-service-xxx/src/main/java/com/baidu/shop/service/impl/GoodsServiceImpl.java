package com.baidu.shop.service.impl;
import com.alibaba.fastjson.JSONObject;
import com.baidu.shop.base.BaseApiService;
import com.baidu.shop.base.Result;
import com.baidu.shop.bto.SkuDTO;
import com.baidu.shop.bto.SpuDTO;
import com.baidu.shop.entity.*;
import com.baidu.shop.mapper.*;
import com.baidu.shop.service.GoodsService;
import com.baidu.shop.status.HTTPStatus;
import com.baidu.shop.utils.BaiduBeanUtil;
import com.baidu.shop.utils.ObjectUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;
/**
 * @ClassName GoodsServiceImpl
 * @Description: TODO
 * @Author wangteng
 * @Date 2021/1/25
 * @Version V1.0
 **/
@RestController
public class GoodsServiceImpl extends BaseApiService implements GoodsService{
    @Resource
    private SpuMapper spuMapper;
    @Resource
    private BrandMapper brandMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private SpuDetailMapper spuDetailMapper;
    @Resource
    private SkuMapper skuMapper;
    @Resource
    private StockMapper stockMapper;
    @Override
    public Result<List<SpuDTO>> getSpuInfo(SpuDTO spuDTO) {
        if(spuDTO.getPage() != null && spuDTO.getRows() != null)
            PageHelper.startPage(spuDTO.getPage(),spuDTO.getRows());
        Example example = new Example(SpuEntity.class);
        Example.Criteria criteria = example.createCriteria();
        if (ObjectUtil.isNotNull(spuDTO)) {

            if(!StringUtils.isEmpty(spuDTO.getTitle()))
                criteria.andLike("title","%" + spuDTO.getTitle() + "%");

            if (ObjectUtil.isNotNull(spuDTO.getSaleable()) &&
                    spuDTO.getSaleable() != 2)
                criteria.andEqualTo("saleable",spuDTO.getSaleable());

            if (!StringUtils.isEmpty(spuDTO.getSort()))
                example.setOrderByClause(spuDTO.getOrder());
        }
        List<SpuEntity> list = spuMapper.selectByExample(example);
        List<SpuDTO> dtos = list.stream().map(spu -> {
            BrandEntity brandEntity =
                    brandMapper.selectByPrimaryKey(spu.getBrandId());
            String categoryNames =
                    categoryMapper.selectByIdList(Arrays.asList(spu.getCid1(), spu.getCid2(),
                            spu.getCid3()))
                            .stream()
                            .map(category -> category.getName())
                            .collect(Collectors.joining("/"));
            SpuDTO dto = BaiduBeanUtil.copyProperties(spu, SpuDTO.class);
            dto.setBrandName(brandEntity.getName());
            dto.setCategoryName(categoryNames);
            return dto;
        }).collect(Collectors.toList());
        PageInfo<SpuEntity> pageInfo = new PageInfo<>(list);
        long total = pageInfo.getTotal();
        return this.setResult(HTTPStatus.OK,total + "",dtos);
    }
    @Override
    public Result<JSONObject> saveGoods(SpuDTO spuDTO) {
        System.out.println(spuDTO);
//新增spu
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO,
                SpuEntity.class);
        spuEntity.setSaleable(1);
        spuEntity.setValid(1);
        final Date date = new Date();//保持两个时间一致
        spuEntity.setCreateTime(date);
        spuEntity.setLastUpdateTime(date);
        spuMapper.insertSelective(spuEntity);
//新增spuDetail
        SpuDetailEntity spuDetailEntity =
                BaiduBeanUtil.copyProperties(spuDTO.getSpuDetail(), SpuDetailEntity.class);
        spuDetailEntity.setSpuId(spuEntity.getId());
        spuDetailMapper.insertSelective(spuDetailEntity);
        spuDTO.getSkus().stream().forEach(skuDto -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDto,
                    SkuEntity.class);
            skuEntity.setSpuId(spuEntity.getId());
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDto.getStock());
            stockMapper.insertSelective(stockEntity);
        });
        return this.setResultSuccess();

    }
    @Override
    public Result<SpuDetailEntity> getSpuDetailBydSpu(Integer spuId) {
        SpuDetailEntity spuDetailEntity =
                spuDetailMapper.selectByPrimaryKey(spuId);
        return this.setResultSuccess(spuDetailEntity);
    }
    @Override
    public Result<List<SkuDTO>> getSkuBySpuId(Integer spuId) {
        List<SkuDTO> list = skuMapper.selectSkuAndStockBySpuId(spuId);
        return this.setResultSuccess(list);
    }

    @Override
    public Result<JSONObject> editGoods(SpuDTO spuDTO) {
        System.out.println(spuDTO);
//修改spu
        Date date = new Date();
        SpuEntity spuEntity = BaiduBeanUtil.copyProperties(spuDTO,
                SpuEntity.class);
        spuEntity.setLastUpdateTime(date);
        spuMapper.updateByPrimaryKeySelective(spuEntity);
//修改spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(BaiduBeanUtil.copyProperties(spuDTO.
                getSpuDetail(), SpuDetailEntity.class));
//修改sku
//先通过spuid删除sku
//然后新增数据
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId", spuDTO.getId());
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        List<Long> skuIdArr = skuEntities.stream().map(sku ->
                sku.getId()).collect(Collectors.toList());
        skuMapper.deleteByIdList(skuIdArr);
        stockMapper.deleteByIdList(skuIdArr);
        List<SkuDTO> skus = spuDTO.getSkus();
        this.saveSkusAndStocks(spuDTO.getSkus(), spuDTO.getId(), date);
        return this.setResultSuccess();
    }
    private void saveSkusAndStocks(List<SkuDTO> skus,Integer spuId,Date date){
        skus.stream().forEach(skuDto -> {
            SkuEntity skuEntity = BaiduBeanUtil.copyProperties(skuDto,
                    SkuEntity.class);
            skuEntity.setSpuId(spuId);
            skuEntity.setCreateTime(date);
            skuEntity.setLastUpdateTime(date);
            skuMapper.insertSelective(skuEntity);
            StockEntity stockEntity = new StockEntity();
            stockEntity.setSkuId(skuEntity.getId());
            stockEntity.setStock(skuDto.getStock());
            stockMapper.insertSelective(stockEntity);
        });
    }
    @Override
    public Result<JSONObject> delGoods(Integer spuId) {
//删除spu
        spuMapper.deleteByPrimaryKey(spuId);
//删除spuDetail
        spuDetailMapper.deleteByPrimaryKey(spuId);
//查询
        List<Long> skuIdArr = this.getSkuIdArrBySpuId(spuId);
        if(skuIdArr.size() > 0){//尽量加上判断避免全表数据被删除!!!!!!!!!!!!!!!
//删除skus
            skuMapper.deleteByIdList(skuIdArr);
//删除stock,与修改时的逻辑一样,先查询出所有将要修改skuId然后批量删除
            stockMapper.deleteByIdList(skuIdArr);
        }
        return this.setResultSuccess();
    }
    //重复代码抽取出来
    private List<Long> getSkuIdArrBySpuId(Integer spuId){
        Example example = new Example(SkuEntity.class);
        example.createCriteria().andEqualTo("spuId",spuId);
        List<SkuEntity> skuEntities = skuMapper.selectByExample(example);
        return skuEntities.stream().map(sku ->
                sku.getId()).collect(Collectors.toList());
    }

}
