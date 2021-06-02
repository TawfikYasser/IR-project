import java.io.*;
import java.util.*;

class DictEntry2 {

	public int doc_freq = 0; // number of documents that contain the term
	public int term_freq = 0; // number of times the term is mentioned in the collection
	public HashSet<Integer> postingList;

	DictEntry2() {
		postingList = new HashSet<Integer>();
	}
}

public class InvertedIndex {

	// --------------------------------------------
	Map<Integer, String> sources; // store the doc_id and the file name
	HashMap<String, DictEntry2> index; // THe inverted index
	// --------------------------------------------

	InvertedIndex() {
		sources = new HashMap<Integer, String>();
		index = new HashMap<String, DictEntry2>();
	}

	public static HashMap<String, Integer> sortByValue(HashMap<String, Integer> hm) {
		// Create a list from elements of HashMap
		List<Map.Entry<String, Integer>> list = new LinkedList<Map.Entry<String, Integer>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<String, Integer> temp = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> aa : list) {
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	// ---------------------------------------------
	public void printDictionary() {
		Iterator it = index.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			DictEntry2 dd = (DictEntry2) pair.getValue();
			HashSet<Integer> hset = dd.postingList;// (HashSet<Integer>) pair.getValue();
			System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
			Iterator<Integer> it2 = hset.iterator();
			while (it2.hasNext()) {
				System.out.print(it2.next() + ", ");
			}
			System.out.println("");
			// it.remove(); // avoids a ConcurrentModificationException
		}
		System.out.println("------------------------------------------------------");
		System.out.println("*** Number of terms = " + index.size());
	}

	// -----------------------------------------------
	public void buildIndex(String[] files) {
		int i = 0;
		for (String fileName : files) {
			try (BufferedReader file = new BufferedReader(new FileReader(fileName))) {
				sources.put(i, fileName);
				String ln;
				while ((ln = file.readLine()) != null) {
					String[] words = ln.split("\\W+");
					for (String word : words) {
						word = word.toLowerCase();
						// check to see if the word is not in the dictionary
						if (!index.containsKey(word)) {
							index.put(word, new DictEntry2());
						}
						// add document id to the posting list
						if (!index.get(word).postingList.contains(i)) {
							index.get(word).doc_freq += 1; // set doc freq to the number of doc that contain the term
							index.get(word).postingList.add(i); // add the posting to the posting:ist
						}
						// set the term_fteq in the collection
						index.get(word).term_freq += 1;
					}
				}
				printDictionary();
			} catch (IOException e) {
				System.out.println("File " + fileName + " not found. Skip it");
			}
			i++;
		}
	}

	// --------------------------------------------------------------------------
	// query inverted index
	// takes a string of terms as an argument
	public String find(String phrase) {

		String[] words = phrase.split("\\W+");

		HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);

