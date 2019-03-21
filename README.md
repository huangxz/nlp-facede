## 快速开始

首先需要部署模型到本地，这里把 [nlp.zip](https://pan.baidu.com/s/1BLrmW1ZbhVO6-d_277A-8A) 解压到 /opt 这个目录。

否则你需要修改 application.yml 和 hanlp.properties 中相应的目录。

之后，你可以直接在项目根目录用 mvn spring-boot:run 启动，也可以用下边的 jar 方式启动。

打包
```shell
# mvn clean package -Dmaven.test.skip=true
```

启动，根据实际情况修改 hanlp 感知机模型的路径。这里的 JVM 调优参数可以酌情删除，是训练大模型时需要配置的。
```shell
# java -XX:MetaspaceSize=1024m -XX:MaxMetaspaceSize=1024m -Xms8192m -Xmx8192m -Xmn1024m -Xss8m \
     -jar nlp-facede-0.0.1-SNAPSHOT.jar \
     --app.nlp.corpus.v1=/opt/nlp/corpus/v1 \
     --app.nlp.corpus.v2=/opt/nlp/corpus/v2.txt \
     --app.nlp.model.cws=/opt/nlp/hanlp/data/model/perceptron/wenjian/cws.bin \
     --app.nlp.model.pos=/opt/nlp/hanlp/data/model/perceptron/wenjian/pos.bin \
     --app.nlp.model.ner=/opt/nlp/hanlp/data/model/perceptron/wenjian/ner.bin \
     --app.nlp.model.vec=/opt/nlp/hanlp/data/model/word2vec/wenjian.msr.txt
```

提取关键字
```shell
# curl -X POST \
  'http://localhost:9090/api/keyword?title=转发信息产业部国家发展和改革委员会关于调整部分电信业务资费管理方式的通知&size=5' \
  -H 'Cache-Control: no-cache'
```

## 制作自己的语料库

在【基于感知机制作 word2vec 语料库】一节中会用到这三个模型，可以根据实际需要打开或者关闭。
如果注释掉，就会使用 hanlp 的默认模型。
如果打开，就会加载自己训练的模型。

```
PerceptronCWSModelPath=data/model/perceptron/pku2681/cws.bin
PerceptronPOSModelPath=data/model/perceptron/pku2681/pos.bin
PerceptronNERModelPath=data/model/perceptron/pku2681/ner.bin
```

基于感知机制作人民日报2014语料格式语料库
```shell
# curl -X POST http://localhost:9090/corpus/perceptron?path=/opt/nlp/text
```

基于感知机制作 word2vec 语料库
```shell
# curl -X POST http://localhost:9090/corpus/w2c/d?path=/opt/nlp/text
```

## 根据自己的语料库训练行业模型

在训练模型之前，要先确保模型生成的目录 pku2681、word2vec 已经存在。

停止之前的 App，重新使用下边的命令 run
```shell
# java -jar nlp-facede-0.0.1-SNAPSHOT.jar \
     --app.nlp.corpus.v1=/opt/nlp/corpus/v1 \
     --app.nlp.corpus.v2=/opt/nlp/corpus/v2.txt \
     --app.nlp.model.cws=/opt/nlp/hanlp/data/model/perceptron/pku2681/cws.bin \
     --app.nlp.model.pos=/opt/nlp/hanlp/data/model/perceptron/pku2681/pos.bin \
     --app.nlp.model.ner=/opt/nlp/hanlp/data/model/perceptron/pku2681/ner.bin \
     --app.nlp.model.vec=/opt/nlp/hanlp/data/model/word2vec/msr.txt
```

### 基于 v1 语料库

训练 cws 分词模型
```shell
# curl -X POST http://localhost:9090/model/cws
```

训练 pos 词性标注模型
```shell
# curl -X POST http://localhost:9090/model/pos
```

训练 ner 实体识别模型
```shell
# curl -X POST http://localhost:9090/model/ner
```

### 基于 v2 语料库

训练 word2vec 向量模型
```shell
# curl -X POST http://localhost:9090/model/w2v
```

 

