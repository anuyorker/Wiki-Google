/*
 * Wiki-Google
 * Anurag Prasad
 * Spring 2015
 * This program allows searching the text of an article using keywords and Cosine Similarity.
 * Accessory Files: MaxHeap.java, TermFrequencyTable.java 
 */

import java.util.*;
import java.util.StringTokenizer;
import java.lang.Character;


public class MiniGoogle {
  
  private static Article[] getArticleList(DatabaseIterator db) {
    
    // count how many articles are in the directory
    int count = db.getNumArticles(); 
    
    // now create array
    Article[] list = new Article[count];
    for(int i = 0; i < count; ++i)
      list[i] = db.next();
    
    return list; 
  }
  
  private static DatabaseIterator setupDatabase(String path) {
    return new DatabaseIterator(path);
  }
  
  private static void addArticle(Scanner s, ArticleTable A) {
    System.out.println();
    System.out.println("Add an article");
    System.out.println("==============");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    System.out.println("You may now enter the body of the article.");
    System.out.println("Press return two times when you are done.");
    
    String body = "";
    String line = "";
    do {
      line = s.nextLine();
      body += line + "\n";
    } while (!line.equals(""));
    
    A.insert(new Article(title, body));
  }
  
  
  private static void removeArticle(Scanner s, ArticleTable A) {
    System.out.println();
    System.out.println("Remove an article");
    System.out.println("=================");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    
    A.delete(title);
  }
  
  
  private static void titleSearch(Scanner s, ArticleTable A) {
    System.out.println();
    System.out.println("Search by article title");
    System.out.println("=======================");
    
    System.out.print("Enter article title: ");
    String title = s.nextLine();
    
    Article a = A.lookup(title);
    if(a != null)
      System.out.println(a);
    else {
      System.out.println("Article not found!"); 
      return; 
    }
    
    System.out.println("Press return when finished reading.");
    s.nextLine();
  }
  
  // Take a string, turn it into all lower case, and remove
  // all characters except for letters, digits, and whitespace
  private static String preprocess(String s) {
    String lowercase = s.toLowerCase();
    String cleanString = ""; 
    
    for(int i = 0; i < lowercase.length(); ++i) {
      Character stringChar = lowercase.charAt(i); 
      if(Character.isLetter(stringChar) || Character.isDigit(stringChar) || Character.isWhitespace(stringChar))
        cleanString += stringChar; 
    }
    
    return cleanString;
  }
  
  // List of words not to be considered when comparing strings
  private static final String [] blackList = { "the", "of", "and", "a", "to", "in", "is", 
    "you", "that", "it", "he", "was", "for", "on", "are", "as", "with", 
    "his", "they", "i", "at", "be", "this", "have", "from", "or", "one", 
    "had", "by", "word", "but", "not", "what", "all", "were", "we", "when", 
    "your", "can", "said", "there", "use", "an", "each", "which", "she", 
    "do", "how", "their", "if", "will", "up", "other", "about", "out", "many", 
    "then", "them", "these", "so", "some", "her", "would", "make", "like", 
    "him", "into", "time", "has", "look", "two", "more", "write", "go", "see", 
    "number", "no", "way", "could", "people",  "my", "than", "first", "water", 
    "been", "call", "who", "oil", "its", "now", "find", "long", "down", "day", 
    "did", "get", "come", "made", "may", "part" }; 
  
