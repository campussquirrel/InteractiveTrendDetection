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
import edu.cmu.lti.jawjaw.db.SynlinkDAO;
import edu.cmu.lti.jawjaw.db.SynsetDefDAO;
import edu.cmu.lti.jawjaw.db.SenseDAO;




public class KeyTermsFinder {
	
	private static ILexicalDatabase db = new NictWordNet();
	
	private static double compute(String word1, String word2) {
		WS4JConfiguration.getInstance().setMFS(true);
		double s = new WuPalmer(db).calcRelatednessOfWords(word1, word2);
		return s;
	}
 
	public static void main(String[] args) {
		
		
		String word1="use";
		DepthFinder depthfinder=new DepthFinder(db);
		PathFinder pathfinder=new PathFinder(db);
		POS pos = POS.valueOf("n");
		double criterionSimilarityDegree=0.9;
		
		List<Word> words = WordDAO.findWordsByLemmaAndPos(word1, pos);
		List<Sense> senses = SenseDAO.findSensesByWordid( words.get(0).getWordid() );
		List<Concept> synsetStrings = new ArrayList<Concept>(senses.size());	
		String synsetId = senses.get(0).getSynset();
		Synset synset1 = SynsetDAO.findSynsetBySynset( synsetId );
		SynsetDef synsetDef;// = SynsetDefDAO.findSynsetDefBySynsetAndLang(synsetId, Lang.eng);
		//List<Synlink> synlinks = SynlinkDAO.findSynlinksBySynset( synsetId );
		String word2="entity";
		List<Word> words2 = WordDAO.findWordsByLemmaAndPos(word2, pos);
		
		for(Sense sense: senses){
			synsetId = sense.getSynset();
			synset1 = SynsetDAO.findSynsetBySynset( synsetId );
			synsetDef = SynsetDefDAO.findSynsetDefBySynsetAndLang(synsetId, Lang.eng);
			System.out.println( synset1 );
			System.out.println( synsetDef );
			synsetStrings.add(new Concept(synsetId, POS.valueOf(pos.toString())));
			
		}
		
		
	    Concept con=synsetStrings.get(3);
		//get the HyperTree of the word indicating the desired criterion (word1)
		Set<String> history = new HashSet<String>();
		List<List<String>> hyperTree1 = pathfinder.getHypernymTrees(con.getSynset(), history);
		
		Set<String> d2ListOfSynsets=new HashSet<String>();
		d2ListOfSynsets=getd2ListOfSynsets(hyperTree1,criterionSimilarityDegree);
		System.out.println("d2 possibilities: "+d2ListOfSynsets+"\n"+d2ListOfSynsets.size());
		System.out.println("lemma list is:\n "+getLemmasfromSynsets(d2ListOfSynsets));
		
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
	
/**
* next generation i.e. the children (as hyponyms) of a particular node are listed by this method.
	 * 
	 * @param synsetID synset
	 * @return listofChildren list of synsets of the corresponding hyponyms 
	 */

public static Set<String> nextGenerationSynsets(Set<String> tempListOfSynsets){
		
		Set<String> nextGeneration = new HashSet<String>();// = new HashSet<String>();
		Traverser Trav=new Traverser();
		for(String synsetID:tempListOfSynsets){
		Set<String> listofChildren=new HashSet<String>();
		 listofChildren=Trav.getDownwardSynsets(synsetID); 
		nextGeneration.addAll(listofChildren);
		}

		return nextGeneration;
		
	}
/**
* returns lemmas of each of the synsets in the list
 * 
 * @param listOfsynsets Set<String>
 * @return listOflemmas Set<String> (could be longer than the given set- multiple lemmas for one synset)
 */

public static Set<String> getLemmasfromSynsets(Set<String> listOfsynsets){
	
	Set<String> listOflemmas = new HashSet<String>();;
	WordNetUtil wnu=null;
	
	for(String ChildID: listOfsynsets){
		List<Word> wordsList=wnu.synsetToWords(ChildID);
		for(Word word: wordsList){
			//System.out.println(word.getLang());
			if(word.getLang()==Lang.eng){
				listOflemmas.add(word.getLemma());}
		}

   }
	return listOflemmas;
}
/**
* returns the sysnsetID of each of the possible d2s 
 * given the criterion similarity degree and the hypertrees of a specific sense of the criterion term
 * 
 * @param hyperTreeOfd1 List<List<String>
 * @param csdegree double
 * @return d2ListOfSynsets Set<String> 
 */
public static Set<String> getd2ListOfSynsets(List<List<String>> hyperTreeOfd1,double csdegree){
	Set<String> d2ListOfSynsets=new HashSet<String>();
	int depth1;
	//for each path from the criterion term(d1) to the ROOT (all possible HyperTrees)
	for(List<String> availablePath: hyperTreeOfd1){
		
			depth1 = availablePath.size();
		   
		  //calculating LCS possiblities
		    
		    int lowerBound_LCS= (int) Math.ceil((double)(csdegree/(2-csdegree))*depth1);
		    int upperBound_LCS=depth1+1;
		    
		    //for each LCS, the depth of the potential terms(d2) is being calculated 
		    for(int i=lowerBound_LCS; i<upperBound_LCS;i++){		   			    
		    int upperBound_d2;
		    int lowerBound_d2;
		  
		    	Set<String> TempListOfSynsets=new HashSet<String>();
		    	lowerBound_d2=i;
		    	upperBound_d2=(int) Math.floor((double) (2/csdegree)*i - depth1) +1;
		    	
		    	
		    	for(int j=lowerBound_d2; j<upperBound_d2;j++){
		    		if(j==i){
		    			String synIDatLCS=availablePath.get(i-1);
		    			d2ListOfSynsets.add(synIDatLCS);
		    			TempListOfSynsets.add(synIDatLCS);
		    		}else{
		    		
		    			
		    		d2ListOfSynsets.addAll(nextGenerationSynsets(TempListOfSynsets));
		    		
		    		if(upperBound_d2-i==1) break;
		    		TempListOfSynsets=new HashSet<String>(nextGenerationSynsets(TempListOfSynsets));
		    		}	 
			    }	
		    }
	}
	
	return d2ListOfSynsets;
}
		
}
