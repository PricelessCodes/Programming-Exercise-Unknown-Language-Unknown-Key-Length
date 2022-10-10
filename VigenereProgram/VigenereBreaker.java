import java.util.*;
import edu.duke.*;
import java.io.*;

public class VigenereBreaker {
    public String sliceString(String message, int whichSlice, int totalSlices)
    {
        StringBuilder sb = new StringBuilder();
        
        for (int i = whichSlice; i < message.length(); i += totalSlices)
        {
            sb.append(message.charAt(i));
        }
        
        return sb.toString();
    }

    public int[] tryKeyLength(String encrypted, int klength, char mostCommon) {
        int[] key = new int[klength];
        
        for (int i = 0; i < klength; i++)
        {
            String message = sliceString(encrypted, i, klength);
            
            CaesarCracker cc = new CaesarCracker();
            
            int k = cc.getKey(message);
            
            key[i] = k;
        }
        
        return key;
    }
    
    public HashSet<String> readDictionary(FileResource fr)
    {
        HashSet<String> words = new HashSet<String>();
        
        for (String word : fr.lines())
        {
            word = word.toLowerCase();
            
            if (!words.contains(word))
            {
                words.add(word);
            }
        }
        
        return words;
    }
    
    public int countWords(String message, HashSet<String> words)
    {
        
        String[] messageWords = message.split("\\W+");
        
        int counter = 0;
        
        for (int i = 0; i < messageWords.length; i++)
        {
            String word = messageWords[i].toLowerCase();
            
            if (words.contains(word))
            {
                counter++;
            }
        }
        
        return counter;
    }
    
    public String breakForLanguage(String encrypted, HashSet<String> words) {
        
        char mostCommenLetter = mostCommonCharIn(words);
        int max = 0;
        int[] key = null;
        VigenereCipher vc = null;
        String decryptedMessage = "";
        
        for (int i = 1; i <= 100; i++)
        {
            int[] currentKey = tryKeyLength(encrypted, i, mostCommenLetter);
            
            vc = new VigenereCipher(currentKey);
            
            decryptedMessage =  vc.decrypt(encrypted);
            
            int count = countWords(decryptedMessage, words);
            
            if (count > max)
            {
                max = count;
                key = currentKey;
            }
        }
        
        if (key != null)
        {
            vc = new VigenereCipher(key);
            decryptedMessage =  vc.decrypt(encrypted);
        }
        System.out.print("key is: ");
        for (int i = 0; i < key.length; i++)
        {
            System.out.print(key[i] + " ");
        }
        System.out.println("");
        
        System.out.println("This file contains " + max + " valid words out of " + encrypted.split("\\W+").length);
        return decryptedMessage;
    }
    
    public char mostCommonCharIn (HashSet<String> words){
        
        String wordsAsString = words.toString();
        
        CaesarCracker cc = new CaesarCracker();
        
        int[] counts = cc.countLetters(wordsAsString);
        
        int maxIndex = cc.maxIndex(counts);
        
        char mostCommonChar = (char)(maxIndex + 'a');
        
        return mostCommonChar;
    }
    
    public void breakForAllLangs(String encrypted, HashMap<String, HashSet<String>> languages)
    {
        
        for (String language : languages.keySet())
        {
            System.out.println("the language used is: " + language);
            
            HashSet<String> words = languages.get(language);
            
            String decryptedMessage =  breakForLanguage(encrypted, words);
            
            System.out.println("");
            System.out.println(decryptedMessage);
            System.out.println("");
        }
    }

    public void breakVigenere () {
        
        FileResource fr = new FileResource();
        
        String message = fr.asString();
        
        DirectoryResource dr = new DirectoryResource();
        
        HashMap<String, HashSet<String>> languages = new HashMap<String, HashSet<String>>();
        
        for (File language : dr.selectedFiles())
        {
            fr = new FileResource(language);
            
            HashSet<String> words = readDictionary(fr);
            
            languages.put(language.getName(), words);
            System.out.println("reading " + language.getName() + " is done.");
        }
        
        breakForAllLangs(message, languages);
    }
    
}