  // Determine if sthe string s is a member of the blacklist
  private static boolean blacklisted(String s) {
    for(int i = 0; i < blackList.length; ++i) {
      if(s.equals(blackList[i]))  
        return true;                    // s is one of the blacklist terms 
    }
    return false;                       // s is none of blacklist terms
  }
  
  
  // Extract the cosine similarity of strings and return it
  private static double getCosineSimilarity(String s, String t) { 
    
  // Take the two strings and preprocess each
    String pps = preprocess(s);                    // preprocess first string s
    String ppt = preprocess(t);                    // preprocess second string t

  // Use StringTokenizer class to extract each of the strings
    StringTokenizer tokenize_s = new StringTokenizer(pps);
    StringTokenizer tokenize_t = new StringTokenizer(ppt); 
    
  // Create TermFrequencyTable and insert all unblacklisted terms into table
  // with its respective docNum (String s for doc 0, String t for doc 1)
    TermFrequencyTable T = new TermFrequencyTable();
    
    // insert terms from s
    T.HTreset(); 
    while(tokenize_s.hasMoreTokens()) {
      String ts = tokenize_s.nextToken();
      if(!blacklisted(ts))     // if not in blacklist insert the first 
        T.insert(ts, 0);       // token into the table (docNum 0)  
    }
    
    // insert terms from t 
    while(tokenize_t.hasMoreTokens()) { 
      String tt = tokenize_t.nextToken();
      if(!blacklisted(tt))     // if not in blacklist, insert the first
        T.insert(tt, 1);       // token into the table (docNum 1)
    }
    
    return T.cosineSimilarity();                   // return the cosine similarity of the table
  }
  
  // Take an ArticleTable and search it for articles most similar to 
  // the phrase; return a string response that includes the top three
  public static String phraseSearch(String phrase, ArticleTable T) {
    String output = "";
    
    // MaxHeap for search terms 
    MaxHeap searchHeap = new MaxHeap(); 
    
    // iterate through the table and insert articles in 
    // MaxHeap with cosine similarity > 0.001
    T.reset();
    while(T.hasNext()) { 
      Article art = T.next();
      String content = art.getBody();
      double artCosSim = getCosineSimilarity(phrase, content);
      if(artCosSim > 0.001)
        searchHeap.insert(new MaxHeap.Node(art, artCosSim));
    }
    
    if(searchHeap.isEmpty())
      return "\nNo matching articles found!";
    else {
      output += "Top 3 Matches:\n";
      
      for(int i = 0; i < 3 & !searchHeap.isEmpty(); ++i) {       // get top 3 most similar articles
        MaxHeap.Node topArt = searchHeap.getMax();
        
        if(topArt != null) {                                     // print the top articles
          output += "Match " + ((int)i+1) + " with cosine similarity of " + topArt.sim + ":\n\n" + topArt.a;
        }
      }
      return output;
    }
  }
  
  // User interface 
  public static void main(String[] args) {
    Scanner user = new Scanner(System.in);
    
    String dbPath = "articles/";
    
    DatabaseIterator db = setupDatabase(dbPath);
    
    System.out.println("Read " + db.getNumArticles() + 
                       " articles from disk.");
    
    ArticleTable L = new ArticleTable(); 
    Article[] A = getArticleList(db);
    L.initialize(A);
    
    int choice = -1;
    do {
      System.out.println();
      System.out.println("Welcome to Mini-Google!");
      System.out.println("=====================");
      System.out.println("Make a selection from the " +
                         "following options:");
      System.out.println();
      System.out.println("Manipulating the database");
      System.out.println("-------------------------");
      System.out.println("    1. add a new article");
      System.out.println("    2. remove an article");
      System.out.println("    3. search by exact article title");
      System.out.println("    4. search by phrase (list of keywords)");
      System.out.println();
      
      System.out.print("Enter a selection (1-4, or 0 to quit): ");
      
      choice = user.nextInt();
      user.nextLine();
      
      switch (choice) {
        case 0:
          System.out.println("Bye!"); 
          return;
          
        case 1:
          addArticle(user, L);
          break;
          
        case 2:
          removeArticle(user, L);
          break;
          
        case 3:
          titleSearch(user, L);
          break;
          
        case 4:
          System.out.println();
          System.out.println("Search by article content");
          System.out.println("=======================");
    
          System.out.print("Enter search phrase: ");
          String inputPhrase = user.nextLine();
    
          System.out.println(phraseSearch(inputPhrase, L));
          break;
          
        default:
          break;
      }
      
      choice = -1;
      
    } while (choice < 0 || choice > 4);
    
  }
  
  
}
