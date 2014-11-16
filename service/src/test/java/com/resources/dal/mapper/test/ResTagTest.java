package com.resources.dal.mapper.test;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.google.common.collect.ImmutableList;
import com.resources.dal.dataobject.ResTagDO;
import com.resources.service.ResTagService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResTagTest extends TestCase {

    @Resource
    private ResTagService resTagService;

    public ResTagDO get() {
        ResTagDO value = new ResTagDO();
        value.setBizType(1);
        value.setCid(1);
        value.setTagName("tagName");
        value.setCreatedTime(new Date());
        return value;
    }

    @Test
    public void testAddData() {
        int r = resTagService.addData(get());
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testAddDataIfNotExist() {
        ResTagDO value = get();
        int r = resTagService.addDataIfNotExist(value);
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testDelData() {
        ResTagDO value = get();
        value.setTagName("deleteTag");
        resTagService.addData(value);
        int r = resTagService.delData(value.getId());
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testUpdateData() {
        ResTagDO value = get();
        value.setTagName("tagName-update");
        resTagService.addData(value);
        value.setTagName(value.getTagName() + "_updated");
        int r = resTagService.updateData(value);
        Assert.assertTrue(r > 0);
    }

    @Test
    public void testGetData() {
        long pkId = 1;
        resTagService.getData(pkId);
    }

    @Test
    public void testGetTagListByIds() {
        ImmutableList<Long> list = ImmutableList.of(1L, 2L, 3L, 4L);
        List<ResTagDO> retList = resTagService.getTagListByIds(list);
        Assert.assertTrue(retList != null);
        Assert.assertTrue(!retList.isEmpty());
    }

}
