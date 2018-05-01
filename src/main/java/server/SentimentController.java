package server;

import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import org.ejml.simple.SimpleMatrix;

import edu.stanford.nlp.sentiment.*;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

@RestController
public class SentimentController {

	StanfordCoreNLP tokenizer;
	StanfordCoreNLP pipeline;

	public SentimentController() {
		// We construct two pipelines.  One handles tokenization, if
		// necessary.  The other takes tokenized sentences and converts
		// them to sentiment trees.
		Properties pipelineProps = new Properties();
		Properties tokenizerProps = new Properties();

		pipelineProps.setProperty("ssplit.isOneSentence", "true");
		pipelineProps.setProperty("annotators", "parse, sentiment");
		pipelineProps.setProperty("enforceRequirements", "false");

		tokenizerProps.setProperty("annotators", "tokenize, ssplit");

		this.tokenizer = new StanfordCoreNLP(tokenizerProps);
		this.pipeline = new StanfordCoreNLP(pipelineProps);

	}

	@RequestMapping(value = "/sentiment", consumes = "application/json")
	@ResponseBody
	public HashMap<Integer,HashMap<String,Object>> sentiment(@RequestBody ArrayList<String> lines) {

		System.out.println("line" + lines);

		HashMap<Integer,HashMap<String,Object>> response = new HashMap<Integer,HashMap<String,Object>>();
		for(int i = 0; i < lines.size(); i++) {

			response.put(i,new HashMap<String,Object>());

			Annotation annotation = tokenizer.process(lines.get(i));
			pipeline.annotate(annotation);
			double sentiment = 0;
			int sentenceCount = annotation.get(CoreAnnotations.SentencesAnnotation.class).size();
			for( int j = 0; j < sentenceCount; j++ ) {
				CoreMap sentence = annotation.get(CoreAnnotations.SentencesAnnotation.class).get(j);
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				SimpleMatrix vector = RNNCoreAnnotations.getPredictions(tree);
				sentiment += (vector.get(1)*0.25+vector.get(2)*0.5+vector.get(3)*0.75+vector.get(4))/(double)sentenceCount;
			}
			response.get(i).put("line",lines.get(i));
			response.get(i).put("sentiment",sentiment);
		}
		return response;
	}
}
