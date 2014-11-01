package com.resources.dal.mapper.test;

import java.util.Date;

import javax.annotation.Resource;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.resources.dal.module.ResourceInfoDO;
import com.resources.service.ResourceInfoService;

/**
 * Unit test for simple App.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
public class ResourceInfoMapperTest extends TestCase {

    @Resource(name = "resourceInfoService")
    private ResourceInfoService resourceInfoService;

    @Test
    public void testAddResourceInfo() {

        ResourceInfoDO resourceInfo = new ResourceInfoDO();
        resourceInfo.setId(1);
        resourceInfo.setTitle("v-title");
        resourceInfo.setDescription("v-desc");
        resourceInfo.setBizType(1);
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

    public void testDeleteResourceInfo() {

    }

    public void testUpdateResourceInfo() {

    }

    public void testGetResourceInfo() {

    }

    public void testGetResourceInfoByIds() {

    }
}
