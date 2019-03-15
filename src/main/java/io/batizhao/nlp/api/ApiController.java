package io.batizhao.nlp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.mining.word2vec.DocVectorModel;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.TextRankKeyword;
import io.batizhao.nlp.Document;
import io.batizhao.nlp.WordVectorModelInitiator;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author batizhao
 * @since 2018-12-24
 */
@RestController
@RequestMapping("api")
public class ApiController {

    private static Logger LOG = LoggerFactory.getLogger(ApiController.class);

    @Autowired
    WordVectorModelInitiator initiator;

    /**
     * 根据 q，查 documents 中的相关度，从高到低排序
     * @param q 查询的串
     * @param documents 被查询的 JSON
     * @throws IOException
     */
    @PostMapping("similar")
    public List<Document> lookupSimilarDocuments(@RequestParam String q, @RequestBody List<Document> documents) {
        DocVectorModel docVectorModel = new DocVectorModel(initiator.getWordVectorModel());

        LOG.info("docs : {}", documents);

        for (int i = 0; i < documents.size(); i++)
        {
            docVectorModel.addDocument(i, documents.get(i).getTitle());
        }

        List<Map.Entry<Integer, Float>> entryList = docVectorModel.nearest(q);
        List<Document> list = new ArrayList<>();

        for (Map.Entry<Integer, Float> entry : entryList) {
            list.add(documents.get(entry.getKey()));
            LOG.info("Title : {}, Similar : {}", documents.get(entry.getKey()).getTitle(), entry.getValue());
        }

        return list;

    }

    /**
     * 提取会议通知内容
     * @param path 文件路径
     * @return
     * @throws IOException
     */
    @PostMapping("meeting")
    public Map<String, String> extractMeetingNotice(@RequestParam String path) throws IOException {

        File file = new File(path);
        String text = FileUtils.readFileToString(file, UTF_8);

        List<Term> terms = HanLP.newSegment().enableCustomDictionaryForcing(true).seg(text);

        //在词前增加分隔符用来 split
        Nature pcNature = Nature.fromString("HYTZ");
        for (Term term : terms) {
            if (term.nature == pcNature) {
                term.word = "|" + term.word;
            }
        }

        //去除首尾中括号
        String data = terms.toString().replaceAll("[\\[\\]]", "");
        LOG.debug("s1: {}", data);

        //必须用 StringUtils 才能过滤开头的空格
        String[] strs = StringUtils.split(data, "|");
        LOG.debug("s2: {}", Arrays.toString(strs));

        Map<String, String> m = new HashMap<>();
        String ms0, ms1;
        for (String s : strs) {
            String[] ms = StringUtils.splitByWholeSeparator(s,"/HYTZ, ");
            System.out.println("s3" + Arrays.toString(ms));

            ms0 = ms[0];
            ms1 = ms[1];
            ms1 = ms1.replaceAll("/\\w+,*\\s*", "");

            m.put(ms0, ms1);
        }

        return m;
    }

    /**
     * 关键词提取
     * @param document 被提取的内容
     * @param size 返回关键字的数量
     * @return
     */
    @PostMapping("keyword")
    public List<String> extractKeyword(@RequestBody String document, @RequestBody int size) throws IOException {
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer();
        List<Term> terms = analyzer.seg(document);

        TextRankKeyword textRankKeyword = new TextRankKeyword();
        List<String> sentenceList = textRankKeyword.getKeywords(terms, size);
        return sentenceList;
    }
}
