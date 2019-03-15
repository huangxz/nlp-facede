package io.batizhao.nlp.corpus;

import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.model.perceptron.PerceptronLexicalAnalyzer;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.EmptyFileFilter;
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
 * 基于感知机制作人民日报2014语料格式语料库
 * 使用的感知机模型在 application.yml 文件中指定
 *
 * @author batizhao
 * @since 2018-12-04
 *
 */
@RestController
@RequestMapping("corpus")
public class PerceptronController {

    private static Logger LOG = LoggerFactory.getLogger(PerceptronController.class);

    private static final String TARGET_SUFFIX = ".txt";

    @Value("${app.nlp.corpus.v1}")
    public String corpus;

    @Value("${app.nlp.model.cws}")
    public String cws;

    @Value("${app.nlp.model.pos}")
    public String pos;

    @Value("${app.nlp.model.ner}")
    public String ner;

    /**
     * 基于感知机序列标注的词法分析器
     * 生成人民日报2014语料格式：
     *
     * 单词与词性之间使用/分割，如华尔街/nsf，且任何单词都必须有词性，包括标点等。
     * 单词与单词之间使用空格分割，如美国/nsf 华尔街/nsf 股市/n。
     * 支持用[]将多个单词合并为一个复合词，如[纽约/nsf 时报/n]/nz，复合词也必须遵守1和2两点规范。
     *
     * @param path 原始纯文本文件的路径，支持单个文件或者目录。
     * @throws IOException
     */
    @PostMapping("perceptron")
    public boolean parse(@RequestParam String path) throws IOException {
        PerceptronLexicalAnalyzer analyzer = new PerceptronLexicalAnalyzer(cws, pos, ner);

        File file_ = new File(path);

        LOG.info("Parse start ...");

        if (file_.isDirectory()) {
            String filePath;
            List<File> files = (List<File>) FileUtils.listFiles(file_, EmptyFileFilter.NOT_EMPTY, DirectoryFileFilter.INSTANCE);

            for (File f : files) {
                filePath = f.getPath();
                analyze(analyzer, filePath, f);
            }
        } else {
//            analyze(analyzer, path, file_);

            try(Stream<String> lines = Files.lines(Paths.get(path), UTF_8)) {
                String name = FilenameUtils.getBaseName(path);
                File file = new File(corpus, name.concat(TARGET_SUFFIX));
                lines.forEachOrdered(new Consumer<String>() {
                    @Override
                    public void accept(String line) {
                        try {
                            Sentence sentence = analyzer.analyze(line);
                            FileUtils.writeStringToFile(file, sentence.toString().concat(System.getProperty("line.separator")), UTF_8, true);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LOG.info("Parse completed , please see to path {}.", corpus);

        return true;
    }

    private void analyze(PerceptronLexicalAnalyzer analyzer, String filePath, File f) throws IOException {
        String data;
        String name;
        File file;
        LOG.info("parsing {} to {}.", filePath, corpus);

        data = FileUtils.readFileToString(f, UTF_8);

        Sentence sentence = analyzer.analyze(data);

        name = FilenameUtils.getBaseName(filePath);
        file = new File(corpus, name.concat(TARGET_SUFFIX));

        FileUtils.writeStringToFile(file, sentence.toString(), UTF_8);
    }

}
