/*
 * Term Frequency Table
 * Anurag Prasad
 * Spring 2015
 * This program stores words from two Strings so that the cosine similarity between the two can be calculated. 
 */

public class TermFrequencyTable {
  
  public static final int SIZE = 103;           // term frequency table size (prime # around 100)
  private static Node [] T = new Node[SIZE];    // array representing term freq table
  
  private static Node head = null;              // pointer to head node in the global LL of all nodes
  private static Node ptr = head;               // pointer to next node in the global LL of all nodes
  
  // bucket Node
  private class Node {
    String term;
    int[] termFreq = new int[2];               // gives the term frequency in each of two documents for this term
    Node next;
    Node next2;
    
    // Node constructor
    public Node(String t, int docNum, Node n1, Node n2) {
      this.term = t;
      this.next = n1;
      this.next2 = n2;
      termFreq[docNum] = 1; 
    }
  }
  
  // hash: take a term key, perform hash, and return a
  // number which can be used as an index into the array
  int hash(String t) {
    int sumFromString = 0;
    
    for(int i = 0; i < t.length(); ++i) {
      int char_num = t.charAt(i);
      sumFromString += char_num;
    }
    int index = sumFromString % SIZE;
    
    return index;
  }
  
  // insert a term from a document docNum (=0 for first doc, =1 for second doc) into the table
  public void insert(String term, int docNum) {
    T[hash(term)] = insertHelper(term, docNum, T[hash(term)]);
  }
  
  private Node insertHelper(String term, int docNum, Node p) {  // wrapper method for insert 
    if(p == null) {
      head = new Node(term, docNum, null, head);                // if empty LL, create a new Node for the head 
      return head;                                                                                  
    }
    
    else if(term.equals(p.term)) {                              // if the term is already there, just
      ++p.termFreq[docNum];                                     // increment its termFreq value 
      return p;          
    }
    
    else {
      p.next = insertHelper(term, docNum, p.next);              // go through LL until insertion possible
      return p; 
    }
  }
  
  // return the cosine similarity of the terms for the two documents stored in this table
  double cosineSimilarity() {
    reset(); 
    double SumAtimesB = 0;               // numerator
    double SumAsquared = 0;              // part I of denominator
    double SumBsquared = 0;              // part II of denominator

    while(hasNext()) {
      int[] TF = next();                // retrieve next term's frequency in strings A and B
      
      SumAtimesB += (TF[0] * TF[1]);    // summation (A * B)
      SumAsquared += (TF[0] * TF[0]);   // summation (A^2)
      SumBsquared += (TF[1] * TF[1]);   // summation (B^2)
    }
    
    // formula for cosine similarity is [(A*B) / (sqrt(A^2) * sqrt(B^2)]
    double denominator = Math.sqrt(SumAsquared) * Math.sqrt(SumBsquared);
    double cosSim = SumAtimesB / denominator;
 
    return cosSim;  
  }
  
  // Hash Table Iterator Methods
  
  public void reset() {      // reinitialize the iterator                    
    ptr = head;              // by setting global pointer back to head 
  }
  
  // reset entire hash table
  public void HTreset() {
        T = new Node[SIZE];
        head = null;
    }
  
  // check if the next node in global LL is not null 
  public boolean hasNext() {
    return (ptr != null); 
  }
  
  // return next term's frequency in global LL of all nodes 
  public int[] next() {
    int[] nextTermFreq = ptr.termFreq;  
    ptr = ptr.next2;         
    return nextTermFreq;           
  }
  

// unit test for TermFrequencyTable
  public static void main(String [] args) {
 
    // compare two documents that have the exact same terms (cosine similarity should be 1.0)
    TermFrequencyTable Test = new TermFrequencyTable();
    
    Test.insert("A", 0); 
    Test.insert("B", 0);
    
    Test.insert("A", 1);
    Test.insert("A", 1);
    Test.insert("B", 1);
    Test.insert("B", 1);
    
    System.out.println("Should print out the max cosine similarity 1.0...");
    System.out.println(Test.cosineSimilarity());
    
    Test.HTreset();

    // compare two documents with no common terms (cosine similarity should be 0.0)
    TermFrequencyTable Min = new TermFrequencyTable();

    Test.insert("A", 0);
    Test.insert("B", 0);
    
    Test.insert("C", 1);
    Test.insert("D", 1);

    System.out.println("Should print out the min cosine simlarity 0.0...");
    System.out.println(Min.cosineSimilarity());
    
    Test.HTreset();
    
    // compare two documents wtih moderate similarity (cosine similarity should be between 0 and 1)
    TermFrequencyTable Mid = new TermFrequencyTable();

    Test.insert("CS112", 0);
    Test.insert("HW10", 0);
    
    Test.insert("CS112", 1);
    Test.insert("HW10", 1);
    Test.insert("HW10", 1);

    System.out.println("Should print out the cosine similarity 0.9487.");
    System.out.println(Test.cosineSimilarity());
  
  }
  
  
  }



