package sandbox.duplicate;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

public class Duplicate {
	private static String dupFileName(String filename) {
	    	
			if (filename.isEmpty()) return "";
	    	
	        Random randomGenerator = new Random();
	        List<String> filenameBits = Arrays.asList(filename.split("\\."));
	        
	        filenameBits.set(0, filenameBits.get(0) + "_dup" + randomGenerator.nextInt(100));
	        return StringUtils.join(filenameBits,".");
	}
	
	public static void main(String[] args) {
		String filename = "";
		System.out.println(dupFileName(filename));
	}
}