		for (String word : words) {
			res.retainAll(index.get(word).postingList);
		}
		if (res.size() == 0) {
			System.out.println("Not found");
			return "";
		}
		String result = "Found in: \n";
		for (int num : res) {
			result += "\t" + sources.get(num) + "\n";
		}
		return result;
	}

	// ----------------------------------------------------------------------------
	HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
		HashSet<Integer> answer = new HashSet<Integer>();
		if (pL1.isEmpty() || pL2.isEmpty())
			return answer;

		for (Integer l1 : pL1) {
			for (Integer l2 : pL2) {
				if (l1 == l2) {
					answer.add(l1);
					break;
				}
			}
		}

		return answer;
	}
	// -----------------------------------------------------------------------

	public String find_01(String phrase) {
		String result = "";
		String[] words = phrase.split("\\W+");
		// 1- get first posting list
		HashSet<Integer> pL1 = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
		// 2- get second posting list
		HashSet<Integer> pL2 = new HashSet<Integer>(index.get(words[1].toLowerCase()).postingList);
		// 3- apply the algorithm
		HashSet<Integer> answer = intersect(pL1, pL2);
		System.out.println("Found in: ");
		for (int num : answer) {
			result += "\t" + sources.get(num) + "\n";
		}
		return result;
	}
	// -----------------------------------------------------------------------

	public String find_02(String phrase) {
		String result = "";
		if (phrase == "")
			return result;

		String[] words = phrase.split("\\W+");
		HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
		for (int i = 1; i < words.length; i++) {
			res = intersect(res, index.get(words[i].toLowerCase()).postingList);
		}
		System.out.println("Found in: ");
		for (int id : res) {
			result += "\t" + sources.get(id) + "\n";
		}
		return result;

	}
	// -----------------------------------------------------------------------

	public String find_03(String phrase) {
		String result = "";
		if (phrase == "")
			return result;

		String[] words = phrase.split("\\W+");
		HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
		for (int i = 1; i < words.length; i++) {
			res = intersect(res, index.get(words[i].toLowerCase()).postingList);
		}
		System.out.println("Found in: ");
		for (int id : res) {
			result += "\t" + sources.get(id) + "\n";
		}
		return result;

	}
	// -----------------------------------------------------------------------

	public String find_04(String phrase) { // optimized search
		String result = "";
		if (phrase == "")
			return result;

		String[] words = phrase.split("\\W+");
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		for (String word : words) {
			map.put(word, (index.get(word.toLowerCase()).postingList).size());
		}

		Map<String, Integer> sortedMap = sortByValue(map);
		HashSet<Integer> res = new HashSet<Integer>();
		for (String key : sortedMap.keySet()) {
			res = new HashSet<Integer>(index.get(key.toLowerCase()).postingList);
			break;
		}

		for (Object key : sortedMap.keySet()) {
			res = intersect(index.get(key.toString().toLowerCase()).postingList, res);
		}

		System.out.println("Found in: ");
		for (int id : res) {
			result += "\t" + sources.get(id) + "\n";
		}
		return result;
	}

	// -----------------------------------------------------------------------
	public void compare(String phrase) {
		long iterations = 1000000;
		String result = "";
		long startTime = System.currentTimeMillis();
		for (long i = 1; i < iterations; i++) {
			result = find(phrase);
			System.out.println("result = " + result);

		}
		System.out.println(" find : result = " + result);
		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(" find: (*) elapsed = " + estimatedTime + " ms.");

		// ***********************************************************************************

		System.out.println("*****************************************************************");

		startTime = System.currentTimeMillis();
		for (long i = 1; i < iterations; i++) {
			result = find_01(phrase);
			System.out.println("result = " + result);

		}
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(" find_01 : result = " + result);
		System.out.println(" find_01 : (*) elapsed = " + estimatedTime + " ms.");

		// ************************************************************************************

		System.out.println("*****************************************************************");

		startTime = System.currentTimeMillis();
		for (long i = 1; i < iterations; i++) {
			result = find_02(phrase);
			System.out.println("result = " + result);

		}
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(" find_02 : result = " + result);
		System.out.println(" find_02 : (*) elapsed = " + estimatedTime + " ms.");

		// ***********************************************************************************

		System.out.println("*****************************************************************");

		startTime = System.currentTimeMillis();
		for (long i = 1; i < iterations; i++) {
			result = find_03(phrase);
			System.out.println("result = " + result);

		}
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(" find_03 : result = " + result);
		System.out.println(" find_03 : (*) elapsed = " + estimatedTime + " ms.");

		// ************************************************************************************

		System.out.println("*****************************************************************");

		startTime = System.currentTimeMillis();
		for (long i = 1; i < iterations; i++) {
			result = find_04(phrase);
			System.out.println("result = " + result);

		}
		estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println(" find_04 : result = " + result);
		System.out.println(" find_04 : (*) elapsed = " + estimatedTime + " ms.");

		// ************************************************************************************

	}
}
