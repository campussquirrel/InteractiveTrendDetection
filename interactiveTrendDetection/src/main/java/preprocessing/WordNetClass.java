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
import edu.cmu.lti.jawjaw.pobj.POS;
import edu.cmu.lti.jawjaw.util.WordNetUtil;
import edu.cmu.lti.jawjaw.JAWJAW;


public class WordNetClass {
	
	private static ILexicalDatabase db = new NictWordNet();
	
	private static double compute(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
 
	public static void main(String[] args) {
		
		
		String word1="material";
		DepthFinder depthfinder=new DepthFinder(db);
		PathFinder pathfinder=new PathFinder(db);
		int depth1;
		POS pos = POS.valueOf("n");
		List<edu.cmu.lti.jawjaw.pobj.Synset> synsets = WordNetUtil.wordToSynsets(word1, pos);
		List<Concept> synsetStrings = new ArrayList<Concept>(synsets.size());
		double criterionSimilarityDegree=0.8;
		
		for ( edu.cmu.lti.jawjaw.pobj.Synset synset : synsets ) {
			synsetStrings.add(new Concept(synset.getSynset(), POS.valueOf(pos.toString())));
			
		}
		
			//computing the depth of the word indicating the desired criterion 
		    Concept synset=synsetStrings.get(0);
		    depth1 = depthfinder.getShortestDepth( synset);
			
			Set<String> hyponyms = JAWJAW.findHyponyms(word1, pos);
			
			
			Set<String> history = new HashSet<String>();
			List<List<String>> paths = pathfinder.getHypernymTrees(word1, history);
			
			
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
