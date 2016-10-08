package edu.illinois.cs.cogcomp.chunker.utils;

import edu.uw.cs.lil.tiny.mr.lambda.LogicalExpression;
import edu.uw.cs.lil.uwtime.chunking.ChunkSequence;
import edu.uw.cs.lil.uwtime.chunking.chunks.IChunk;
import edu.uw.cs.lil.uwtime.chunking.chunks.TemporalJointChunk;
import edu.uw.cs.lil.uwtime.corrections.AnnotationCorrections;
import edu.uw.cs.lil.uwtime.data.TemporalDataset;
import edu.uw.cs.lil.uwtime.data.TemporalDocument;
import edu.uw.cs.lil.uwtime.data.TemporalSentence;
import edu.uw.cs.lil.uwtime.data.readers.AbstractTemporalReader;
import edu.uw.cs.lil.uwtime.data.readers.TimeMLReader;
import edu.uw.cs.lil.uwtime.data.readers.WikiWarsReader;
import edu.uw.cs.lil.uwtime.learn.temporal.MentionResult;
import edu.uw.cs.lil.uwtime.utils.DependencyUtils.DependencyParseToken;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Qiang (John) Ning on 8/29/16.
 */
/*Read from TempEval-3 corpus
and convert the TimeML annotated files to CoNLL2000 format
so that illinois-chunker can take as input.*/

public class TempEval3Reader {
    private AbstractTemporalReader reader;
    private String type;
    private String datalabel;
    private String datasetName;
    private String dir;
    private TemporalDataset dataset;

    public TempEval3Reader(String type, String datalabel, String dir){
        switch(type.toLowerCase()){
            case "timeml":
                this.type = "TIMEML";
                System.out.println("Data type: "+this.type);
                reader = new TimeMLReader();
                System.out.println("TimeMLReader created successfully.");
                break;
            case "wikiwars":
                this.type = "WIKIWARS";
                System.out.println("Data type: "+this.type);
                reader = new WikiWarsReader();
                System.out.println("WikiWarsReader created successfully.");
                break;
            default:
                throw new IllegalArgumentException("Invalid corpus type: "+type+". Valid types are TIMEML and WIKIWARS.");
        }
        this.datalabel = datalabel;
        this.dir = dir;
        this.dataset = null;
        this.datasetName = this.type+":"+this.datalabel;
    }

    private void ReadData() throws IOException, SAXException, ParserConfigurationException, InterruptedException {
        if(dataset==null){
            dataset = new TemporalDataset(datasetName);
            for (TemporalDocument document : reader.getDataset(dir, datalabel).getDocuments()) {
                dataset.addDocument(document);
            }
            dataset.sort();
        }
        else{
            System.out.println("TempEval3Reader.dataset is not empty!");
            return;
        }
    }
    private void ApplyCorrection() throws IOException {
        AnnotationCorrections corrections = new AnnotationCorrections("./data/fromUWTime/corrections.csv");
        corrections.applyCorrections(dataset);
    }
    private void dataset2CoNLL(String out_dir, String out_fname, boolean skipEmpty, boolean singleLabel){
        LinkedList<String> conll = new LinkedList<>();
        for(TemporalDocument document : dataset.getDocuments()) {
            for(TemporalSentence sentence : document.withoutDCT().getSentences()){
                /*Get tokens*/
                List<String> tokens = sentence.getTokens();
                int N = tokens.size();
                /*Get POS tags*/
                List<DependencyParseToken> dependencyParse = sentence.getDependencyParse();
                int M = dependencyParse.size();
                if(N!=M){
                    System.out.println("tokens and dependencyParse do not have the same size: "+sentence.toString());
                    return;
                }
                String[] pos_tag = new String[N];
                for(int n=0;n<N;n++){
                    pos_tag[n] = dependencyParse.get(n).getPOS();
                }
                /*Get golden B/I/O labels with type (DATE/TIME/SET/DURATION)*/
                String[] label = new String[N];
                for(int i=0;i<N;i++)
                    label[i] = "O";
                ChunkSequence<TemporalJointChunk, LogicalExpression> goldChunkSequence =
                        sentence.getLabel();
                List<TemporalJointChunk> chunks = goldChunkSequence.getChunks();
                if(chunks.size()<=0 && skipEmpty){
                    continue;
                }
                String[] type = new String[chunks.size()];
                for(int i = 0; i<chunks.size(); i++){
                    MentionResult result = chunks.get(i).getResult();
                    if(!singleLabel) {
                        type[i] = result.getType();
                    }
                    else{
                        type[i] = "null";
                    }
                    IChunk<LogicalExpression> baseChunk = chunks.get(i).getBaseChunk();
                    label[baseChunk.getStart()] = "B-"+type[i];
                    for(int j=baseChunk.getStart()+1;j<=baseChunk.getEnd();j++){
                        label[j] = "I-"+type[i];
                    }
                }
                /*Add to output stream*/
                for(int i=0;i<N;i++){
                    conll.add(tokens.get(i)+" "+pos_tag[i]+" "+label[i]);
                }
                conll.add("\n");
            }
        }

        /*Output to file*/
        Path file = Paths.get(out_dir+out_fname);
        try{
            Files.write(file,conll, Charset.forName("UTF-8"));
        } catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("CoNLL format file generated successfully and saved to "+out_dir+out_fname);
    }

    private void dataset2sentence(String out_dir, String out_fname, boolean skipEmpty){
        LinkedList<String> output = new LinkedList<>();
        for(TemporalDocument document : dataset.getDocuments()) {
            for (TemporalSentence sentence : document.withoutDCT().getSentences()) {
                ChunkSequence<TemporalJointChunk, LogicalExpression> goldChunkSequence =
                        sentence.getLabel();
                List<TemporalJointChunk> chunks = goldChunkSequence.getChunks();
                if(chunks.size()<=0 && skipEmpty){
                    continue;
                }
                output.add(sentence.toString());
            }
        }
        Path file = Paths.get(out_dir+out_fname);
        try{
            Files.write(file,output, Charset.forName("UTF-8"));
        } catch(IOException e){
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException, SAXException, ParserConfigurationException, InterruptedException{
        String type = "TIMEML";
        String datafolder = "TimeBank";
        String dir = "~/temporal/data/TempEval3/Training/TBAQ-cleaned/";
        String out_dir = "./chunker/data/";
        String out_fname = "TimeBank_1label_corr_brown.txt";
        TempEval3Reader myReader = new TempEval3Reader(type,datafolder,dir);
        myReader.ReadData();
        myReader.ApplyCorrection();
        myReader.dataset2CoNLL(out_dir,out_fname,true,true);
        //myReader.dataset2sentence(out_dir,out_fname,false);
    }
}


