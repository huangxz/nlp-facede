package io.batizhao.nlp;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.mining.word2vec.WordVectorModel;
import com.hankcs.hanlp.model.perceptron.PerceptronSegmenter;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author batizhao
 * @since 2018-12-21
 */
public class DemoTest extends NlpApplicationTests {

    @Value("${app.nlp.model.vec}")
    public String vec;

    @Test
    public void testSeg() throws IOException {
        PerceptronSegmenter segmenter = new PerceptronSegmenter();
        System.out.println(segmenter.segment("商品和服务"));

        System.out.println(segmenter.segment("中国电信股份[2005]94号关于印发《中国电信股份有限公司固定资产投资计划管理暂行办法》的通知各省级电信有限公司"));

    }

    @Test
    public void testWord2Vec() throws IOException {
        WordVectorModel wordVectorModel = new WordVectorModel(vec);
        System.out.println(wordVectorModel.similarity("中国电信", "中电信"));
        System.out.println(wordVectorModel.similarity("政企部", "企信部"));

        System.out.println(wordVectorModel.nearest("中国电信"));
        System.out.println(wordVectorModel.nearest("办公室"));

        DocVectorModel docVectorModel = new DocVectorModel(wordVectorModel);

        String[] documents = new String[]{
                "中国电信纪要〔2010〕198号2008年总部IT运维管理系统建设工程竣工验收会议纪要",
                "中国电信培训〔2013〕20号通知",
                "软件使用权许可及保密协议（两方）",
                "系统集成合同",
                "互联星空业务合同",
                "电信日宽带促销方案",
                "软件开发协议",
                "广告设计制作发布合同"
        };

        for (int i = 0; i < documents.length; i++)
        {
            docVectorModel.addDocument(i, documents[i]);
        }

        System.out.println("============通知=============");
        List<Map.Entry<Integer, Float>> entryList = docVectorModel.nearest("通知");
        for (Map.Entry<Integer, Float> entry : entryList)
        {
            System.out.printf("%d %s %.2f\n", entry.getKey(), documents[entry.getKey()], entry.getValue());
        }

        System.out.println("============会议=============");
        entryList = docVectorModel.nearest("会议");
        for (Map.Entry<Integer, Float> entry : entryList)
        {
            System.out.printf("%d %s %.2f\n", entry.getKey(), documents[entry.getKey()], entry.getValue());
        }

        System.out.println("============合同=============");
        entryList = docVectorModel.nearest("合同");
        for (Map.Entry<Integer, Float> entry : entryList)
        {
            System.out.printf("%d %s %.2f\n", entry.getKey(), documents[entry.getKey()], entry.getValue());
        }

        System.out.println(docVectorModel.similarity("中国电信培训〔2013〕20号通知", "中国移动纪要〔2010〕198号"));
        System.out.println(docVectorModel.similarity("软件使用权许可及保密协议", "中国电信培训〔2013〕20号通知"));

    }

    @Test
    public void testKeyword() {
        String document = "中电信瑞金〔2016〕82号关于瑞金分公司领导分工调整的通知各部室、中心、班组、营业部：经支委会研究，现对瑞金分公司领导工作分工作如下调整：何荣胜总经理主持全面工作，直接分管财务、人力资源工作。黄文鑫副总经理协助总经理分管网络部、设备维护组、客户网络维护组、客端装维中心；具体负责通信建设、网络维护、资源管理、安全生产、工会工作，并协助总经理抓好其他各项工作。曾祥东副总经理协助总经理分管经营工作；具体分管（销售部、渠道建设、客户服务部）、核心商圈营业部、城区营业部、农村营业部，并协助总经理抓好其他各项工作。丁晓辉督办协助总经理分管党群、文秘档案、后勤、土建工程、教育培训、宣传报道、精神文明、综合治理、人武、计划生育、“精准扶贫”等工作，具体分管综合办公室、政企客户部、商客营业部，并协助总经理抓好其他各项工作。中国电信瑞金分公司2016年11月7日中国电信瑞金分公司办公室2016年11月7日印发";
        List<String> keywordList = HanLP.extractKeyword(document, 5);
        System.out.println(keywordList);

        List<String> phraseList = HanLP.extractPhrase(document, 10);
        System.out.println(phraseList);
    }
}
