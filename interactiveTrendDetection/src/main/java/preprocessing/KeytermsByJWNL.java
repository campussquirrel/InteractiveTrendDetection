package preprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.lexical_db.data.Concept;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.list.PointerTargetNode; 
import net.didion.jwnl.dictionary.Dictionary;


public class KeytermsByJWNL {
	public static void main(String[] args) throws Exception{
		 
		 //JWNL.initialize(); 
		//demonstrateTreeOperation(indexWord);
		JWNL.initialize(new FileInputStream("properties.xml"));    	
		final Dictionary dictionary = Dictionary.getInstance();
		
		IndexWord indexWord = dictionary.getIndexWord(POS.NOUN, "material");

		//demonstrateTreeOperation(indexWord);
		System.out.println("The hypernym Tree is : ");
		demonstrateHyperTreeOperation(indexWord);
		
		//Synset[] senses = indexWord.getSenses();

		/*	for (Synset sense : senses) {
				int dept=2;
				System.out.println(indexWord + ": " + sense.getGloss());
			}*/
		
	}
	
	//The depth of the word indicating the desired criterion (d1)
	int depth1;
	
   /* Concept con=synsetStrings.get(0);
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
    	}*/
    
   
	


	//testing getting hyponyms using JWNL
		public static void demonstrateTreeOperation(IndexWord word) throws JWNLException {
			// Get all the hyponyms (children) of the first sense of <var>word</var>
			PointerTargetTree hyponyms = PointerUtils.getInstance().getHyponymTree(word.getSense(1));
			System.out.println("Hyponyms of \"" + word.getLemma() + "\":");
			hyponyms.print();
		}
		
		// get 'hyponym' synsets 
		public static Synset[] getHyponymSynsets(net.didion.jwnl.data.Synset synset) {
			PointerTargetNodeList hyponyms = null;
			try {
				hyponyms = PointerUtils.getInstance().getDirectHyponyms(synset);
			} catch (JWNLException e) {}
			if (hyponyms == null) return null;
			
			return getSynsets(hyponyms);
		}
		
		//testing on hypernymTree
		//testing getting hyponyms using JWNL
				public static void demonstrateHyperTreeOperation(IndexWord word) throws JWNLException {
					// Get all the hyponyms (children) of the first sense of <var>word</var>
					PointerTargetTree hypernyms = PointerUtils.getInstance().getHypernymTree(word.getSense(1));
					System.out.println("Hyponyms of \"" + word.getLemma() + "\":");
					hypernyms.print();
				}
		
		
		/**
		  * Looks up the synsets that correspond to the nodes in a node list. 
		  *  
		  * @param nodes node list 
		  * @return synsets 
		  */ 		 
		public static Synset[] getSynsets(PointerTargetNodeList nodes) { 
		  Synset[] synsets = new Synset[nodes.size()]; 
		   
		  for (int i = 0; i < nodes.size(); i++) { 
		   PointerTargetNode node  = (PointerTargetNode) nodes.get(i); 
		   synsets[i] = node.getSynset(); 
		  } 
		   
		  return synsets; 
		 } 

}
	
