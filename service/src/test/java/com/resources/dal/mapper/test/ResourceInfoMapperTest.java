package com.resources.dal.mapper.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.resources.common.BizType;
import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.ResourceInfoService;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResourceInfoMapperTest extends TestCase {

    @Resource
    private ResourceInfoService resourceInfoService;

    @Test
    public void testAddResourceInfo() {

        ResourceInfoDO resourceInfo = new ResourceInfoDO();
        resourceInfo.setId(1);
        resourceInfo.setTitle("v-title");
        resourceInfo.setDescription("v-desc");
        resourceInfo.setBizType(BizType.MOVIE.getType());
        resourceInfo.setCid(1);
        resourceInfo.setDownloadCounts(2);
        resourceInfo.setViewCounts(3);
        resourceInfo.setAttachment("http://www.qq.com");
        resourceInfo.setCreatedTime(new Date());
        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
        resourceInfo.setStatus(1);
        long resId = resourceInfoService.addResourceInfo(resourceInfo);
        Assert.assertTrue(resId > 0);

    }

    @Test
    public void testDeleteResourceInfo() {
        resourceInfoService.deleteResourceInfo(2);
    }

    @Test
    public void testUpdateResourceInfo() {
        ResourceInfoDO resourceInfo = new ResourceInfoDO();
        resourceInfo.setId(1);
        resourceInfo.setTitle("v-title-update");
        resourceInfo.setDescription("v-desc-update");
        resourceInfo.setBizType(BizType.MOVIE.getType());
        resourceInfo.setCid(1);
        resourceInfo.setDownloadCounts(3);
        resourceInfo.setViewCounts(3);
        resourceInfo.setAttachment("http://www.qq.com");
        resourceInfo.setCreatedTime(new Date());
        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
        resourceInfo.setStatus(1);

        int count = resourceInfoService.updateResourceInfo(resourceInfo);
        Assert.assertTrue(count > 0);
    }

    @Test
    public void testGetResourceInfo() {
        ResourceInfoDO resourceInfo = resourceInfoService.getResourceInfo(1);
        Assert.assertTrue(resourceInfo != null);
        Assert.assertTrue(resourceInfo.getId() == 1);
        Assert.assertTrue(resourceInfo.getTitle().equals("v-title-update"));
    }

    @Test
    public void testGetResourceInfoByIds() {
        List<Long> ids = new ArrayList<Long>();
        ids.add(1L);
        ids.add(3L);
        List<ResourceInfoDO> list = resourceInfoService.getResourceInfoByIds(ids);
        Assert.assertTrue(list.size() == 2);
    }
}
