/*
 * Article Table
 * Anurag Prasad 
 * Spring 2015
 * This program stores articles in a hash table using a separate chaining technique 
 */

public class ArticleTable {
  
  private static final int SIZE = 2503;          // article hash table size (prime #)
  private static Node [] H = new Node[SIZE];     // array representing hash table
  
  private static Node head = null;               // pointer to head node in the global LL of all nodes
  private static Node ptr = head;                // pointer to next node in the global LL of all nodes
  
  // Node which represents a "bucket" in hash table
  public static class Node {
    String title;            // title serves as key to insert into the table
    Article a;               // title's corresponding article 
    Node next;               // pointer to next node in particular bucket 
    Node next2;              // pointer to global list of all nodes 
    
    // Node constructor
    public Node(String t, Article a, Node n1, Node n2) {
      title = t;
      this.a = a;
      next = n1;
      this.next2 = n2; 
    }
  }
  
  // insert articles into table  
  public void initialize(Article[] A) {
      for(int i = 0; i < A.length; ++i) 
         insert(A[i]); 
   }
  
  // Helper Methods 
  
  // member global: check if article is in the table using article 
  boolean member(Article a) {
    return (lookup(a.getTitle()) != null);      
  }
  
  // member buckets: check if article with same title is in bucket 
  boolean member(String t) {
   return (lookup(t) != null); 
  }
  
  // hash: take a title key, perform hash, and return a
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
  
  
  // Interface Methods
  
  // insert article a into the table using the title of a as the hash key
  public void insert(Article a) {  
    H[hash(a.getTitle())] = insertHelper(a, H[hash(a.getTitle())]);  
  }
  
  private Node insertHelper(Article a, Node p) {     // wrapper method for insert 
    if(p == null) {  
      head = new Node(a.getTitle(), a, null, head);  // if empty LL, create new article Node    
      return head; 
    }
    else if(a.getTitle().equals(p.title))            // if there is duplicate, just return the Node
      return p; 
    else {
      p.next = insertHelper(a, p.next);              // go through LL until insertion possible
      return p; 
    }
  }
  
  // deletes node with specified title  
  public void delete(String t) {
    H[hash(t)] = deleteHelper(t, H[hash(t)]); 
  }
  
  private Node deleteHelper(String t, Node p) {       // wrapper method for delete 
    if(p == null)
      return p;                                       // if nothing to delete, just return the empty LL
    else if(p.title.equals(t)) {
      head = deleteHelperGlobal(t, head);             // also delete article from global LL     
      return p.next;                                  // reroute LL to next node in bucket  
    }
    else {
      p.next = deleteHelper(t, p.next);               // proceed to check if deletion necessary in next node
      return p; 
    }
 
  }
  
  // deletes node from the global LL
  private Node deleteHelperGlobal(String t, Node p) {
    if(p == null)
      return p;
    else if(p.title.equals(t)) {
      return p.next2;                                // reroute LL to next global node
    }
    else {
     p.next2 = deleteHelperGlobal(t, p.next2);       // proceed to check if deletion necessary in next node
     return p;
    }
  }
  
  // return the article with the given title or null if not found
  public Article lookup(String t) {     
    return lookupHelper(t, H[hash(t)]); 
  }
  
  private Article lookupHelper(String t, Node p) {  // wrapper method for lookup 
    if(p == null)
      return null;                                  // null node, did not find
    else if(p.title.equalsIgnoreCase(t))            // found title, so return the article
     return p.a;
    else
      return lookupHelper(t, p.next);
  }

  
  // Hash Table Iterator Methods
  
  public void reset() {      // initialize the iterator                    
    ptr = head;              // set pointer back to head 
  }
  
  // check if the next node in global LL is not null 
  public boolean hasNext() {
    return (ptr != null); 
  }
  
  // return next article in global LL of all nodes 
  public Article next() {
    Article next_a = ptr.a;  // store next node's article in global LL
    ptr = ptr.next2;         
    return next_a;           
  } 
  
  
 
// ArticleTable unit test 
public static void main(String [] args) {
  
  ArticleTable T = new ArticleTable();
  
  // ZenHabits.com articles 
  Article breathe = new Article("Breathe", "If you feel stressed out and overwhelmed, breathe. It will calm you and release the tensions.");
  Article still = new Article("Be Still", "Be still. Just for a moment. Listen to the world around you. Feel your breath coming in and going out. Listen to your thoughts. See the details of your surroundings.");
  Article life = new Article("A Brief Guide to Life", "Smile, breathe, and go slowly. If you live your life by those five words, youÕll do pretty well.");
  Article goal = new Article("The Best Goal is No Goal", "HereÕs why: you are extremely limited in your actions. When you donÕt feel like doing something, you have to force yourself to do it. Your path is chosen, so you donÕt have room to explore new territory. You have to follow the plan, even when youÕre passionate about something else.");
  Article solitude = new Article("The Lost Art of Solitude", "Solitude is a lost art in these days of ultra-connectedness, and while I donÕt bemoan the beauty of this global community, I do think thereÕs a need to step back from it on a regular basis.");
  
  // test article insertion
  T.insert(breathe);
  T.insert(still);
  T.insert(life);
  T.insert(goal);
  T.insert(solitude);
  T.reset();
  
  System.out.println("Testing insert...");
  System.out.println("[1] Should print the five articles: The Lost Art of Solitude, The Best Goal is No Goal, A Brief Guide to Life, Be Still, Breathe\n");
  
  while(T.hasNext()) {
    Article article = T.next();
    System.out.println(article);
  }
  
  // test article lookup 
  System.out.println("Testing lookup...\n");
  
  System.out.println("[2] Should print Breathe article...");
  System.out.println(T.lookup("Breathe"));
  System.out.println("[3] Should print null...");
  System.out.println(T.lookup("Zen")); // this title is not in table
  System.out.println("\n");
  System.out.println("[4] Should print Be Still article...");
  System.out.println(T.lookup("Be Still"));
  
  // test article deletion
  System.out.println("Testing delete...\n");
  T.delete("Be Still");
  T.delete("The Best Goal is No Goal");
  T.reset();
  
  System.out.println("[5] Should print the three reamining articles: The Lost Art of Solitude, A Brief Guide to Life, Breathe");

  while(T.hasNext()) {
   Article article = T.next();
   System.out.println(article);
  }
  
  
}

}






