package io.batizhao.nlp.corpus;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * 基于感知机制作 word2vec 语料库
 * 使用的感知机模型在 hanlp.properties 文件中指定
 *
 * @author batizhao
 * @since 2018-12-04
 */
@RestController
@RequestMapping("corpus")
public class Word2VecController {

    private static Logger LOG = LoggerFactory.getLogger(Word2VecController.class);

    @Value("${app.nlp.corpus.v2}")
    public String corpus_v2;

    /**
     * 处理目录下的所有文件数据
     * 生成的语料格式为单个txt文本文件，文件中每行一个句子，单词与单词之间用空格分割。
     * 去除了词性标签、前后的中括号。
     *
     *
     * @param path 原始纯文本文件的路径，支持单个文件或者目录。
     * @return
     * @throws IOException
     */
    @PostMapping("w2c/d")
    public boolean parseDir(@RequestParam String path) throws IOException {
        File file_ = new File(path);

        LOG.info("Parse start ...");

        if (file_.isDirectory()) {
            String filePath, data;
            File file = new File(corpus_v2);
            List<File> files = (List<File>) FileUtils.listFiles(file_, FileFilterUtils.suffixFileFilter("txt"), DirectoryFileFilter.INSTANCE);

            for (File f : files) {
                filePath = f.getPath();
                LOG.info("parsing {} to {}.", filePath, corpus_v2);

                data = FileUtils.readFileToString(f, UTF_8);
                List<Term> terms = NLPTokenizer.segment(data);

                //根据标点符号换行
                data = terms.toString().replaceAll("[，。：；！]/\\w+,\\s+", "\n");

                //清除开头和结尾的中括号、分词的逗号、词性
                data = data.replaceAll("[\\[\\],《》<>〈〉]|/\\w+", "");

                FileUtils.writeStringToFile(file, data.concat("\n"), UTF_8, true);
            }
        }

        LOG.info("Parse completed , please see to path {}.", corpus_v2);

        return true;
    }

    /**
     * 处理单个文件中的数据
     * 生成的语料格式为单个txt文本文件，文件中每行一个句子，单词与单词之间用空格分割。
     * 去除了词性标签、前后的中括号。
     *
     *
     * @param path 原始纯文本文件的路径，支持单个文件或者目录。
     * @return
     * @throws IOException
     */
    @PostMapping("w2c/s")
    public boolean parseSingleFile(@RequestParam String path) {
        LOG.info("Parse start ...");

        File file = new File(corpus_v2);

        try(Stream<String> lines = Files.lines(Paths.get(path), UTF_8)) {
            lines.forEachOrdered(new Consumer<String>() {
                @Override
                public void accept(String line) {
                    try {
                        List<Term> terms = NLPTokenizer.segment(line);
                        String data = terms.toString().replaceAll("[\\[\\],《》<>〈〉]|/\\w+", "");
                        FileUtils.writeStringToFile(file, data.concat(System.getProperty("line.separator")), UTF_8, true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOG.info("Parse completed , please see to path {}.", corpus_v2);

        return true;
    }

}
