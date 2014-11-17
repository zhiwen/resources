SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Indexes */

DROP INDEX IDX_KEY_TYPE ON RES_KV;
DROP INDEX IDX_ID_DID ON RES_MOVIE;
DROP INDEX IDX_BIZT_TAGN ON RES_TAG;



/* Drop Tables */

DROP TABLE RES_KV;
DROP TABLE RES_MOVIE;
DROP TABLE RES_TAG;
DROP TABLE RES_URL;
DROP TABLE SPIDER_RECORD;




/* Create Tables */

CREATE TABLE RES_KV
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	-- 信息的key
	RES_KEY VARCHAR(100) NOT NULL COMMENT '信息的key',
	-- 资源value，可以放任何字符的东西
	RES_VALUE VARCHAR(4000) COMMENT '资源value，可以放任何字符的东西',
	-- 资源类型
	TYPE VARCHAR(50) NOT NULL COMMENT '资源类型',
	-- 创建时间
	CREATED_TIME DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY (ID),
	CONSTRAINT un_K_T UNIQUE (RES_KEY, TYPE)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE RES_MOVIE
(
	-- 影视id
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT COMMENT '影视id',
	-- 豆瓣影视id
	DID BIGINT COMMENT '豆瓣影视id',
	-- 影视标题
	TITLE VARCHAR(100) NOT NULL UNIQUE COMMENT '影视标题',
	-- 原名
	ORIGINAL_TITLE VARCHAR(100) COMMENT '原名',
	-- 演员->tag表id列表
	-- 
	CAST_IDS VARCHAR(400) COMMENT '演员->tag表id列表
',
	-- 导演-->tag表id列表
	DIRECTOR_IDS VARCHAR(200) COMMENT '导演-->tag表id列表',
	-- 电影类型
	GENRE_IDS VARCHAR(200) COMMENT '电影类型',
	-- 作家-->tag表id列表
	WRITER_IDS VARCHAR(200) COMMENT '作家-->tag表id列表',
	-- 制片国家/地区-->tag表id列表
	COUNTRY_IDS VARCHAR(100) COMMENT '制片国家/地区-->tag表id列表',
	-- 影视又名
	AKA VARCHAR(200) COMMENT '影视又名',
	-- douban影视url
	ALT VARCHAR(100) COMMENT 'douban影视url',
	-- 电影封面图id
	COVER_IMAGES_ID BIGINT COMMENT '电影封面图id',
	-- douban移动版影视url
	MOBILE_URL VARCHAR(100) COMMENT 'douban移动版影视url',
	-- 评分人数
	RATING_COUNT INT COMMENT '评分人数',
	-- 评分信息-->kv表id
	RATING_ID BIGINT COMMENT '评分信息-->kv表id',
	-- 想看人数
	WISH_COUNT INT COMMENT '想看人数',
	-- 看过人数
	COLLECT_COUNT INT COMMENT '看过人数',
	-- 条目分类, movie或者tv
	SUBTYPE INT COMMENT '条目分类, movie或者tv',
	-- 官方网站
	WEBSITE VARCHAR(100) COMMENT '官方网站',
	-- 豆瓣小站
	DOUBAN_SITE VARCHAR(100) COMMENT '豆瓣小站',
	-- 如果条目类型是电影则为上映日期，如果是电视剧则为首Ï日期
	PUBDATES VARCHAR(50) COMMENT '如果条目类型是电影则为上映日期，如果是电视剧则为首Ï日期',
	-- 大陆上映日期，如果条目类型是电影则为上映日期，如果是电视剧则为首播日期
	MAINLAND_PUBDATE VARCHAR(50) COMMENT '大陆上映日期，如果条目类型是电影则为上映日期，如果是电视剧则为首播日期',
	-- 年代
	YEAR VARCHAR(50) COMMENT '年代',
	-- 语言
	LANGUAGES VARCHAR(100) COMMENT '语言',
	-- 片长
	DURATIONS VARCHAR(100) COMMENT '片长',
	-- 简介-->kv表id
	-- 
	SUMMARY_ID BIGINT COMMENT '简介-->kv表id
',
	-- 短评数
	COMMENT_COUNT INT COMMENT '短评数',
	-- 影评数量
	REVIEW_COUNT INT COMMENT '影评数量',
	-- 总季数(tv only)
	SEASON_COUNT INT COMMENT '总季数(tv only)',
	-- 当前季数(tv only)
	CURRENT_SEASON INT COMMENT '当前季数(tv only)',
	-- 当前季的集数(tv only)
	EPISODE_COUNT INT COMMENT '当前季的集数(tv only)',
	-- imdb的id
	IMDB_ID VARCHAR(50) COMMENT 'imdb的id',
	-- 标签 -->tag表Id数组
	-- 
	TAG_IDS VARCHAR(400) COMMENT '标签 -->tag表Id数组
',
	-- 数据状态(1,2，3，4，5)
	DATA_STATUS INT COMMENT '数据状态(1,2，3，4，5)',
	-- 是否可以播放，（0，1）
	PLAYABLE INT COMMENT '是否可以播放，（0，1）',
	-- 创建时间
	CREATED_TIME DATETIME NOT NULL COMMENT '创建时间',
	-- 修改时间
	MODIFIED_TIME DATETIME NOT NULL COMMENT '修改时间',
	PRIMARY KEY (ID)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE RES_TAG
(
	-- tag id
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT COMMENT 'tag id',
	-- 几大类别(电影，文库，软件...)
	CID BIGINT COMMENT '几大类别(电影，文库，软件...)',
	-- 标签业务类型(作者，演员，导演...)
	BIZ_TYPE INT NOT NULL COMMENT '标签业务类型(作者，演员，导演...)',
	-- 标签名称
	TAG_NAME VARCHAR(100) NOT NULL COMMENT '标签名称',
	-- 创建时间
	CREATED_TIME DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY (ID)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE RES_URL
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	-- 业务类型
	-- 
	BIZ_TYPE INT COMMENT '业务类型
',
	-- url
	URL VARCHAR(200) COMMENT 'url',
	-- 图片描述
	DESCRIPTION VARCHAR(200) COMMENT '图片描述',
	-- 业务主建
	OBJECT_ID BIGINT COMMENT '业务主建',
	-- 创建时间
	CREATED_TIME DATETIME NOT NULL COMMENT '创建时间',
	PRIMARY KEY (ID)
) DEFAULT CHARACTER SET utf8;


CREATE TABLE SPIDER_RECORD
(
	ID BIGINT NOT NULL UNIQUE AUTO_INCREMENT,
	-- 类型(douban, baike)
	TYPE INT COMMENT '类型(douban, baike)',
	-- 名称
	TAG_NAME VARCHAR(100) COMMENT '名称',
	-- 第多少页
	PAGE_NUMBER INT COMMENT '第多少页',
	-- 已经抓取条数
	EAT_NUMBER INT COMMENT '已经抓取条数',
	CREATED_TIME DATETIME,
	PRIMARY KEY (ID),
	UNIQUE (TYPE, TAG_NAME)
) DEFAULT CHARACTER SET utf8;



/* Create Indexes */

CREATE INDEX IDX_KEY_TYPE ON RES_KV (RES_KEY ASC, TYPE ASC);
CREATE INDEX IDX_ID_DID ON RES_MOVIE (ID ASC, DID ASC);
CREATE INDEX IDX_BIZT_TAGN ON RES_TAG (BIZ_TYPE ASC, TAG_NAME ASC);



