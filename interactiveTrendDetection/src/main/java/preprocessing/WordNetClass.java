package preprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.lexical_db.data.Concept;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.HirstStOnge;
import edu.cmu.lti.ws4j.impl.JiangConrath;
import edu.cmu.lti.ws4j.impl.LeacockChodorow;
import edu.cmu.lti.ws4j.impl.Lesk;
import edu.cmu.lti.ws4j.impl.Lin;
import edu.cmu.lti.ws4j.impl.Path;
import edu.cmu.lti.ws4j.impl.Resnik;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;
import edu.cmu.lti.ws4j.util.DepthFinder;
import edu.cmu.lti.ws4j.util.PathFinder;
import edu.cmu.lti.ws4j.util.DepthFinder.Depth;
import edu.cmu.lti.jawjaw.pobj.Lang;
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.pobj.Sense;
import edu.cmu.lti.jawjaw.pobj.Synlink;
import edu.cmu.lti.jawjaw.pobj.Synset;
import edu.cmu.lti.jawjaw.pobj.SynsetDef;
import edu.cmu.lti.jawjaw.pobj.Word;
import edu.cmu.lti.jawjaw.util.WordNetUtil;
import edu.cmu.lti.jawjaw.JAWJAW;
import edu.cmu.lti.jawjaw.db.SenseDAO;
import edu.cmu.lti.jawjaw.db.SynlinkDAO;
import edu.cmu.lti.jawjaw.db.SynsetDAO;
import edu.cmu.lti.jawjaw.db.SynsetDefDAO;
import edu.cmu.lti.jawjaw.db.WordDAO;




public class WordNetClass {
	
	private static ILexicalDatabase db = new NictWordNet();
	
	private static double compute(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
 
	public static void main(String[] args) {
		
		
		String word1="material";//"use";
		DepthFinder depthfinder=new DepthFinder(db);
		PathFinder pathfinder=new PathFinder(db);
		
		POS pos = POS.valueOf("n");
		//List<Synset> synsets = WordNetUtil.wordToSynsets(word1, pos);
		//List<Concept> synsetStrings = new ArrayList<Concept>(synsets.size());
		double criterionSimilarityDegree=0.8;
		
		List<Word> words = WordDAO.findWordsByLemmaAndPos(word1, pos);
		List<Sense> senses = SenseDAO.findSensesByWordid( words.get(0).getWordid() );
		List<Concept> synsetStrings = new ArrayList<Concept>(senses.size());	
		//String synsetId = senses.get(0).getSynset();
		//Synset synset1 = SynsetDAO.findSynsetBySynset( synsetId );
		//SynsetDef synsetDef = SynsetDefDAO.findSynsetDefBySynsetAndLang(synsetId, Lang.eng);
		//List<Synlink> synlinks = SynlinkDAO.findSynlinksBySynset( synsetId );
	
		// Showing the result

		System.out.println( words.get(0) );

		System.out.println( senses.get(0) );

		//System.out.println( synset1 );

		//System.out.println( synsetDef );

		//System.out.println( synlinks.get(0) );
		
		/*for (Synset synset : synsets ) {
			System.out.println(synset);
			synsetStrings.add(new Concept(synset.getSynset(), POS.valueOf(pos.toString())));
			
		}*/
		String synsetId;
		Synset synset1 ;
		for(Sense sense: senses){
			synsetId = sense.getSynset();
			synset1 = SynsetDAO.findSynsetBySynset( synsetId );
			System.out.println( synset1 );
			synsetStrings.add(new Concept(synsetId, POS.valueOf(pos.toString())));
		}
		
			//The depth of the word indicating the desired criterion (d1)
			int depth1;
		    Concept con=synsetStrings.get(0);
		    depth1 = depthfinder.getShortestDepth( con);
		    System.out.println("depth1:"+ depth1);
		    
		    //LCS possiblities
		    List<Integer> LCSList=new ArrayList<Integer>();
		    int lowerBound_LCS= (int) Math.ceil((double)2*depth1/3);
		    int upperBound_LCS=depth1+1;
		    for(int i=lowerBound_LCS; i<upperBound_LCS;i++){
		    	LCSList.add(i);		   
		    }
		    
		    //depth2 (d2) possiblities
		    List<Integer> d2List=new ArrayList<Integer>();
		    int upperBound_d2;
		    int lowerBound_d2;
		    
		    for(int LCS:LCSList){
		    	lowerBound_d2=LCS;
		    	upperBound_d2=(int) Math.floor((double) 2.5*LCS - depth1) +1;
		    	for(int i=lowerBound_d2; i<upperBound_d2;i++){
			    	d2List.add(i);		   
			    }
		    	System.out.println("For LCS= "+LCS+" d2 possiblities are: "+d2List);
		    	d2List.clear();
		    	}
		    
		   
			
			
			//get the HyperTree of the word indicating the desired criterion (word1)
			Set<String> history = new HashSet<String>();
			List<List<String>> hyperTree1 = pathfinder.getHypernymTrees(senses.get(0).getSynset(), history);
			for(List<String> availablePath: hyperTree1){
				
					System.out.println(availablePath);	
				
				
			}
			
			
			
			
			Set<String> hyponyms = JAWJAW.findHyponyms(word1, pos);
			Set<String> nextGeneration = new HashSet<String>();
			nextGeneration=nextGenerationRetriever(hyponyms,pos);
			
			/*Set<String> setOfRelevantTerms = new HashSet<String>();
			//starting from Lowest Common Subsummer to be the last term on the tree of word1 (criterion indicator)
			double LCS_depth;
			int upperbound=1;
			int lowerbound=0;
			
			LCS_depth=depth1;
			while (upperbound-lowerbound >= 0){
				
				upperbound= (int) ((int) (2/criterionSimilarityDegree)*LCS_depth - depth1);
				lowerbound=(int)LCS_depth;
				
			}*/
			
			System.out.println( "The number of 1st generation is: \t"+ hyponyms.size()+"\n and the children(hyponyms) of "+word1+" are: \t"+ hyponyms );
			System.out.println( "The number of the 2nd generation is: \t"+ nextGeneration.size()+"\n The 2nd generation are: \t"+ nextGeneration );
			hyponyms.addAll(nextGeneration);
			
			
		
	}
		
	

	public static Set<String> nextGenerationRetriever(Set<String> currentGeneration, POS pos){
		
		Set<String> nextGeneration = new HashSet<String>();
		
		for(String term: currentGeneration){
				try{
				Set<String> secondGenerationOfTerm = JAWJAW.findHyponyms(term, pos);
				for(String newTerm: secondGenerationOfTerm){
					nextGeneration.add(newTerm);
				}
				
				}
				catch (Exception e){System.out.println("unmatching POS");}
			}
		
		
		return nextGeneration;
		
	}
	
	
	
		
}
