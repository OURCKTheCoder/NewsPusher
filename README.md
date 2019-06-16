# NewsRecommendation
大学校内新闻搜索 & 推荐系统。

### 架构 & 实现要点
~~1. 使用IKAnalyzer分词器对新闻进行分词；  
2. 使用R按照分词结果对新闻按照关键词聚类。聚类的结果存储在数据库；  
3. 使用Solr对文档集合（新闻总和）进行倒排索引。~~  

单纯采用“按关键词聚类”的方法缺乏灵活性。  
现采用基于协同过滤的推荐算法。基本思想大致为：将具有相似阅读习惯（体现为阅读历史记录完全相同或一定程度上相似）的多个用户分到同一组，将该组内成员共通或相似的阅读记录提取出来，作为该组的特征阅读习惯（又称为“特征向量”）。  
在实现上遵循以下步骤：  
1. 若某个用户组G之外的某位用户A阅读习惯与该组G的阅读习惯一定程度上相似，即可认为该用户A属于组G；
2. 将G的特征阅读习惯历史中A未阅读的文章推荐给A，并将将A加入G；
3. 使用Solr作为实时推荐引擎，组阅读习惯与用户阅读习惯的匹配度根据查询的得分高低来衡量。

### 系统架构
![Image text](https://github.com/OURCKTheCoder/YibanNewsPusher/blob/master/src/main/resources/SysStructure.png)
