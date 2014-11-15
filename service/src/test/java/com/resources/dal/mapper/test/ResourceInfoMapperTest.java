//package com.resources.dal.mapper.test;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
//import javax.annotation.Resource;
//
//import junit.framework.Assert;
//import junit.framework.TestCase;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import com.resources.common.BizType;
//import com.resources.dal.module.ResourceInfoDO;
//import com.resources.service.ResourceInfoService;
//
///**
// * Unit test for simple App.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(value = { "classpath:com/resources/dal/mapper/test/test-service.xml" })
//public class ResourceInfoMapperTest extends TestCase {
//
//    @Resource
//    private ResourceInfoService resourceInfoService;
//
//    @Test
//    public void testAddResourceInfo() {
//
//        ResourceInfoDO resourceInfo = new ResourceInfoDO();
//        resourceInfo.setId(1);
//        // resourceInfo.setTitle("2014年惊悚悬疑《窃听风云3》BD国粤双语中字");
//        resourceInfo.setTitle("2014年惊悚科幻《机器纪元》BD中英双字幕");
//        resourceInfo.setDescription("本片讲述了香港新界围村陆氏家族在房产开发交易黑幕后的故事。故事开始于村民罗永就（古天乐 饰）醉酒撞死了异姓兄弟陆永远（钱嘉乐 饰），陆永远之妻月华（周迅 饰）从此与他势不两立。罗永就5年后出狱，发现陆氏家族大家长陆瀚涛（曾江 饰）和其女陆永瑜（叶璇 饰），以及女婿（黄磊 饰）已经坐拥新界最大房产公司，而陆家兄弟陆金强（刘青云 饰）、陆永富（方中信 饰）等人则疯狂扩张势力。不久香港房产巨鳄邀请罗永就“共商大事”。同时，神秘人阿祖（吴彦祖 饰）透露给月华一个通过窃听得到的秘密消息，他告诉月华要想在这场欺天阴谋中复仇，就必须按照他的指示去做…… 于是几方势力都开始暗自行动。");
//        resourceInfo.setBizType(BizType.MOVIE.getType());
//        resourceInfo.setCid(1);
//        resourceInfo.setDownloadCounts(2);
//        resourceInfo.setViewCounts(3);
//        resourceInfo.setAttachment("ftp://ygdy8:ygdy8@y201.dygod.org:8111/[阳光电影www.ygdy8.com].窃听风云3.BD.720p.国粤双语中字.mkv");
//        resourceInfo.setCreatedTime(new Date());
//        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
//        resourceInfo.setStatus(1);
//        long resId = resourceInfoService.addResourceInfo(resourceInfo);
//        Assert.assertTrue(resId > 0);
//    }
//
//    @Test
//    public void testAddResourceInfoGame() {
//
//        ResourceInfoDO resourceInfo = new ResourceInfoDO();
//        resourceInfo.setId(1);
//        // resourceInfo.setTitle("2014年惊悚悬疑《窃听风云3》BD国粤双语中字");
//        resourceInfo.setTitle("07月新番 刀剑神域2 第16话");
//        resourceInfo.setDescription("超人气轻小说改编动画《刀剑神域》曾经在日本乃至世界多个国家掀起刀剑旋风。自从宣布将制作第二季动画以来，有关动向一直牵动粉丝们的心。而在上周末刚刚结束的电击文库活动游戏的电击 感谢祭2014电击文库 春之祭典2014当中，官方终于公布了第二季动画的具体开播时间");
//        resourceInfo.setBizType(BizType.GAME.getType());
//        resourceInfo.setCid(1);
//        resourceInfo.setDownloadCounts(2);
//        resourceInfo.setViewCounts(3);
//        resourceInfo.setAttachment("http://down14.gamefk.com/狂怒中文版.rar");
//        resourceInfo.setCreatedTime(new Date());
//        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
//        resourceInfo.setStatus(1);
//        long resId = resourceInfoService.addResourceInfo(resourceInfo);
//        Assert.assertTrue(resId > 0);
//    }
//
//    @Test
//    public void testAddResourceInfoSoftware() {
//
//        ResourceInfoDO resourceInfo = new ResourceInfoDO();
//        resourceInfo.setId(1);
//        resourceInfo.setTitle("神拍手PowerCam 2.5.0支持对焦测光分离");
//        resourceInfo.setDescription("神拍手PowerCam，名字起得有点大哈。实际使用感觉还是不错的，有很多贴心的功能。");
//        resourceInfo.setBizType(BizType.SOFTWARE.getType());
//        resourceInfo.setCid(1);
//        resourceInfo.setDownloadCounts(2);
//        resourceInfo.setViewCounts(3);
//        resourceInfo.setAttachment("http://pan.baidu.com/s/1o6wTMWy");
//        resourceInfo.setCreatedTime(new Date());
//        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
//        resourceInfo.setStatus(1);
//        long resId = resourceInfoService.addResourceInfo(resourceInfo);
//        Assert.assertTrue(resId > 0);
//    }
//
//    @Test
//    public void testDeleteResourceInfo() {
//        resourceInfoService.deleteResourceInfo(2);
//    }
//
//    @Test
//    public void testUpdateResourceInfo() {
//        ResourceInfoDO resourceInfo = new ResourceInfoDO();
//        resourceInfo.setId(1);
//        resourceInfo.setTitle("v-title-update");
//        resourceInfo.setDescription("v-desc-update");
//        resourceInfo.setBizType(BizType.MOVIE.getType());
//        resourceInfo.setCid(1);
//        resourceInfo.setDownloadCounts(3);
//        resourceInfo.setViewCounts(3);
//        resourceInfo.setAttachment("http://www.qq.com");
//        resourceInfo.setCreatedTime(new Date());
//        resourceInfo.setModifiedTime(resourceInfo.getCreatedTime());
//        resourceInfo.setStatus(1);
//
//        int count = resourceInfoService.updateResourceInfo(resourceInfo);
//        Assert.assertTrue(count > 0);
//    }
//
//    @Test
//    public void testGetResourceInfo() {
//        ResourceInfoDO resourceInfo = resourceInfoService.getResourceInfo(1);
//        Assert.assertTrue(resourceInfo != null);
//        Assert.assertTrue(resourceInfo.getId() == 1);
//        Assert.assertTrue(resourceInfo.getTitle().equals("v-title-update"));
//    }
//
//    @Test
//    public void testGetResourceInfoByIds() {
//        List<Long> ids = new ArrayList<Long>();
//        ids.add(1L);
//        ids.add(3L);
//        List<ResourceInfoDO> list = resourceInfoService.getResourceInfoByIds(ids);
//        Assert.assertTrue(list.size() == 2);
//    }
//
//    @Test
//    public void testGetOrderResourceInfoByViews() {
//        List<ResourceInfoDO> resList = resourceInfoService.getOrderResourceInfoByViews(1, BizType.MOVIE, 0, 10);
//        Assert.assertTrue(resList.size() < 10 && resList.size() > 0);
//    }
//
//    @Test
//    public void testGetOrderResourceInfoByDownloads() {
//        List<ResourceInfoDO> resList = resourceInfoService.getOrderResourceInfoByDownloads(1, BizType.MOVIE, 0, 10);
//        Assert.assertTrue(resList.size() < 10 && resList.size() > 0);
//    }
// }
